package com.ashwashing.pro.ui.fragment.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.R;
import com.ashwashing.pro.api.AccountInfoQueryService;
import com.ashwashing.pro.api.OrderQueryService;
import com.ashwashing.pro.api.bean.AccountInfo;
import com.ashwashing.pro.api.bean.DataResult;
import com.ashwashing.pro.api.bean.OrderInfo;
import com.ashwashing.pro.util.ApiUtils;
import com.ashwashing.pro.util.UiUtils;
import com.ashwashing.pro.util.UniUtils;

import static com.ashwashing.pro.AshApp.OrderManager.ORDER_COUNT_DOWN_DURATION;

public class OrderFragment extends DialogFragment implements View.OnClickListener {
    private Button mBtnRetry, mBtnAction, mBtnCheck, mBtnCopyOrderId;
    private TextView mTvOrderInfo;
    private ProgressBar mPbLoad;
    private OrderQueryService mOrderQueryService;
    private AccountInfoQueryService mAccountQueryService;
    private int mOrderType;
    private OrderInfo mInfo;
    private boolean mBackFromAlipay;
    private AshApp.OrderManager mOrderManager;

    private AshApp.CountDownerManager.CountDowner mCountDowner;


    private OrderFragment() {
        mOrderManager = AshApp.getOrderManager();
        mCountDowner = mOrderManager.getCountDowner();
    }

    public static OrderFragment create(int orderType) {
        OrderFragment orderFragment = new OrderFragment();
        orderFragment.mOrderType = orderType;
        return orderFragment;
    }


    public static OrderFragment createFromLatest() {
        OrderFragment orderFragment = new OrderFragment();
        orderFragment.mInfo = orderFragment.mOrderManager.getLatest();
        return orderFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_Dialog_Alert_Material_Fragment_Order);
        mOrderQueryService = ApiUtils.createGsonRetrofit().create(OrderQueryService.class);
        mAccountQueryService = ApiUtils.createGsonRetrofit().create(AccountInfoQueryService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_order, container, false);
        mBtnRetry = root.findViewById(R.id.btn_retry);
        mBtnAction = root.findViewById(R.id.btn_action);
        mTvOrderInfo = root.findViewById(R.id.tv_order_info);
        mPbLoad = root.findViewById(R.id.pb_load);
        mBtnAction.setOnClickListener(this);
        mBtnRetry.setOnClickListener(this);
        mBtnCheck = root.findViewById(R.id.btn_check);
        mBtnCheck.setOnClickListener(this);
        mBtnCopyOrderId = root.findViewById(R.id.btn_copy_order_id);
        mBtnCopyOrderId.setOnClickListener(this);
        return root;
    }

    private String formatOrderInfo(OrderInfo info) {
        return getString(R.string.order_info_format, info.order_id, info.order_price, info.order_price - info.qr_price, info.qr_price);
    }

    private void countDownActionButton(long total) {
        mCountDowner.countDown(total
                , getLifecycle(), new Runnable() {
                    @Override
                    public void run() {
                        mBtnAction.setText(String.format("%s %s", getString(R.string.goto_alipay), UniUtils.formatDuration(mCountDowner.getLeftoverMills())));
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        UiUtils.animateInvisible(mBtnCheck);
                        mBtnAction.setEnabled(false);
                        mBtnAction.setText(R.string.expired);
                    }
                });
    }

    private void notifyFromOrderInfo(OrderInfo info) {
        if (mCountDowner.getLeftoverMills() != 0) {
            countDownActionButton(mCountDowner.getLeftoverMills());
        }
        UiUtils.makeGone(mPbLoad);
        mTvOrderInfo.setText(formatOrderInfo(info));
        UiUtils.makeVisible(mTvOrderInfo, mBtnAction, mBtnCheck);
    }

    private void enqueue() {
        mOrderQueryService.query(ApiUtils.generateHeaderMap(), mOrderType).enqueue(new ApiUtils.DataResultCallbackAdapter<DataResult<OrderInfo>>(requireActivity()) {
            @Override
            public void onWhatever() {
                UiUtils.animateGone(mPbLoad);
            }

            @Override
            public void onSuccess(DataResult<OrderInfo> result) {
                mInfo = result.getData();
                mInfo.local_create_timestamp = System.currentTimeMillis();
                mOrderManager.setLatest(mInfo);
                mTvOrderInfo.setText(formatOrderInfo(mInfo));
                UiUtils.animateVisible(mTvOrderInfo);
                UiUtils.animateVisible(mBtnAction);
                countDownActionButton(ORDER_COUNT_DOWN_DURATION);
            }

            @Override
            public void onNotSuccess() {
                super.onNotSuccess();
                UiUtils.animateVisible(mBtnRetry);
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mInfo != null) {
            notifyFromOrderInfo(mInfo);
        } else {
            enqueue();
        }
    }

    private void launchAlipayFromUri(String uri) {
        try {
            Intent i = new Intent();
            i.setData(Uri.parse(uri));
            startActivity(i);
            mBackFromAlipay = true;
        } catch (Exception e) {
            UiUtils.showErrorToast(requireActivity(), R.string.launch_alipay_failed);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBackFromAlipay) {
            mBackFromAlipay = false;
            UiUtils.animateVisible(mBtnCheck);
        }
    }

    private void showUnpaidDialog() {
        UiUtils.createAlert(requireActivity(), R.string.unpaid_rationale).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_action:
                if (mInfo.qr_url == null) {
                    UiUtils.showErrorToast(requireActivity(), R.string.get_order_failed);
                } else {
                    launchAlipayFromUri(mInfo.qr_url);
                }
                break;
            case R.id.btn_retry:
                UiUtils.animateVisible(mPbLoad);
                UiUtils.animateGone(mBtnRetry);
                enqueue();
                break;
            case R.id.btn_check:
                final Dialog progress = UiUtils.createProgressDialog(requireActivity(), R.string.verifying);
                progress.show();
                mAccountQueryService.queryAccountInfo(ApiUtils.generateHeaderMap()).enqueue(new ApiUtils.DataResultCallbackAdapter<DataResult<AccountInfo>>(requireActivity()) {

                    @Override
                    public void onSuccess(DataResult<AccountInfo> result) {
                        AccountInfo accountInfo = result.getData();
                        if (AccountInfo.mine().getSubscriptionDueDate().equals(accountInfo.getSubscriptionDueDate())) {
                            showUnpaidDialog();
                        } else {
                            mOrderManager.removeLatest();
                            AccountInfo.mine().set(accountInfo);
                            UiUtils.showShortAshToast(requireActivity(), getString(R.string.subscribe_success, accountInfo.getSubscriptionDueDate()));
                            requireActivity().sendBroadcast(new Intent(AshApp.ACTION_SBP_UPDATED));
                            dismiss();
                        }
                    }

                    @Override
                    public void onWhatever() {
                        super.onWhatever();
                        progress.dismiss();
                    }
                });
                break;
            case R.id.btn_copy_order_id:
                if (mInfo != null && !TextUtils.isEmpty(mInfo.order_id)) {
                    AshApp.copyToClipboard(mInfo.order_id);
                    UiUtils.showShortAshToast(requireActivity(), R.string.copy_completed);
                }
                break;
        }
    }


}
