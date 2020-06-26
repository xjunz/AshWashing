package com.ashwashing.pro.ui.fragment.register;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.ashwashing.pro.R;
import com.ashwashing.pro.api.CaptchaService;
import com.ashwashing.pro.api.Constants;
import com.ashwashing.pro.api.SendVerifyCodeService;
import com.ashwashing.pro.api.VerifyService;
import com.ashwashing.pro.api.bean.SimpleResult;
import com.ashwashing.pro.ui.activity.BaseActivity;
import com.ashwashing.pro.util.ApiUtils;
import com.ashwashing.pro.util.UiUtils;
import com.ashwashing.pro.util.UniUtils;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.ashwashing.pro.api.bean.AccountInfo.getRegisterInfo;

public class HYVerifyFragment extends BaseRegisterFragment implements View.OnClickListener {
    private VerifyService mVerifyService;
    private CaptchaService mCaptchaService;
    private SendVerifyCodeService mSendVerifyCodeService;
    private TextInputEditText mEtPhone, mEtVerifyCode;
    private Button mBtnObtain;
    private static final String CAPTCHA_COUNT_DOWN_TAG = "ash.tag.countdown.captcha";
    private static final String VRF_CODE_COUNT_DOWN_TAG = "ash.tag.countdown.vrf_code";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVerifyService = ApiUtils.createGsonRetrofit().create(VerifyService.class);
        mCaptchaService = new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .build().create(CaptchaService.class);
        mSendVerifyCodeService = ApiUtils.createGsonRetrofit().create(SendVerifyCodeService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_hy_verify, container, false);
        mEtPhone = root.findViewById(R.id.et_username);
        mEtVerifyCode = root.findViewById(R.id.et_verify_code);
        mEtVerifyCode.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    BaseActivity.hideIme(mEtVerifyCode);
                    return true;
                }
                return false;
            }
        });
        mBtnObtain = root.findViewById(R.id.btn_obtain_verify_code);
        mBtnObtain.setOnClickListener(this);
        if (getLeftoverMills(VRF_CODE_COUNT_DOWN_TAG) != 0) {
            countDownObtainButton(VRF_CODE_COUNT_DOWN_TAG, getLeftoverMills(VRF_CODE_COUNT_DOWN_TAG));
        } else if (getLeftoverMills(CAPTCHA_COUNT_DOWN_TAG) != 0) {
            countDownObtainButton(CAPTCHA_COUNT_DOWN_TAG, getLeftoverMills(CAPTCHA_COUNT_DOWN_TAG));
        }
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
        final String phone = Objects.requireNonNull(mEtPhone.getText()).toString();
        final String code = Objects.requireNonNull(mEtVerifyCode.getText()).toString();
        if (TextUtils.isEmpty(phone) || phone.length() < 11 || UniUtils.illegalPhoneNumber(phone)) {
            showErrorToast(R.string.pls_enter_correct_phone);
            mEtPhone.requestFocus();
            BaseActivity.showIme(mEtPhone);
        } else if (TextUtils.isEmpty(code) || code.length() < 6 || !TextUtils.isDigitsOnly(code)) {
            showErrorToast(getString(R.string.malformat, getString(R.string.vrf_code)));
            mEtVerifyCode.requestFocus();
            BaseActivity.showIme(mEtVerifyCode);
        } else {
            final Dialog progressDialog = UiUtils.createProgressDialog(requireContext(), R.string.verifying);
            progressDialog.show();
            mVerifyService.verify(ApiUtils.generateHeaderMap(), phone, Constants.USER_TYPE_HY, code).enqueue(new retrofit2.Callback<SimpleResult>() {
                @Override
                public void onResponse(@NotNull Call<SimpleResult> call, @NotNull Response<SimpleResult> response) {
                    progressDialog.dismiss();
                    SimpleResult result = response.body();
                    if (result != null) {
                        if (result.code == Constants.CODE_SUCCESS) {
                            //here we verify successfully!
                            showShortToast(R.string.verify_succeeded);
                            getRegisterInfo().phone = phone;
                            getRegisterInfo().password = code;
                            getCallback().onDone(HYVerifyFragment.this);
                        } else {
                            showErrorToast(getString(R.string.verify_failed_and, result.msg));
                        }
                    } else {
                        showErrorToast(R.string.unknown_error);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<SimpleResult> call, @NotNull Throwable t) {
                    progressDialog.dismiss();
                    showErrorToast(R.string.network_or_server_error);
                }
            });

        }
    }

    @Override
    public int getStepIndex() {
        return 1;
    }

    private void countDownObtainButton(final String tag, long mills) {
        countDown(tag, mills, new Runnable() {
            @Override
            public void run() {
                mBtnObtain.setEnabled(false);
                mBtnObtain.setText(getString(R.string.try_later_after, (int) Math.ceil(getLeftoverMills(tag) / 1000f)));
            }
        }, new Runnable() {
            @Override
            public void run() {
                mBtnObtain.setEnabled(true);
                mBtnObtain.setText(getString(R.string.obtain_verify_code));
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_obtain_verify_code) {

            final String phone = Objects.requireNonNull(mEtPhone.getText()).toString();
            if (TextUtils.isEmpty(phone) || phone.length() < 11 || UniUtils.illegalPhoneNumber(phone)) {
                showErrorToast(R.string.pls_enter_correct_phone);
                mEtPhone.requestFocus();
                BaseActivity.showIme(mEtPhone);
            } else {
                @SuppressLint("InflateParams") View root = getLayoutInflater().inflate(R.layout.dialog_captcha, null);
                final ProgressBar progressBar = root.findViewById(R.id.pb_captcha);
                final EditText editText = root.findViewById(R.id.et_captcha);
                final ImageView imageView = root.findViewById(R.id.iv_captcha);
                final Call<ResponseBody> call = mCaptchaService.getCaptcha(ApiUtils.generateHeaderMap(), phone);
                new AlertDialog.Builder(requireContext(), R.style.Theme_AppCompat_Light_Dialog_Alert_Material)
                        .setView(root)
                        .setTitle(R.string.input_captcha)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                final String captcha = editText.getText().toString();
                                if (captcha.matches("[\\w]{4}")) {
                                    mSendVerifyCodeService.trySend(ApiUtils.generateHeaderMap(), phone, captcha).enqueue(new retrofit2.Callback<SimpleResult>() {
                                        @Override
                                        public void onResponse(@NotNull Call<SimpleResult> call, @NotNull Response<SimpleResult> response) {
                                            SimpleResult result = response.body();
                                            if (result != null) {
                                                if (result.code == Constants.CODE_SUCCESS) {
                                                    showShortToast(R.string.vrf_code_sent);
                                                    countDownObtainButton(VRF_CODE_COUNT_DOWN_TAG, 60 * 1000L);
                                                } else {
                                                    showErrorToast(result.msg);
                                                }
                                            } else {
                                                showErrorToast(R.string.unknown_error);
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NotNull Call<SimpleResult> call, @NotNull Throwable t) {

                                        }
                                    });
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //cancel the previous call in case of unpredicted issues
                                if (call != null && !call.isCanceled()) {
                                    call.cancel();
                                }
                                BaseActivity.hideIme(editText);
                            }
                        }).show();

                call.enqueue(new retrofit2.Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        ResponseBody body = response.body();
                        if (body != null) {

                            countDownObtainButton(CAPTCHA_COUNT_DOWN_TAG, 3 * 1000L);

                            Bitmap bitmap = BitmapFactory.decodeStream(body.byteStream());
                            imageView.setImageBitmap(bitmap);
                            UiUtils.animateVisible(imageView);
                            UiUtils.animateGone(progressBar);
                            editText.requestFocus();
                            BaseActivity.showIme(editText);
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {

                    }
                });

            }
        }
    }
}
