package com.ashwashing.pro.ui.fragment.register;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.DialerKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ashwashing.pro.R;
import com.ashwashing.pro.api.Constants;
import com.ashwashing.pro.api.RegisterService;
import com.ashwashing.pro.api.bean.AccountInfo;
import com.ashwashing.pro.api.bean.SimpleResult;
import com.ashwashing.pro.api.converter.AshGsonConverterFactory;
import com.ashwashing.pro.ui.activity.BaseActivity;
import com.ashwashing.pro.util.ApiUtils;
import com.ashwashing.pro.util.UiUtils;
import com.ashwashing.pro.util.UniUtils;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.ashwashing.pro.api.bean.AccountInfo.getRegisterInfo;

public class RegisterFragment extends BaseRegisterFragment {
    private TextInputEditText mEtUsername, mEtPassword, mEtPasswordAgain;
    private RegisterService mRegisterService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRegisterService = ApiUtils.createGsonRetrofit().create(RegisterService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);
        mEtUsername = root.findViewById(R.id.et_username);
        mEtPassword = root.findViewById(R.id.et_password);
        mEtPasswordAgain = root.findViewById(R.id.et_password_confirm);
        mEtUsername.setKeyListener(new DialerKeyListener() {
            @NotNull
            @Override
            protected char[] getAcceptedChars() {
                return "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
            }

            @Override
            public int getInputType() {
                return InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
            }
        });
        return root;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public void notifyUndone() {

    }


    @Override
    public void go() {
        final String username = Objects.requireNonNull(mEtUsername.getText()).toString();
        final String password = Objects.requireNonNull(mEtPassword.getText()).toString();
        String password2 = Objects.requireNonNull(mEtPasswordAgain.getText()).toString();

        if (TextUtils.isEmpty(username) || username.length() < 4 || username.matches("^\\d.+")) {
            showErrorToast(getString(R.string.malformat, getString(R.string.username)));
            mEtUsername.requestFocus();
            BaseActivity.showIme(mEtUsername);
        } else if (TextUtils.isEmpty(password)) {
            showErrorToast(getString(R.string.pls_enter, getString(R.string.password)));
            mEtPassword.requestFocus();
            BaseActivity.hideIme(mEtPassword);
        } else if (password.length() < 6) {
            showErrorToast(getString(R.string.mallength, getString(R.string.password)));
            mEtPassword.requestFocus();
            BaseActivity.hideIme(mEtPassword);
        } else if (!password.equals(password2)) {
            showErrorToast(R.string.pwd_not_correspond);
            mEtPasswordAgain.requestFocus();
            BaseActivity.hideIme(mEtPasswordAgain);
        } else {
            final Dialog dialog = UiUtils.createProgressDialog(requireContext(),R.string.posting);
            dialog.show();
            AccountInfo.RegisterInfo info = getRegisterInfo();
            mRegisterService.register(ApiUtils.generateHeaderMap(),info.phone, info.userType, info.password, username, password).enqueue(new retrofit2.Callback<SimpleResult>() {
                @Override
                public void onResponse(@NotNull Call<SimpleResult> call, @NotNull Response<SimpleResult> response) {
                    dialog.dismiss();
                    SimpleResult result = response.body();
                    if (result == null) {
                        showErrorToast(R.string.unknown_error);
                    } else {
                        if (result.code == Constants.CODE_SUCCESS) {
                            showShortToast(R.string.register_succeeded);
                            getRegisterInfo().username = username;
                            getRegisterInfo().accountPwd = password;
                            getCallback().onDone(RegisterFragment.this);
                        } else {
                            showErrorToast(result.msg);
                        }
                    }
                }

                @Override
                public void onFailure(@NotNull Call<SimpleResult> call, @NotNull Throwable t) {
                    dialog.dismiss();
                    showErrorToast(R.string.network_or_server_error);
                }
            });
        }

    }

    @Override
    public int getStepIndex() {
        return 2;
    }
}
