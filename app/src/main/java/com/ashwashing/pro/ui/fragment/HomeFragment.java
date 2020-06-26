package com.ashwashing.pro.ui.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.R;
import com.ashwashing.pro.api.DeviceInfoQueryService;
import com.ashwashing.pro.api.bean.AccountInfo;
import com.ashwashing.pro.api.bean.DataResult;
import com.ashwashing.pro.api.bean.DeviceInfo;
import com.ashwashing.pro.customview.RippleView;
import com.ashwashing.pro.impl.bluetooth.BluetoothManager;
import com.ashwashing.pro.impl.bluetooth.WaterDevice;
import com.ashwashing.pro.ui.activity.ShowerActivity;
import com.ashwashing.pro.ui.fragment.dialog.CollectFragment;
import com.ashwashing.pro.util.ApiUtils;
import com.ashwashing.pro.util.UiUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends BaseFragment implements BluetoothManager.ScanListener, BluetoothManager.BondListener {

    private RippleView mRipple;
    private Toolbar mToolbar;
    private DeviceAdapter mAdapter;
    private TextView mTvStatus;
    private Activity mContext;
    /**
     * The flag determines whether to restore view state when {@link HomeFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    private boolean mRestore;
    private int mTvStatusVisibility;
    private Dialog mProgressDialog;
    /**
     * All water devices with alias queried during this discovery. These devices will be shown to the user.
     */
    private List<WaterDevice> mNamedDeviceList;
    private BluetoothManager mBluetoothManager;
    private static final int REQUEST_BT_ENABLE_CODE = 1;
    private DeviceInfoQueryService mDeviceInfoQueryService;
    private List<DeviceInfo> mDeviceInfoList;
    private static final String NAME_DEVICE_INFO_LIST = "device_info_list";
    private boolean mShouldWriteDeviceInfoList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDeviceInfoQueryService = ApiUtils.createGsonRetrofit().create(DeviceInfoQueryService.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
        mBluetoothManager = BluetoothManager.of(mContext);
        mBluetoothManager.setDeviceFilter(new BluetoothManager.BluetoothDeviceFilter() {
            @Override
            public boolean accept(BluetoothDevice device) {
                return device != null && device.getName() != null && device.getName().startsWith("KLCXKJ") && device.getAddress().startsWith("00");
            }
        });
        mNamedDeviceList = new ArrayList<>();
        List<DeviceInfo> deviceInfoList = (List<DeviceInfo>) AshApp.getSerializedObjManager().read(NAME_DEVICE_INFO_LIST, List.class);
        if (deviceInfoList == null) {
            mDeviceInfoList = new ArrayList<>();
        } else {
            mDeviceInfoList = deviceInfoList;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup parent = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);
        initViews(parent);
        mBluetoothManager.setBondListener(this);
        mBluetoothManager.setScanListener(this);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mToolbar);
        return parent;
    }

    private void initViews(final View parent) {
        mProgressDialog = UiUtils.createProgressDialog(requireActivity(), R.string.bonding);
        mRipple = parent.findViewById(R.id.ripple_view);
        mToolbar = parent.findViewById(R.id.toolbar);
        mTvStatus = parent.findViewById(R.id.tv_status);
        RecyclerView mRvDevice = parent.findViewById(R.id.rv_device);
        mAdapter = new DeviceAdapter();
        mRvDevice.setAdapter(mAdapter);
        if (mRestore) {
            mTvStatus.setVisibility(mTvStatusVisibility);
        }
    }


    public Toolbar getToolbar() {
        return mToolbar;
    }


    //开始搜索设备（点击涟漪图中心的点击事件）
    public void invokeBluetooth(View view) {
        if (AccountInfo.mine().exists()) {
            if (AccountInfo.mine().isSubscriptionDue()) {
                UiUtils.showShortAshToast(requireActivity(), getString(R.string.not_subscribed));
                return;
            }
            if (mBluetoothManager.isDiscovering()) {
                mBluetoothManager.cancelDiscovery();
                mRipple.stop();
            } else {
                if (!mBluetoothManager.getAdapter().isEnabled()) {
                    Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBt, REQUEST_BT_ENABLE_CODE);
                } else {
                    mBluetoothManager.startDiscovery();
                }
            }
        } else {
            UiUtils.showShortAshToast(requireActivity(), getString(R.string.pls_login_first));
        }
    }


    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (!mBluetoothManager.isDiscovering()) {
            mRipple.stop();
            mTvStatus.setText(R.string.nothing_here);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBluetoothManager.removeBondListener();
        mBluetoothManager.removeScanListener();
        mRestore = true;
        mTvStatusVisibility = mTvStatus.getVisibility();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BT_ENABLE_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mBluetoothManager.startDiscovery();
            } else {
                Toast.makeText(mContext, R.string.open_bluetooth_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onFound(final WaterDevice waterDevice) {
        final String mac = waterDevice.getAddress();
        //query this device's alias in local firstly
        int index = mDeviceInfoList.indexOf(DeviceInfo.mock(mac));
        if (index >= 0) {
            DeviceInfo info = mDeviceInfoList.get(index);
            waterDevice.setInfo(info);
            mNamedDeviceList.add(waterDevice);
            mAdapter.notifyItemInserted(mNamedDeviceList.size() - 1);
            mTvStatus.setVisibility(View.GONE);
            return;
        }
        //if we did'nt find the device's alias in database then fetch it from internet
        if (isNetworkAvailable()) {
            mDeviceInfoQueryService.query(ApiUtils.generateHeaderMap(), waterDevice.getAddress()).enqueue(new Callback<DataResult<DeviceInfo>>() {
                @Override
                public void onResponse(@NotNull Call<DataResult<DeviceInfo>> call, @NotNull Response<DataResult<DeviceInfo>> response) {
                    if (response.body() != null) {
                        DeviceInfo info = response.body().getData();
                        if (info != null) {
                            waterDevice.setInfo(info);

                            //here we copy it to database for future use.
                            //only those have PrjRate do or else hasPrjRate() will always be false
                            if (info.hasPrjRate()) {
                                mDeviceInfoList.add(info);
                                mShouldWriteDeviceInfoList = true;
                            }

                            mNamedDeviceList.add(waterDevice);
                            mAdapter.notifyItemInserted(mNamedDeviceList.size() - 1);
                            mTvStatus.setVisibility(View.GONE);

                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<DataResult<DeviceInfo>> call, @NotNull Throwable t) {
                }
            });
        }
    }

    @Override
    public void onStartScan() {
        mNamedDeviceList.clear();
        mAdapter.notifyDataSetChanged();
        mTvStatus.setText(R.string.searching);
        mRipple.start();
    }

    @Override
    public void onStopScan() {
        if (mNamedDeviceList.size() == 0) {
            mTvStatus.setVisibility(View.VISIBLE);
            mTvStatus.setText(R.string.nothing_here);
        }
        mRipple.stop();
    }


    @Override
    public void onStartBond() {
        mProgressDialog.show();
    }

    @Override
    public void onBonded(WaterDevice device) {
        mProgressDialog.dismiss();
        DeviceInfo info = device.getInfo();
        if (!info.hasPrjRate()) {
            DialogFragment collectDialog = new CollectFragment();
            collectDialog.show(requireFragmentManager(), "collect");
        } else {
            Intent i = new Intent(mContext, ShowerActivity.class);
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(mContext).toBundle();
            startActivityForResult(i, 0, bundle);
        }
    }


    @Override
    public void onBondFailed() {
        Toast.makeText(mContext, getString(R.string.bond_fail), Toast.LENGTH_SHORT).show();
        mProgressDialog.dismiss();
    }


    private class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

        @NonNull
        @Override
        public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false);
            return new DeviceViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
            WaterDevice device = mNamedDeviceList.get(position);
            if (device.getInfo() == null) {
                holder.tvDeviceName.setText(device.getName());
            } else {
                if (!TextUtils.isEmpty(device.getInfo().DevName)) {
                    holder.tvDeviceName.setText(device.getInfo().DevName);
                }
            }
            holder.tvMacAddress.setText(device.getAddress());
        }


        @Override
        public int getItemCount() {
            return mNamedDeviceList.size();
        }

        private class DeviceViewHolder extends RecyclerView.ViewHolder {
            TextView btnConnect;
            TextView tvDeviceName;
            TextView tvMacAddress;

            DeviceViewHolder(@NonNull View itemView) {
                super(itemView);
                btnConnect = itemView.findViewById(R.id.btn_bond);
                tvDeviceName = itemView.findViewById(R.id.tv_device_name);
                tvMacAddress = itemView.findViewById(R.id.tv_mac);
                btnConnect.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBluetoothManager.bondWaterDevice(mNamedDeviceList.get(getAdapterPosition()));
                    }
                });
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mShouldWriteDeviceInfoList) {
            AshApp.getSerializedObjManager().write(mDeviceInfoList, NAME_DEVICE_INFO_LIST);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRestore = false;
        mBluetoothManager.unregisterReceiver();
    }

}



