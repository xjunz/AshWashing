package com.ashwashing.pro.ui.fragment;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.RecyclerView;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.R;
import com.ashwashing.pro.api.AccountInfoQueryService;
import com.ashwashing.pro.api.bean.AccountInfo;
import com.ashwashing.pro.api.bean.DataResult;
import com.ashwashing.pro.ui.activity.AccountInfoActivity;
import com.ashwashing.pro.ui.activity.HistoryActivity;
import com.ashwashing.pro.ui.activity.LoginActivity;
import com.ashwashing.pro.ui.activity.SubscribeActivity;
import com.ashwashing.pro.util.ApiUtils;
import com.ashwashing.pro.util.UiUtils;
import com.ashwashing.pro.util.UniUtils;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import retrofit2.Call;

public class MineFragment extends Fragment implements View.OnClickListener {
    private TextView mBtnLogInOrSubscribe;
    private TextView mTvUsername, mTvProfile;
    private ImageView mIvAvatar;
    private Activity mActivity;
    private TextView mTvSubscriptionInfo;
    private AccountInfoQueryService mQueryService;
    private Call<DataResult<AccountInfo>> mQueryCall;
    private AccountInfoUpdateReceiver mUpdateReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQueryService = ApiUtils.createGsonRetrofit().create(AccountInfoQueryService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_mine, container, false);
        RecyclerView rvMenu = content.findViewById(R.id.rv_menu);
        rvMenu.setAdapter(new MenuAdapter());
        mBtnLogInOrSubscribe = content.findViewById(R.id.btn_log_in_or_subscribe);
        mBtnLogInOrSubscribe.setOnClickListener(this);
        mIvAvatar = content.findViewById(R.id.iv_avatar);
        mTvUsername = content.findViewById(R.id.tv_title);
        mTvProfile = content.findViewById(R.id.tv_profile);
        mTvSubscriptionInfo = content.findViewById(R.id.tv_sbp_info);
        initViews(false);
        if (AccountInfo.mine().exists()) {
            mQueryCall = mQueryService.queryAccountInfo(ApiUtils.generateHeaderMap());
            mQueryCall.enqueue(new ApiUtils.DataResultCallbackAdapter<DataResult<AccountInfo>>(requireActivity()) {
                @Override
                public void onSuccess(DataResult<AccountInfo> result) {
                    AccountInfo me = AccountInfo.mine();
                    if (me.getSubscriptionDueDate() != null && !me.getSubscriptionDueDate().equals(result.getData().getSubscriptionDueDate())) {
                        AccountInfo.mine().set(result.getData());
                        AccountInfo.mine().persist();
                        initViews(false);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<DataResult<AccountInfo>> call, @NotNull Throwable t) {
                    //NO-OP
                }

                @Override
                public void onIllegalAccess() {
                    UiUtils.showErrorToast(requireActivity(), R.string.login_invalid_and_re_login);
                    AccountInfo.mine().invalidate();
                    initViews(true);
                }
            });
        }
        return content;
    }

    private void setSbpText(String raw) {
        mTvSubscriptionInfo.setText(getString(R.string.subscribe_to, raw.substring(0, 10)));
    }

    private void initViews(boolean animate) {
        AccountInfo accountInfo = AccountInfo.mine();
        initSharedElements();
        if (accountInfo.exists()) {
            if (animate) {
                UiUtils.animateSwitchText(mTvUsername, accountInfo.getUsername(), null);
                UiUtils.animateSwitchText(mTvProfile, accountInfo.getAcademy(), null);
            } else {
                mTvUsername.setText(accountInfo.getUsername());
                mTvProfile.setText(accountInfo.getAcademy());
            }
            UiUtils.makeVisible(mTvSubscriptionInfo);
            if (accountInfo.isSubscriptionDue()) {
                UiUtils.setBackgroundTint(mTvSubscriptionInfo, R.color.unsubscribed_tint);
                mTvSubscriptionInfo.setText(R.string.unsubscribed);
            } else {
                UiUtils.setBackgroundTint(mTvSubscriptionInfo, R.color.golden);
                setSbpText(accountInfo.getSubscriptionDueDate());
            }
        } else {
            if (animate) {
                UiUtils.animateSwitchTextRes(mTvUsername, R.string.title_mine, null);
                UiUtils.animateSwitchTextRes(mTvProfile, R.string.not_logged_in, null);
            } else {
                mTvUsername.setText(R.string.title_mine);
                mTvProfile.setText(R.string.not_logged_in);
            }
            UiUtils.makeGone(mTvSubscriptionInfo);
        }
    }

