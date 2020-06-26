package com.ashwashing.pro.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.ashwashing.pro.R;
import com.ashwashing.pro.api.Constants;
import com.ashwashing.pro.api.ConsumptionHistoryQueryService;
import com.ashwashing.pro.api.bean.Consumption;
import com.ashwashing.pro.api.bean.ListResult;
import com.ashwashing.pro.api.converter.AshGsonConverterFactory;
import com.ashwashing.pro.customview.ElasticDragDismissFrameLayout;
import com.ashwashing.pro.util.ApiUtils;
import com.ashwashing.pro.util.UiUtils;
import com.ashwashing.pro.util.UniUtils;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Retrofit;

public class HistoryActivity extends BaseActivity {
    private ImageView mPlaceHolderArt;
    private HistoryAdapter mAdapter;
    private ConsumptionHistoryQueryService mQueryService;
    private Call<ListResult<Consumption>> mQueryCall;
    private List<Consumption> mHistory;
    private ProgressBar mPbLoad;
    private boolean mShouldRefreshList = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar(), "no action bar attached").setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        RecyclerView rvHistory = findViewById(R.id.rv_history);
        mPlaceHolderArt = findViewById(R.id.iv_nothing);
        mPbLoad = findViewById(R.id.pb_load);
        final ElasticDragDismissFrameLayout elasticDragDismissFrameLayout = findViewById(R.id.elastic);
        elasticDragDismissFrameLayout.addListener(new ElasticDragDismissFrameLayout.SystemChromeFader(this) {
            @Override
            public void onDragDismissed() {
                // if dragged up
                if (elasticDragDismissFrameLayout.getTranslationY() < 0) {
                    getWindow().setReturnTransition(TransitionInflater.from(HistoryActivity.this).inflateTransition(R.transition.history_slide_from_top));
                }
                super.onDragDismissed();
            }
        });

        mAdapter = new HistoryAdapter();
        rvHistory.setAdapter(mAdapter);
        mQueryService = ApiUtils.createGsonRetrofit().create(ConsumptionHistoryQueryService.class);
        mQueryCall = mQueryService.getAll(ApiUtils.generateHeaderMap());
        mQueryCall.enqueue(mCallback);
        getWindow().getEnterTransition().addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                transition.removeListener(this);
                mShouldRefreshList = true;
                if (mHistory != null) {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private ApiUtils.ListResultCallbackAdapter<ListResult<Consumption>> mCallback = new ApiUtils.ListResultCallbackAdapter<ListResult<Consumption>>(this) {
        @Override
        public void onSuccess(ListResult<Consumption> result) {
            mHistory = result.getAll();
            Collections.reverse(mHistory);
            if (mHistory.size() == 0) {
                UiUtils.animateVisible(mPlaceHolderArt);
            } else if (mShouldRefreshList) {
                mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onNotSuccess() {
            super.onNotSuccess();
            UiUtils.animateVisible(mPlaceHolderArt);
        }

        @Override
        public void onWhatever() {
            UiUtils.animateGone(mPbLoad);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mQueryCall != null) {
            mQueryCall.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.history, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_statistics) {
            if (mHistory == null || mHistory.size() == 0) {
                return super.onOptionsItemSelected(item);
            }
            float cost = 0;
            for (Consumption consumption : mHistory) {
                cost += consumption.UsedMoney / 1000f;
            }
            new AlertDialog.Builder(this).setTitle(R.string.statistics)
                    .setMessage(getString(R.string.statistics_format, mHistory.get(0).ConsumeDT, mHistory.size(), cost))
                    .setNegativeButton(android.R.string.ok, null)
                    .show();
        } else if (item.getItemId() == R.id.item_refresh) {
            UiUtils.animateVisible(mPbLoad);
            UiUtils.animateGone(mPlaceHolderArt);
            if (mHistory != null) {
                mHistory.clear();
                mAdapter.notifyDataSetChanged();
            }
            mQueryCall = mQueryService.getAll(ApiUtils.generateHeaderMap());
            mQueryCall.enqueue(mCallback);
        }
        return super.onOptionsItemSelected(item);
    }


    private String getDuration(Consumption consumption) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date start = df.parse(consumption.ConsumeDT);
            Date end = df.parse(consumption.ConsumeFinishDT);
            long l = end.getTime() - start.getTime();
            return UniUtils.formatDuration(l);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "null";
    }


    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {
        private static final int TYPE_NORMAL_ITEM = 0x2;
        private static final int TYPE_PADDING_ITEM = 0x3;

        @Override
        public int getItemViewType(int position) {
            return position == 0 ? TYPE_PADDING_ITEM : TYPE_NORMAL_ITEM;
        }

        @NonNull
        @Override
        public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            int res = viewType == TYPE_NORMAL_ITEM ? R.layout.item_timeline : R.layout.item_timeline_padding;
            return new HistoryViewHolder(getLayoutInflater().inflate(res, parent, false));

        }

        @Override
        public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
            if (holder.getItemViewType() == TYPE_NORMAL_ITEM) {
                Consumption item = mHistory.get(position - 1);
                holder.tvNum.setText(String.valueOf(position));
                holder.tvBillID.setText(item.ConsumeID);
                String finishDT = TextUtils.isEmpty(item.ConsumeFinishDT) ? getString(R.string.shower_undone) : item.ConsumeFinishDT;
                String duration = TextUtils.isEmpty(item.ConsumeFinishDT) ? "-" : getDuration(item);
                holder.tvDuration.setText(getString(R.string.shower_duration_format, item.ConsumeDT, finishDT, duration));
                holder.tvDevice.setText(item.DevName);
                holder.tvCost.setText(getString(R.string.yuan, item.UsedMoney / 1000f));
                if (position == getItemCount() - 1) {
                    holder.dividerBottom.setVisibility(View.GONE);
                } else {
                    holder.dividerBottom.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return mHistory == null ? 0 : mHistory.size() + 1;
        }

        class HistoryViewHolder extends RecyclerView.ViewHolder {
            TextView tvNum, tvBillID, tvDevice, tvDuration, tvCost;
            View dividerBottom;

            HistoryViewHolder(@NonNull final View itemView) {
                super(itemView);
                tvNum = itemView.findViewById(R.id.tv_num);
                tvBillID = itemView.findViewById(R.id.tv_bill_id);
                tvDevice = itemView.findViewById(R.id.tv_device);
                tvDuration = itemView.findViewById(R.id.tv_duration);
                tvCost = itemView.findViewById(R.id.tv_cost);
                dividerBottom = itemView.findViewById(R.id.divider_bottom);
            }
        }
    }
}
