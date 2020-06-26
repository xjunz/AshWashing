package com.ashwashing.pro.ui.activity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.R;
import com.ashwashing.pro.api.SubscriptionPlanQueryService;
import com.ashwashing.pro.api.bean.ListResult;
import com.ashwashing.pro.api.bean.Plan;
import com.ashwashing.pro.customview.ElasticDragDismissFrameLayout;
import com.ashwashing.pro.ui.fragment.dialog.OrderFragment;
import com.ashwashing.pro.util.ApiUtils;
import com.ashwashing.pro.util.UiUtils;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;

public class SubscribeActivity extends BaseActivity {
    private ProgressBar mPbLoad;
    private Button mBtnRetry, mBtnCreateOrder;
    private TextView mTvPayHint;
    private RecyclerView mRvPlan;
    private PlanAdapter mAdapter;
    private int mSelectedPlanIndex = 0;
    private List<Plan> mPlanList;
    private SubscriptionPlanQueryService mPlanQueryService;
    private Call<ListResult<Plan>> mPlanQueryCall;
    private String CREATE_ORDER_COUNT_DOWN_TAG = "ash.tag.countdown.create_order";
    private AshApp.OrderManager mOrderManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe);
        mOrderManager = AshApp.getOrderManager();
        initViews();
        mPlanQueryService = ApiUtils.createGsonRetrofit().create(SubscriptionPlanQueryService.class);

        getWindow().getSharedElementEnterTransition().addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                mPlanQueryCall = mPlanQueryService.query(ApiUtils.generateHeaderMap());
                mPlanQueryCall.enqueue(mPlanQueryCallback);
                transition.removeListener(this);
            }
        });
    }

    private void countDownCreateOrderButton(long mills) {
        countDown(CREATE_ORDER_COUNT_DOWN_TAG, mills, new Runnable() {
            @Override
            public void run() {
                mBtnCreateOrder.setEnabled(false);
                mBtnCreateOrder.setText(getString(R.string.try_later_after, (int) Math.ceil(getLeftoverMills(CREATE_ORDER_COUNT_DOWN_TAG) / 1000f)));
            }
        }, new Runnable() {
            @Override
            public void run() {
                mBtnCreateOrder.setEnabled(true);
                mBtnCreateOrder.setText(getString(R.string.create_order));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.subscribe, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_latest_order) {
            if (mOrderManager.hasLatest()) {
                OrderFragment orderFragment = OrderFragment.createFromLatest();
                orderFragment.show(getSupportFragmentManager(), "order");
            } else {
                UiUtils.showShortAshToast(this, R.string.no_latest_order);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private Callback<ListResult<Plan>> mPlanQueryCallback = new ApiUtils.ListResultCallbackAdapter<ListResult<Plan>>(this) {
        @Override
        public void onSuccess(ListResult<Plan> result) {
            mPlanList = result.getAll();
            TransitionManager.beginDelayedTransition(mRvPlan);
            mAdapter.notifyDataSetChanged();
            refreshPayHint();
        }

        @Override
        public void onWhatever() {
            super.onWhatever();
            UiUtils.animateGone(mPbLoad);
        }

        @Override
        public void onNotSuccess() {
            super.onNotSuccess();
            UiUtils.animateVisible(mBtnRetry);
        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlanQueryCall != null) {
            mPlanQueryCall.cancel();
        }
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar(), "no action bar attached").setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ElasticDragDismissFrameLayout elasticDragDismissFrameLayout = findViewById(R.id.elastic);
        elasticDragDismissFrameLayout.addListener(new ElasticDragDismissFrameLayout.SystemChromeFader(this));
        mTvPayHint = findViewById(R.id.tv_pay_hint);
        mRvPlan = findViewById(R.id.rv_package);
        mPbLoad = findViewById(R.id.pb_load);
        mBtnRetry = findViewById(R.id.btn_retry);
        mBtnCreateOrder = findViewById(R.id.btn_create_order);
        mAdapter = new PlanAdapter();
        mRvPlan.setAdapter(mAdapter);
        long leftover = getLeftoverMills(CREATE_ORDER_COUNT_DOWN_TAG);
        if (leftover != 0) {
            countDownCreateOrderButton(leftover);
        }
    }


    public void launchAlipayForCoupon(View view) {
        try {
            Intent i = new Intent();
            i.setClassName("com.eg.android.AlipayGphone", "com.eg.android.AlipayGphone.AlipayLogin");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            String code = "652483228";
            ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).setPrimaryClip(new ClipData(new ClipDescription("alipay coupon code", new String[]{"text/plain"}), new ClipData.Item(code)));
            Toast toast = Toast.makeText(this, "已复制" + code + ",在顶部搜索栏粘贴即可", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, 0, 100);
            toast.show();
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "操作失败,你安装支付宝了吗？", Toast.LENGTH_SHORT).show();
        }
    }

    public void retryQuerySbpPlans(View view) {
        mPlanQueryCall = mPlanQueryService.query(ApiUtils.generateHeaderMap());
        UiUtils.animateVisible(mPbLoad);
        UiUtils.animateGone(mBtnRetry);
        mPlanQueryCall.enqueue(mPlanQueryCallback);
    }

    private void refreshPayHint() {
        mTvPayHint.setText(getString(R.string.plan_hint_format, mSelectedPlanIndex + 1));
    }


    public void showOrderDialog(View view) {
        if (mPlanList != null) {
            OrderFragment orderFragment = OrderFragment.create(mPlanList.get(mSelectedPlanIndex).type);
            orderFragment.show(getSupportFragmentManager(), "order");
            countDownCreateOrderButton(10 * 1000);
        }
    }


    private class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.PlanViewHolder> {

        @NonNull
        @Override
        public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.item_sbp_plan, parent, false);
            return new PlanViewHolder(itemView);
        }

        private String formatMoney(float amount) {
            return getString(R.string.cny_format, amount);
        }

        private String formatDuration(int day) {
            return getString(R.string.day_format, day);
        }

        @Override
        public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
            if (mPlanList != null) {
                holder.itemView.setVisibility(View.VISIBLE);
                Plan plan = mPlanList.get(position);
                holder.tvNum.setText(String.valueOf(position + 1));
                holder.tvAmount.setText(formatMoney(plan.spotPrice));
                holder.tvAmountCrossed.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                holder.tvAmountCrossed.setText(formatMoney(plan.originalPrice));
                holder.tvDuration.setText(formatDuration(plan.durationDayUnited));
                holder.rbSelection.setChecked(position == mSelectedPlanIndex);
            } else {
                holder.itemView.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return mPlanList == null ? 3 : mPlanList.size();
        }

        private class PlanViewHolder extends RecyclerView.ViewHolder {
            TextView tvAmount, tvAmountCrossed, tvDuration, tvNum;
            RadioButton rbSelection;

            PlanViewHolder(@NonNull View itemView) {
                super(itemView);
                tvAmount = itemView.findViewById(R.id.tv_amount);
                tvAmountCrossed = itemView.findViewById(R.id.tv_amount_crossed);
                tvDuration = itemView.findViewById(R.id.tv_duration);
                tvNum = itemView.findViewById(R.id.tv_package_num);
                rbSelection = itemView.findViewById(R.id.rb_selection);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = getAdapterPosition();
                        if (index != mSelectedPlanIndex) {
                            int old = mSelectedPlanIndex;
                            mSelectedPlanIndex = index;
                            notifyItemChanged(old);
                            rbSelection.setChecked(true);
                            refreshPayHint();
                        }
                    }
                });
            }
        }
    }
}