    private void initSharedElements() {
        if (AccountInfo.mine().exists()) {
            mIvAvatar.setImageResource(AccountInfo.mine().getAvatarRes());
            mBtnLogInOrSubscribe.setCompoundDrawablesRelativeWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_attach_money_24dp), null, null, null);
            mBtnLogInOrSubscribe.setCompoundDrawablePadding(12);
            mBtnLogInOrSubscribe.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.brown)));
            mBtnLogInOrSubscribe.setText(R.string.subscribe_service);
        } else {
            mIvAvatar.setImageResource(R.drawable.ic_account_circle_48dp);
            mBtnLogInOrSubscribe.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
            mBtnLogInOrSubscribe.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            mBtnLogInOrSubscribe.setText(R.string.log_in_to);
        }
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mUpdateReceiver = new AccountInfoUpdateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(AshApp.ACTION_LOG_OUT);
        filter.addAction(AshApp.ACTION_SBP_UPDATED);
        filter.addAction(AshApp.ACTION_REGISTERED);
        filter.addAction(AshApp.ACTION_LOG_IN);
        requireActivity().registerReceiver(mUpdateReceiver, filter);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        requireActivity().unregisterReceiver(mUpdateReceiver);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_log_in_or_subscribe) {
            if (AccountInfo.mine().exists()) {
                Intent goToSubscribe = new Intent(mActivity, SubscribeActivity.class);
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(mActivity, new Pair<>(v, v.getTransitionName())).toBundle();
                startActivityForResult(goToSubscribe, 0, bundle);
            } else {
                Intent goToLogIn = new Intent(mActivity, LoginActivity.class);
                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(mActivity, new Pair<>(v, v.getTransitionName())
                        , new Pair<>(mIvAvatar, getString(R.string.tn_image_view))).toBundle();
                startActivityForResult(goToLogIn, 0, bundle);
            }
        }
    }


    public void showUserInfo(View view) {
        if (AccountInfo.mine().exists()) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity(), view, view.getTransitionName()).toBundle();
            startActivity(new Intent(getActivity(), AccountInfoActivity.class), bundle);
        } else {
            mBtnLogInOrSubscribe.performClick();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mQueryCall != null) {
            mQueryCall.cancel();
        }
    }


    private class AccountInfoUpdateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (getLifecycle().getCurrentState() == Lifecycle.State.STARTED) {
                initViews(true);
            } else {
                initViews(false);
            }
        }
    }


    private class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuViewHolder> {
        private String[] menuCaptions;
        private int[] menuIconsResources;

        MenuAdapter() {
            menuCaptions = getResources().getStringArray(R.array.menu_captions);
            TypedArray ta = getResources().obtainTypedArray(R.array.menu_icons);
            menuIconsResources = new int[menuCaptions.length];
            for (int i = 0; i < menuIconsResources.length; i++) {
                menuIconsResources[i] = ta.getResourceId(i, -1);
            }
            ta.recycle();
        }

        @NonNull
        @Override
        public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
            return new MenuViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
            holder.tvCaption.setText(menuCaptions[position]);
            holder.ivIcon.setImageResource(menuIconsResources[position]);
        }

        @Override
        public int getItemCount() {
            return menuCaptions.length;
        }

        private class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            ImageView ivIcon;
            TextView tvCaption;

            MenuViewHolder(@NonNull View itemView) {
                super(itemView);
                ivIcon = itemView.findViewById(R.id.iv_icon);
                tvCaption = itemView.findViewById(R.id.tv_caption);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                switch (getBindingAdapterPosition()) {
                    case 0:
                        if (AccountInfo.mine().exists()) {
                            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle();
                            startActivity(new Intent(getActivity(), HistoryActivity.class), bundle);
                        } else {
                            UiUtils.showShortAshToast(requireActivity(), getString(R.string.pls_login_first));
                        }
                        break;
                    case 1:
                        UiUtils.showShortAshToast(requireActivity(), R.string.under_construction);
                        break;
                    case 2:
                        if (AccountInfo.mine().exists()) {
                            String body = getString(R.string.mail_body, String.format("%tc", new Date()), AccountInfo.mine().AccID, UniUtils.getEssentialHardwareInfo(), UniUtils.getVersionInfo());
                            body = body.replaceAll("\\n", "<br/>");
                            String uri = getString(R.string.mailto_uri, AccountInfo.mine().getUsername(), body);
                            Intent mail = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                            startActivity(mail);
                        } else {
                            UiUtils.showShortAshToast(requireActivity(), getString(R.string.pls_login_first));
                        }
                        break;
                    case 3:
                        UiUtils.showShortAshToast(requireActivity(), R.string.under_construction);
                        break;
                    case 4:
                        new AlertDialog.Builder(requireActivity(), R.style.Theme_AppCompat_Light_Dialog_Alert_Material)
                                .setTitle(R.string.about)
                                .setMessage(getString(R.string.app_name) + "v" + AshApp.VERSION_NAME)
                                .setPositiveButton(android.R.string.ok, null)
                                .show();
                        break;
                }
            }
        }
    }
}



