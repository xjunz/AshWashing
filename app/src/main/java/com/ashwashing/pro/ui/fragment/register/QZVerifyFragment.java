package com.ashwashing.pro.ui.fragment.register;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ashwashing.pro.R;
import com.ashwashing.pro.api.Constants;
import com.ashwashing.pro.api.VerifyService;
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

public class QZVerifyFragment extends BaseRegisterFragment {
    private VerifyService mVerifyService;
    private TextInputEditText mEtPhone, mEtPassword;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVerifyService = ApiUtils.createGsonRetrofit().create(VerifyService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_qz_verify, container, false);
        mEtPhone = root.findViewById(R.id.et_username);
        mEtPassword = root.findViewById(R.id.et_password);
        mEtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    BaseActivity.hideIme(mEtPassword);
                    return true;
                }
                return false;
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

    private Dialog mProgressDialog;

    @Override
    public void go() {
        final String phone = Objects.requireNonNull(mEtPhone.getText()).toString();
        final String password = Objects.requireNonNull(mEtPassword.getText()).toString();
        if (TextUtils.isEmpty(phone) || phone.length() < 11 || UniUtils.illegalPhoneNumber(phone)) {
            showErrorToast(R.string.pls_enter_correct_phone);
            mEtPhone.requestFocus();
            BaseActivity.showIme(mEtPhone);
        } else if (TextUtils.isEmpty(password)) {
            showErrorToast(getString(R.string.pls_enter, getString(R.string.password)));
            mEtPassword.requestFocus();
            BaseActivity.hideIme(mEtPassword);
        } else if (password.length() < 6) {
            showErrorToast(getString(R.string.mallength,getString(R.string.password)));
            mEtPassword.requestFocus();
            BaseActivity.hideIme(mEtPassword);
        } else {
            mProgressDialog = UiUtils.createProgressDialog(requireContext(), R.string.verifying);
            mProgressDialog.show();
            mVerifyService.verify(ApiUtils.generateHeaderMap(), phone, Constants.USER_TYPE_QZ, password).enqueue(new retrofit2.Callback<SimpleResult>() {
                @Override
                public void onResponse(@NotNull Call<SimpleResult> call, @NotNull Response<SimpleResult> response) {
                    mProgressDialog.dismiss();
                    SimpleResult result = response.body();
                    if (result != null) {
                        if (result.code == Constants.CODE_SUCCESS) {
                            //here we verify successfully!
                            showShortToast(R.string.verify_succeeded);
                            getRegisterInfo().phone = phone;
                            getRegisterInfo().password = password;
                            getCallback().onDone(QZVerifyFragment.this);
                        } else {
                            showErrorToast(result.msg);
                        }
                    } else {
                        showErrorToast(R.string.unknown_error);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<SimpleResult> call, @NotNull Throwable t) {
                    mProgressDialog.dismiss();
                    showErrorToast(R.string.network_or_server_error);
                }
            });
        }

    }

    @Override
    public int getStepIndex() {
        return 1;
    }


}