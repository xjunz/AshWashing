package com.ashwashing.pro.ui.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.ashwashing.pro.R;
import com.ashwashing.pro.api.ClientCheckService;
import com.ashwashing.pro.api.Constants;
import com.ashwashing.pro.api.SprayService;
import com.ashwashing.pro.api.bean.AccountInfo;
import com.ashwashing.pro.api.bean.DataResult;
import com.ashwashing.pro.api.bean.DeviceInfo;
import com.ashwashing.pro.api.bean.SimpleResult;
import com.ashwashing.pro.api.bean.SprayInfo;
import com.ashwashing.pro.impl.analyze.AnalyTools;
import com.ashwashing.pro.impl.bluetooth.BluetoothManager;
import com.ashwashing.pro.impl.bluetooth.BluetoothService;
import com.ashwashing.pro.impl.bluetooth.WaterDevice;
import com.ashwashing.pro.impl.cmd.Analyzer;
import com.ashwashing.pro.impl.cmd.Dispatcher;
import com.ashwashing.pro.impl.cmd.Listener;
import com.ashwashing.pro.util.ApiUtils;
import com.ashwashing.pro.util.UiUtils;
import com.ashwashing.pro.util.UniUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This the activity controlling a whole shower via the bluetooth communication with a shower device.The following shows a coarse procedures of a shower:
 * <p>
 * enter activity--> connect--> awaken device 1--> check device status--> enter pending state--> press button--> awaken device 2--> dispatch fee rate to device
 * --> enter showering state--> press button--> end spray--> collect device datum--> store spray--> enter finishing state--> exit activity
 * </P>
 */
public class ShowerActivity2 extends BaseActivity implements BluetoothService.Callback, Listener {
    private RecyclerView mRvInfo;
    private TextView mTvStatusTag, mTvActionTint;
    private ProgressBar mPbPrepare;
    private ImageButton mIbAction;
    private ImageView mIvBanner;
    private ShowerState mState;
    private InfoAdapter mAdapter;
    private BluetoothService mBluetoothService;
    private WaterDevice mDevice;
    private AccountInfo mUserInfo;
    private DeviceInfo mDeviceInfo;
    private boolean mDeviceCheckedUsable;
    private SprayService mSprayService;
    private ClientCheckService mCheckService;
    private int mCost;
    private long mStartTimestamp, mEndTimestamp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDevice = BluetoothManager.getAwaitingDevice();
        if (mDevice == null) {
            throw new RuntimeException("got null device!");
        } else if (mDevice.getInfo() == null) {
            throw new RuntimeException("got null device info!");
        }
        mDeviceInfo = mDevice.getInfo();
        mUserInfo = AccountInfo.mine();
        mState = ShowerState.PENDING;

        setContentView(R.layout.activity_shower);
        initViews();

