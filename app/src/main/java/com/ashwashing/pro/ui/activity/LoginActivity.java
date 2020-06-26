package com.ashwashing.pro.ui.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.R;
import com.ashwashing.pro.api.AccountInfoQueryService;
import com.ashwashing.pro.api.LoginService;
import com.ashwashing.pro.api.bean.AccountInfo;
import com.ashwashing.pro.api.bean.DataResult;
import com.ashwashing.pro.util.ApiUtils;
import com.ashwashing.pro.util.UiUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import retrofit2.Callback;

public class LoginActivity extends BaseActivity implements View.OnKeyListener {

    private ViewGroup mPanel;
    private ViewGroup mRootView;
    private TextInputEditText mEtUsername;
    private TextInputEditText mEtPwd;
    private ViewGroup mContainer;
    private ProgressBar mPbLogIn;
    private ImageView mIvAvatar;
    public static final int REQUEST_CODE_REGISTER = 0x1a;
    private RegisterReceiver mRegisterReceiver;
    private LoginService mLoginService;
    private AccountInfoQueryService mQueryService;
    public static final String EXTRA_ALLOW_REGISTER = "ash.extra.allowRegister";

    public static void showAsLogin(Activity from) {
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(from).toBundle();
        Intent i = new Intent(from, LoginActivity.class);
        i.putExtra(EXTRA_ALLOW_REGISTER, false);
        from.startActivity(i, bundle);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boolean allowRegister = getIntent().getBooleanExtra(EXTRA_ALLOW_REGISTER, true);
        if (!allowRegister) {
            findViewById(R.id.btn_register).setVisibility(View.GONE);
        }
        mRootView = findViewById(R.id.root);
        mPanel = findViewById(R.id.login_panel);
        mEtUsername = findViewById(R.id.et_qz_username);
        String name = AshApp.getSharedPrefsManager().getLastLoginUserName();
        if (name != null) {
            mEtUsername.setText(name);
        }
        mEtPwd = findViewById(R.id.et_pwd);
        mEtPwd.setOnKeyListener(this);
        mContainer = findViewById(R.id.cl_container);
        mPbLogIn = findViewById(R.id.pb_login);
        mIvAvatar = findViewById(R.id.iv_avatar);
        IntentFilter filter = new IntentFilter();
        filter.addAction(AshApp.ACTION_REGISTERED);
        mRegisterReceiver = new RegisterReceiver();
        registerReceiver(mRegisterReceiver, filter);
        mLoginService = ApiUtils.createGsonRetrofit().create(LoginService.class);
        mQueryService = ApiUtils.createGsonRetrofit().create(AccountInfoQueryService.class);
    }


    public void logIn(View view) {
        final String username = Objects.requireNonNull(mEtUsername.getText()).toString();
        final String password = Objects.requireNonNull(mEtPwd.getText()).toString();
        if (TextUtils.isEmpty(username) || username.length() < 4 || username.matches("^\\d.+")) {
            UiUtils.showErrorToast(this, getString(R.string.malformat, getString(R.string.username)));
            mEtUsername.requestFocus();
            BaseActivity.showIme(mEtUsername);
        } else if (TextUtils.isEmpty(password)) {
            UiUtils.showErrorToast(this, getString(R.string.pls_enter, getString(R.string.password)));
            mEtPwd.requestFocus();
            BaseActivity.showIme(mEtPwd);
        } else if (password.length() < 6) {
            UiUtils.showErrorToast(this, getString(R.string.mallength, getString(R.string.password)));
            mEtPwd.requestFocus();
            BaseActivity.showIme(mEtPwd);
        } else {
            hideIme(mEtPwd);
            transitToLoggingIn(new Runnable() {
                @Override
                public void run() {
                    mLoginService.logIn(ApiUtils.generateHeaderMap(), username, password).enqueue(mLoginCallback);
                }
            });
        }
    }

    private void transitToLoggingIn(final Runnable actionAfterTransition) {
        setBlockBackPress(true);
        TransitionManager.beginDelayedTransition(mRootView, new AutoTransition().addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                if (actionAfterTransition != null) {
                    actionAfterTransition.run();
                }
            }
        }));
        mPanel.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
        UiUtils.makeGone(mContainer);
        UiUtils.makeVisible(mPbLogIn);
    }

    private void transitToInput(final Runnable actionAfterTransition) {
        TransitionManager.beginDelayedTransition(mRootView, new AutoTransition().addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                if (actionAfterTransition != null) {
                    actionAfterTransition.run();
                }
                setBlockBackPress(false);
            }
        }));
        mPanel.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        UiUtils.makeGone(mPbLogIn);
        UiUtils.makeVisible(mContainer);
    }


    public void gotoRegister(View view) {
        Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this, view, getString(R.string.tn_button)).toBundle();
        Intent i = new Intent(this, RegisterActivity.class);
        //startActivity(i);
        startActivityForResult(i, REQUEST_CODE_REGISTER, bundle);
    }


    public void onLogInSuccess() {
        UiUtils.showShortAshToast(this, getString(R.string.login_succeeded));
        AshApp.getSharedPrefsManager().setLastLoginUserName(Objects.requireNonNull(mEtUsername.getText()).toString());
        mIvAvatar.setImageResource(AccountInfo.mine().getAvatarRes());
        mIvAvatar.postDelayed(() -> {
            getWindow().setSharedElementReturnTransition(TransitionInflater.from(LoginActivity.this).inflateTransition(R.transition.login_shared_return));
            sendBroadcast(new Intent(AshApp.ACTION_LOG_IN));
            finishAfterTransition();
        }, 500L);
    }


    public void onLogInFail() {
      /*  transitToInput(new Runnable() {
            @Override
            public void run() {
                UiUtils.showErrorToast(LoginActivity.this, reason);
            }
        });*/
        transitToInput(null);
    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
            logIn(null);
        }
        return false;
    }

    private Callback<DataResult<AccountInfo>> mLoginCallback = new ApiUtils.DataResultCallbackAdapter<DataResult<AccountInfo>>(this) {
        @Override
        public void onSuccess(DataResult<AccountInfo> result) {
            AccountInfo.mine().set(result.getData());
            //query account information
            mQueryService.queryAccountInfo(ApiUtils.generateHeaderMap()).enqueue(new ApiUtils.DataResultCallbackAdapter<DataResult<AccountInfo>>(LoginActivity.this) {
                @Override
                public void onSuccess(DataResult<AccountInfo> result) {
                    AccountInfo.mine().set(result.getData());
                    //generate a random avatar for the user :)
                    AccountInfo.mine().generateAvatar();
                    //persist the account
                    AccountInfo.mine().persist();
                    //notify UI
                    onLogInSuccess();
                }

                @Override
                public void onNotSuccess() {
                    onLogInFail();
                }
            }.setPrefixRes(R.string.query_acc_info_error));
        }

        @Override
        public void onNotSuccess() {
            onLogInFail();
        }
    }.setPrefixRes(R.string.login_failed);


    private class RegisterReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (AshApp.ACTION_REGISTERED.equals(intent.getAction())) {
                mEtUsername.setText(AccountInfo.mine().getUsername());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mRegisterReceiver);
    }
}
