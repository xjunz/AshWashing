package com.ashwashing.pro.ui.fragment.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ashwashing.pro.R;
import com.ashwashing.pro.api.UpdateDeviceInfoService;
import com.ashwashing.pro.api.bean.AccountInfo;
import com.ashwashing.pro.api.bean.DeviceInfo;
import com.ashwashing.pro.api.bean.SimpleResult;
import com.ashwashing.pro.impl.bluetooth.BluetoothManager;
import com.ashwashing.pro.impl.bluetooth.BluetoothService;
import com.ashwashing.pro.impl.bluetooth.WaterDevice;
import com.ashwashing.pro.impl.cmd.Analyzer;
import com.ashwashing.pro.impl.cmd.Dispatcher;
import com.ashwashing.pro.impl.cmd.Listener;
import com.ashwashing.pro.util.ApiUtils;
import com.ashwashing.pro.util.UiUtils;

/**
 * Collecting Procedures:
 * show--> press button--> connect--> awaken device--> collect device datum--> post datum--> dismiss
 */
public class
CollectFragment extends DialogFragment implements BluetoothService.Callback, Listener, View.OnClickListener {


    private BluetoothService mBlueService;
    private DeviceInfo mDeviceInfo;
    private UpdateDeviceInfoService mUpdateService;
    private int mChargeMethod, mProductID;
    private Dialog mCollectProgress;
    private AccountInfo mAccountInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_Dialog_Alert_Material_Fragment);
        WaterDevice waterDevice = BluetoothManager.getAwaitingDevice();
        mAccountInfo = AccountInfo.mine();
        if (waterDevice != null) {
            Analyzer.setListener(this);
            mBlueService = new BluetoothService(waterDevice.getBluetoothDevice(), this);
            mDeviceInfo = waterDevice.getInfo();
            mUpdateService = ApiUtils.createGsonRetrofit().create(UpdateDeviceInfoService.class);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_collect_device_info, container, false);
        Button mBtnCollect = root.findViewById(R.id.btn_action);
        mBtnCollect.setOnClickListener(this);
        return root;
    }


    private static final int CODE_DEVICE_NOT_IN_USING = 0;
    private static final int CODE_ILLEGAL_CHARGE_METHOD = 1;
    private static final int CODE_ILLEGAL_PRODUCT_ID = 2;
    private static final int CODE_ILLEGAL_RATE = 3;

    private void showImproperOperationAlert(int code) {
        UiUtils.createAlert(requireContext(), getString(R.string.improper_collect_operation, code))
                .setPositiveButton(android.R.string.ok, null).show();
    }


    public void afterCollectDeviceDatum(boolean z, String ConsumeDT, int ProductID, int DeviceID, int AccountID, String str2, int i4, String PerMoney, String str4, String rate, String DeviceMac) {
    }

    public void afterAwakenDevice(boolean z, int i, int DeviceID, int productID, int accID, byte[] macBuffer, byte[] tac_timeBuffer, int i5, int i6, int chargeMethod, int i8) {
    }

    @Override
    public void afterCollectDeviceDatum(boolean success, String consumeDT, int productID, int deviceID, int accountID, String groupID, int useCount, String perMoney, String usedMoney, String rate, String mac, byte[] raw) {
        if (mProductID == 0) {
            mProductID = productID;
        }
        if (mProductID == 0) {
            showImproperOperationAlert(CODE_ILLEGAL_PRODUCT_ID);
            return;
        }
        if ("0".equals(rate)) {
            showImproperOperationAlert(CODE_ILLEGAL_RATE);
            return;
        }
        mUpdateService.update(ApiUtils.generateHeaderMap(), mProductID, perMoney, rate, mChargeMethod).enqueue(new ApiUtils.SimpleResultCallbackAdapter<SimpleResult>(requireActivity()) {
            @Override
            public void onSuccess(SimpleResult result) {
                mDeviceInfo.setHasPrjRate(true);
                UiUtils.createAlert(requireContext(), R.string.collect_success).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                }).show();
            }

            @Override
            public void onWhatever() {
                super.onWhatever();
                if (mCollectProgress != null && mCollectProgress.isShowing()) {
                    mCollectProgress.dismiss();
                }
            }
        });
    }

    @Override
    public void afterAwakenDevice(boolean success, int deviceStatus, int deviceID, int productID, int accountID, byte[] mac, byte[] tac, int i5, int i6, int chargeMethod, int i8, String str) {
        if (success) {
            switch (deviceStatus) {
                case 0:
                    showImproperOperationAlert(CODE_DEVICE_NOT_IN_USING);
                    break;
                case 1:
                    mProductID = productID;
                    mChargeMethod = chargeMethod;
                    if (mChargeMethod == 0) {
                        showImproperOperationAlert(CODE_ILLEGAL_CHARGE_METHOD);
                    } else {
                        Dispatcher.collectDeviceDatum(mBlueService);
                    }
                    break;
                default:
                    UiUtils.createAlert(requireContext(), R.string.device_busy_and_change).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dismiss();
                        }
                    }).show();
                    break;
            }
        } else {
            UiUtils.showErrorToast(requireActivity(), R.string.awaken_device_failed);
        }
    }

    @Override
    public void afterStoreSpray(boolean success) {

    }

    @Override
    public void afterEndSpray(boolean success, int AccountID) {

    }

    @Override
    public void startDeal(boolean success, byte[] bArr) {

    }


    @Override
    public void stopDeal(boolean success) {

    }

    @Override
    public void afterDispatchFeeRate(boolean success) {

    }

    private void notifyConnectionIssues() {
        if (mCollectProgress != null && mCollectProgress.isShowing()) {
            mCollectProgress.dismiss();
        }
        UiUtils.showErrorToast(requireActivity(), getString(R.string.blu_connection_issue_occurred));
    }

    @Override
    public void onConnectStart() {

    }


    @Override
    public void onConnectFail(Exception e) {
        notifyConnectionIssues();
    }

    @Override
    public void onConnectionLost(Exception e) {
        notifyConnectionIssues();
    }

    @Override
    public void onConnected() {
        Dispatcher.awakenDevice(mBlueService);
    }

    @Override
    public void onWritten(byte[] data) {

    }

    @Override
    public void onRead(byte[] data) {
        Analyzer.analyze(data);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_action) {
            mCollectProgress = UiUtils.createProgressDialog(requireActivity(), R.string.collecting);
            mCollectProgress.show();
            mBlueService.connect();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBlueService != null) {
            mBlueService.purge();
        }
    }

}
