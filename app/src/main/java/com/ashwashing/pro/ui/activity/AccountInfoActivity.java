package com.ashwashing.pro.ui.activity;

import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ashwashing.pro.R;
import com.ashwashing.pro.api.bean.AccountInfo;
import com.ashwashing.pro.util.UniUtils;

import static com.ashwashing.pro.AshApp.ACTION_LOG_OUT;

public class AccountInfoActivity extends AppCompatActivity {
    private ImageView mIvAvatar;
    private AccountInfo mInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mInfo = AccountInfo.mine();
        RecyclerView rvUserInfo = findViewById(R.id.rv_user_info);
        UserInfoAdapter mAdapter = new UserInfoAdapter();
        rvUserInfo.setAdapter(mAdapter);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mIvAvatar.setImageResource(mInfo.getAvatarRes());
    }

    public void logOut(View view) {
        //invalidate the account
        mInfo.invalidate();
        //send broadcast
        Intent i = new Intent(ACTION_LOG_OUT);
        sendBroadcast(i);
        mIvAvatar.setImageResource(R.drawable.ic_account_circle_64dp);
        finishAfterTransition();
    }


    private class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.UserInfoViewHolder> {
        private int[] titleResources;
        private int[] iconResources;

        UserInfoAdapter() {
            TypedArray typedArray = getResources().obtainTypedArray(R.array.user_info_titles);
            titleResources = new int[typedArray.length()];
            for (int i = 0; i < titleResources.length; i++) {
                titleResources[i] = typedArray.getResourceId(i, -1);
            }
            typedArray = getResources().obtainTypedArray(R.array.user_info_icons);
            iconResources = new int[typedArray.length()];
            for (int i = 0; i < iconResources.length; i++) {
                iconResources[i] = typedArray.getResourceId(i, -1);
            }
            typedArray.recycle();
        }

        @NonNull
        @Override
        public UserInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.item_user_info, parent, false);
            return new UserInfoViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(@NonNull UserInfoViewHolder holder, int position) {
            int titleRes = titleResources[position];
            holder.tvTitle.setText(getString(titleRes));
            holder.ivIcon.setImageResource(iconResources[position]);
            String detail = "-";
            switch (titleRes) {
                case R.string.account_name:
                    detail = mInfo.getUsername();
                    break;
                case R.string.phone_number:
                    detail = UniUtils.blurPhoneNum(mInfo.getPhone());
                    break;
                case R.string.academy:
                    detail = mInfo.getAcademy();
                    break;
                case R.string.subscription_info:
                    detail = mInfo.isSubscriptionDue() ? getString(R.string.no_sbp) : getString(R.string.subscribe_to, mInfo.getSubscriptionDueDate());
                    break;
                case R.string.register_time:
                    detail = mInfo.getRegisterTime();
                    break;
            }
            holder.tvDetail.setText(detail);
        }

        @Override
        public int getItemCount() {
            return titleResources.length;
        }

        private class UserInfoViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDetail;
            ImageView ivIcon;

            UserInfoViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvDetail = itemView.findViewById(R.id.tv_detail);
                ivIcon = itemView.findViewById(R.id.iv_icon);

            }
        }
    }


}