        Analyzer.setListener(this);
        mBluetoothService = new BluetoothService(mDevice.getBluetoothDevice(), this);
        mBluetoothService.connect();
        mSprayService = ApiUtils.createGsonRetrofit().create(SprayService.class);
        mCheckService = ApiUtils.createGsonRetrofit().create(ClientCheckService.class);
    }

    private void initViews() {
        mTvStatusTag = findViewById(R.id.tv_tag);
        mTvActionTint = findViewById(R.id.tv_tint);
        mPbPrepare = findViewById(R.id.pb_prepare);
        mIbAction = findViewById(R.id.ib_action);
        mIvBanner = findViewById(R.id.iv_banner);
        mRvInfo = findViewById(R.id.rv_device_info);
        mAdapter = new InfoAdapter();
        mRvInfo.setAdapter(mAdapter);
        mIbAction.setEnabled(false);
        mIbAction.setImageResource(mState.stateIconRes);
    }


    @Override
    public void onConnectStart() {
        switchToLoadingState(true);
    }

    @Override
    public void onConnectFail(Exception e) {
        setShowerState(ShowerState.INTERRUPTED, true);
    }

    @Override
    public void onConnectionLost(Exception e) {
        setShowerState(ShowerState.INTERRUPTED, true);
    }

    @Override
    public void onConnected() {
        Dispatcher.awakenDevice(mBluetoothService);
    }

    @Override
    public void onWritten(byte[] data) {
        //no-op
    }

    @Override
    public void onRead(byte[] data) {
        AnalyTools.analyWaterDatas(data);
    }


    public void afterCollectDeviceDatum(boolean success, final String ConsumeDT, final int PrjID, final int DeviceID, final int AccID, String GroupID, final int UseCount, String YKMoney, String UsedMoney, String Rate, String MacAddress) {
    }


    public void afterAwakenDevice(boolean success, int deviceStatus, int DeviceID, int PrjID, final int AccID, final byte[] mac, final byte[] tac, int i5, int i6, int ChargeMethod, int i8) {
    }

    @Override
    public void afterCollectDeviceDatum(boolean success, final String consumeDT, final int productID, final int deviceID, final int accountID, String groupID, final int useCount, String perMoney, String usedMoney, String rate, String mac, byte[] raw) {
        if (success) {
            String formattedConsumeDT = "20" + consumeDT;
            mCost = Integer.parseInt(usedMoney);
            //end my spray
            mSprayService.end(ApiUtils.generateHeaderMap(), mDevice.getAddress(), formattedConsumeDT, usedMoney).enqueue(new ShowerSimpleResultCallbackAdapter<SimpleResult>(ShowerState.END_SPRAY_ERROR) {
                @Override
                public void onSuccess(SimpleResult result) {
                    Dispatcher.storeSpray(mBluetoothService, consumeDT, productID, deviceID, accountID, useCount);
                }
            });
        } else {
            setShowerState(ShowerState.COLLECT_DATUM_ERROR, true);
        }
    }

    @Override
    public void afterAwakenDevice(boolean success, int deviceStatus, int deviceID, int productID, int accountID, final byte[] mac, final byte[] tac, int i5, int i6, int chargeMethod, int i8, String str) {
        if (success) {
            switch (deviceStatus) {
                case 0:
                    if (mDeviceCheckedUsable) {
                        mSprayService.start(ApiUtils.generateHeaderMap(), mDevice.getAddress()).enqueue(new ShowerDataResultCallbackAdapter<DataResult<SprayInfo>>(ShowerState.START_SPRAY_ERROR) {
                            @Override
                            public void onSuccess(DataResult<SprayInfo> result) {
                                SprayInfo info = result.getData();
                                Dispatcher.dispatchFeeRate(mBluetoothService, mac, tac, info.getCommand());
                            }
                        });
                    } else {
                        mDeviceCheckedUsable = true;
                        setShowerState(ShowerState.PENDING, true);
                    }
                    break;
                case 1:
                    if (accountID != mUserInfo.AccID && mDeviceCheckedUsable) {
                        //这种情况是你唤醒了设备，但是别人已经结束了你的洗澡,你只能断开
                        UiUtils.createAlert(this, R.string.shower_ended_and_close)
                                .setPositiveButton(R.string.close, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mBluetoothService.safeClose();
                                    }
                                }).show();
                    } else {
                        mCheckService.check(ApiUtils.generateHeaderMap(), mDevice.getAddress(), accountID, productID, deviceID).enqueue(new ShowerSimpleResultCallbackAdapter<SimpleResult>(ShowerState.CHECK_CLIENT_ERROR) {
                            @Override
                            public void onSuccess(SimpleResult result) {
                                UiUtils.createAlert(ShowerActivity2.this, R.string.continue_shower_or_end)
                                        .setNegativeButton(R.string.end, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                setShowerState(ShowerState.SHOWERING, false);
                                                progressShowerState(null);
                                            }
                                        })
                                        .setPositiveButton(R.string.continue_use, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                setShowerState(ShowerState.SHOWERING, false);
                                            }
                                        }).show();

                            }

                            @Override
                            public void onError(int code, String msg) {
                                if (code == Constants.CODE_EX_CLIENT) {
                                    onNotSuccess();
                                    UiUtils.createAlert(ShowerActivity2.this, R.string.use_ex_client)
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mBluetoothService.purge();
                                                    finishAfterTransition();
                                                }
                                            }).show();
                                } else {
                                    super.onError(code, msg);
                                }
                            }
                        });
                    }

                    break;
                case 2:
                    UiUtils.createAlert(this, R.string.device_busy_and_change).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAfterTransition();
                        }
                    }).show();
                    break;
                case 3:
                    Dispatcher.collectDeviceDatum(mBluetoothService);
                    break;
            }
        } else {
            setShowerState(ShowerState.AWAKEN_DEVICE_ERROR, true);
        }
    }

    @Override
    public void afterStoreSpray(boolean success) {
        if (success) {
            //the spray now stopped and the shower is finished
            setShowerState(ShowerState.FINISHED, false);
            mEndTimestamp = System.currentTimeMillis();
        }
    }

    @Override
    public void afterEndSpray(boolean success, int AccountID) {
        if (success) {
            Dispatcher.collectDeviceDatum(this.mBluetoothService);
        } else {
            Dispatcher.awakenDevice(this.mBluetoothService);
        }
    }

    @Override
    public void startDeal(boolean success, byte[] bArr) {

    }


    @Override
    public void stopDeal(boolean success) {

    }

    @Override
    public void afterDispatchFeeRate(boolean success) {
        //   switchToLoadingState(false);
        if (success) {
            //the spray now start working
            setShowerState(ShowerState.SHOWERING, false);
            mStartTimestamp = System.currentTimeMillis();
        } else {
            mDeviceCheckedUsable = false;
            UiUtils.createAlert(this, R.string.device_error_and_retry).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mBluetoothService.safeClose();
                }
            }).show();
        }
    }


    public void progressShowerState(View view) {
        switch (mState) {
            case PENDING:
                switchToLoadingState(true);
                Dispatcher.awakenDevice(mBluetoothService);
                break;
            case SHOWERING:
                switchToLoadingState(true);
                Dispatcher.endSpray(mBluetoothService);
                break;
            case INTERRUPTED:
                mBluetoothService.connect();
                break;
            case CHECK_CLIENT_ERROR:
                switchToLoadingState(true);
                Dispatcher.awakenDevice(mBluetoothService);
                break;
            case START_SPRAY_ERROR:
                switchToLoadingState(true);
                Dispatcher.awakenDevice(mBluetoothService);
                break;
            case END_SPRAY_ERROR:
                switchToLoadingState(true);
                Dispatcher.endSpray(mBluetoothService);
                break;
            default:
                switchToLoadingState(true);
                Dispatcher.awakenDevice(mBluetoothService);
                break;
        }
    }


    private void setShowerState(final ShowerState showerState, boolean forceRefresh) {
        if (mState == null) {
            return;
        }
        if (!forceRefresh && mState == showerState) {
            return;
        }
        switchToLoadingState(false);
        int preColorRes = mState.stateColorRes;
        this.mState = showerState;

        if (showerState.stateIconRes == -1) {
            mIbAction.setEnabled(false);
            UiUtils.animateGone(mIbAction);
        } else {
            UiUtils.animateSwitchImageRes(mIbAction, showerState.stateIconRes, new Runnable() {
                @Override
                public void run() {
                    if (showerState == ShowerState.PENDING) {
                        mIbAction.setEnabled(mDeviceCheckedUsable);
                    } else {
                        mIbAction.setEnabled(true);
                    }
                }
            });
        }

        if (showerState.tintRes == -1) {
            UiUtils.animateGone(mTvActionTint);
        } else {
            UiUtils.animateSwitchTextRes(mTvActionTint, showerState.tintRes, null);
        }

        animateTag(showerState.stateTagRes);
        animateBackgroundColor(getResources().getColor(preColorRes), getResources().getColor(showerState.stateColorRes));
        mAdapter.notifyShowerStateChanged();
    }

    private void switchToLoadingState(boolean into) {
        if (into) {
            setBlockBackPress(true);
            if (mState.stateIconRes == R.drawable.ic_refresh_24dp/*mState == ShowerState.INTERRUPTED*/) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(mIbAction, View.ROTATION, 0, 360f).setDuration(500L);
                animator.setRepeatCount(ValueAnimator.INFINITE);
                animator.setRepeatMode(ValueAnimator.RESTART);
                animator.setAutoCancel(true);
                animator.start();
            } else {
                mPbPrepare.animate().alpha(1f).start();
            }
        } else {
            setBlockBackPress(false);
            if (mState.stateIconRes == R.drawable.ic_refresh_24dp/*mState == ShowerState.INTERRUPTED*/) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(mIbAction, View.ROTATION, mIbAction.getRotation(), 0f);
                animator.setStartDelay(100L);
                animator.start();
            } else {
                mPbPrepare.animate().alpha(0f).start();
            }
        }
    }

    private void animateTag(@StringRes int tagRes) {
        mTvStatusTag.setText(tagRes);
        mTvStatusTag.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(mTvStatusTag, View.SCALE_X, mTvStatusTag.getScaleX(), 1.2f, 1f).start();
        ObjectAnimator.ofFloat(mTvStatusTag, View.SCALE_Y, mTvStatusTag.getScaleY(), 1.2f, 1f).start();
    }

    private void animateBackgroundColor(@ColorInt int startColor, @ColorInt int endColor) {
        ValueAnimator valueAnimator = ValueAnimator.ofArgb(startColor, endColor);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                getWindow().setNavigationBarColor(color);
                mIvBanner.setBackgroundColor(color);
            }
        });
        valueAnimator.start();
    }


    private enum ShowerState {
        PENDING(R.drawable.ic_play_arrow_24dp, R.string.shower_pending, R.string.start_shower, R.color.colorPrimary),
        SHOWERING(R.drawable.ic_stop_24dp, R.string.shower_time, R.string.stop_shower, R.color.colorPrimaryDark),
        INTERRUPTED(R.drawable.ic_refresh_24dp, R.string.shower_interrupted, R.string.reconnect, R.color.colorAccent),
        FINISHED(-1, R.string.shower_completed, -1, R.color.grey),
        CHECK_CLIENT_ERROR(R.drawable.ic_refresh_24dp, R.string.error, R.string.client_check_error, R.string.retry, R.color.colorAccent),
        START_SPRAY_ERROR(R.drawable.ic_refresh_24dp, R.string.error, R.string.start_spray_error, R.string.retry, R.color.colorAccent),
        END_SPRAY_ERROR(R.drawable.ic_refresh_24dp, R.string.error, R.string.end_spray_error, R.string.retry, R.color.colorAccent),
        COLLECT_DATUM_ERROR(R.drawable.ic_refresh_24dp, R.string.comm_error, R.string.collect_datum_error, R.string.retry, R.color.colorAccent),
        AWAKEN_DEVICE_ERROR(R.drawable.ic_refresh_24dp, R.string.comm_error, R.string.awaken_device_error, R.string.retry, R.color.colorAccent);


        @DrawableRes
        private int stateIconRes;
        @StringRes
        private int stateTagRes;
        @StringRes
        private int stateDescRes;
        @ColorRes
        private int stateColorRes;
        @StringRes
        private int tintRes;

        ShowerState(@DrawableRes int iconRes, @StringRes int statusRes, @StringRes int tintRes, @ColorRes int colorRes) {
            this.stateIconRes = iconRes;
            this.stateTagRes = statusRes;
            this.stateDescRes = statusRes;
            this.stateColorRes = colorRes;
            this.tintRes = tintRes;
        }

        ShowerState(@DrawableRes int iconRes, @StringRes int statusRes, @StringRes int stateDescRes, @StringRes int tintRes, @ColorRes int colorRes) {
            this.stateIconRes = iconRes;
            this.stateTagRes = statusRes;
            this.stateDescRes = stateDescRes;
            this.stateColorRes = colorRes;
            this.tintRes = tintRes;
        }

    }


    private class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoViewHolder> {
        private String[] titles;
        private int[] iconResources;
        private int itemCount;

        InfoAdapter() {
            titles = getResources().getStringArray(R.array.shower_info_titles);
            iconResources = new int[titles.length];
            TypedArray ta = getResources().obtainTypedArray(R.array.info_icons);
            for (int i = 0; i < titles.length; i++) {
                iconResources[i] = ta.getResourceId(i, -1);
            }
            ta.recycle();
            itemCount = titles.length - 2;
        }

        private void notifyShowerStateChanged() {
            if (mState == ShowerState.FINISHED) {
                itemCount = titles.length;
                TransitionManager.beginDelayedTransition(mRvInfo, new ChangeBounds());
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public InfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View item = LayoutInflater.from(ShowerActivity2.this).inflate(R.layout.item_info, parent, false);
            return new InfoViewHolder(item);
        }

        @Override
        public void onBindViewHolder(@NonNull InfoViewHolder holder, int position) {
            holder.ivIcon.setImageDrawable(getResources().getDrawable(iconResources[position]));
            holder.tvTitle.setText(titles[position]);
            String detail = "-";
            switch (iconResources[position]) {
                case R.drawable.ic_date_range_24dp:
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINESE);
                    detail = simpleDateFormat.format(new Date());
                    break;
                case R.drawable.ic_water_device_24dp:
                    detail = mDeviceInfo.DevName;
                    break;
                case R.drawable.ic_account_circle_24dp:
                    detail = AccountInfo.mine().getUsername();
                    break;
                case R.drawable.ic_info_outline_24dp:
                    detail = getString(mState.stateDescRes);
                    break;
                case R.drawable.ic_timer_24dp:
                    detail = mState == ShowerState.FINISHED ? UniUtils.formatDuration(mEndTimestamp - mStartTimestamp) : "-";
                    break;
                case R.drawable.ic_expense_24dp:
                    detail = mState == ShowerState.FINISHED ? getString(R.string.yuan, mCost / 1000f) : "-";
            }

            holder.tvDetail.setText(detail);
        }

        @Override
        public int getItemCount() {
            return itemCount;
        }

        private class InfoViewHolder extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvTitle, tvDetail;

            InfoViewHolder(@NonNull View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.iv_icon);
                tvDetail = itemView.findViewById(R.id.tv_detail);
                tvTitle = itemView.findViewById(R.id.tv_title);
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            mBluetoothService.purge();
        }
    }


    private abstract class ShowerDataResultCallbackAdapter<T extends DataResult> extends ApiUtils.DataResultCallbackAdapter<T> {
        private ShowerState mErrorState;

        ShowerDataResultCallbackAdapter(ShowerState errorState) {
            super(ShowerActivity2.this);
            this.mErrorState = errorState;
        }

        @Override
        public void onNotSuccess() {
            super.onNotSuccess();
            setShowerState(mErrorState, true);
        }
    }

    private abstract class ShowerSimpleResultCallbackAdapter<T extends SimpleResult> extends ApiUtils.SimpleResultCallbackAdapter<T> {
        private ShowerState mErrorState;

        ShowerSimpleResultCallbackAdapter(ShowerState errorState) {
            super(ShowerActivity2.this);
            this.mErrorState = errorState;
        }

        @Override
        public void onNotSuccess() {
            super.onNotSuccess();
            setShowerState(mErrorState, true);
        }
    }

}
