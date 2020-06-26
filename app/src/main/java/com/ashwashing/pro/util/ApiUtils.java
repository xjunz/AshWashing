package com.ashwashing.pro.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.webkit.WebSettings;

import androidx.annotation.StringRes;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.R;
import com.ashwashing.pro.api.Constants;
import com.ashwashing.pro.api.bean.AccountInfo;
import com.ashwashing.pro.api.bean.DataResult;
import com.ashwashing.pro.api.bean.ListResult;
import com.ashwashing.pro.api.bean.SimpleResult;
import com.ashwashing.pro.api.converter.AshGsonConverterFactory;
import com.ashwashing.pro.ui.activity.LoginActivity;

import org.apaches.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.ashwashing.pro.util.CryptUtils.specEncrypt;

public class ApiUtils {
    public abstract static class SimpleResultCallbackAdapter<T extends SimpleResult> implements Callback<T> {
        Activity mActivity;
        int mErrorPrefixRes;


        public SimpleResultCallbackAdapter(Activity activity) {
            mActivity = activity;
        }

        /**
         * Called whenever it's {@link SimpleResultCallbackAdapter#onResponse(Call, Response)} or {@link SimpleResultCallbackAdapter#onFailure(Call, Throwable)}
         */
        public void onWhatever() {
        }

        /**
         * Called only when {@link SimpleResult#code} equals to {@link Constants#CODE_SUCCESS}
         */
        public abstract void onSuccess(T result);

        /**
         * Called whenever it's not {@link SimpleResultCallbackAdapter#onSuccess(SimpleResult)},including {@link SimpleResultCallbackAdapter#onFailure(Call, Throwable)},
         * {@link SimpleResultCallbackAdapter#onError(int, String)},{@link SimpleResultCallbackAdapter#onIllegalAccess()} and {@link SimpleResultCallbackAdapter#onNullResult()}
         */
        public void onNotSuccess() {
        }

        /**
         * Called when {@link DataResult#code} does not equal to {@link Constants#CODE_SUCCESS} or {@link Constants#CODE_INVALID_ACCESS_TOKEN}
         */
        public void onError(int code, String msg) {
            if (mErrorPrefixRes != 0) {
                UiUtils.showErrorToast(mActivity, mActivity.getString(mErrorPrefixRes, msg));
            } else {
                UiUtils.showErrorToast(mActivity, mActivity.getString(R.string.error_format, code, msg));
            }
            onNotSuccess();
        }

        /**
         * Called when {@link DataResult} is null or {@link DataResult#getData()} is null
         */
        public void onNullResult() {
            UiUtils.showErrorToast(mActivity, R.string.unknown_error);
            onNotSuccess();
        }

        /**
         * Called only when {@link DataResult#code} equals to {@link Constants#CODE_INVALID_ACCESS_TOKEN}
         */
        public void onIllegalAccess() {
            AccountInfo.mine().invalidate();
            mActivity.sendBroadcast(new Intent(AshApp.ACTION_LOG_OUT));
            UiUtils.showErrorToast(mActivity, R.string.illegal_access);
            LoginActivity.showAsLogin(mActivity);
            onNotSuccess();
        }


        public Callback<T> setPrefixRes(@StringRes int res) {
            mErrorPrefixRes = res;
            return this;
        }


        @Override
        public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {
            onWhatever();
            T result = response.body();
            if (result != null) {
                if (result.code == Constants.CODE_SUCCESS) {
                    onSuccess(result);
                } else if (result.code == Constants.CODE_INVALID_ACCESS_TOKEN) {
                    onIllegalAccess();
                } else {
                    onError(result.code, result.msg);
                }
            } else {
                onNullResult();
            }
        }

        @Override
        public void onFailure(@NotNull Call<T> call, @NotNull Throwable t) {
            onWhatever();
            UiUtils.showErrorToast(mActivity, R.string.network_or_server_error);
            onNotSuccess();
        }
    }

    public abstract static class DataResultCallbackAdapter<T extends DataResult> extends SimpleResultCallbackAdapter<T> {


        public DataResultCallbackAdapter(Activity activity) {
            super(activity);
        }

        public void onNullData() {
            UiUtils.showErrorToast(mActivity, R.string.unknown_error);
            onNotSuccess();
        }

        @Override
        public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {
            onWhatever();
            T result = response.body();
            if (result != null) {
                if (result.code == Constants.CODE_SUCCESS) {
                    if (result.getData() == null) {
                        onNullData();
                    } else {
                        onSuccess(result);
                    }
                } else if (result.code == Constants.CODE_INVALID_ACCESS_TOKEN) {
                    onIllegalAccess();
                } else {
                    onError(result.code, result.msg);
                }
            } else {
                onNullResult();
            }
        }
    }

    public abstract static class ListResultCallbackAdapter<T extends ListResult> extends SimpleResultCallbackAdapter<T> {

        public ListResultCallbackAdapter(Activity activity) {
            super(activity);
        }

        public void onNullList() {
            UiUtils.showErrorToast(mActivity, R.string.unknown_error);
            onNotSuccess();
        }

        @Override
        public void onResponse(@NotNull Call<T> call, @NotNull Response<T> response) {
            onWhatever();
            T result = response.body();
            if (result != null) {
                if (result.code == Constants.CODE_SUCCESS) {
                    if (result.getAll() == null) {
                        onNullList();
                    } else {
                        onSuccess(result);
                    }
                } else if (result.code == Constants.CODE_INVALID_ACCESS_TOKEN) {
                    onIllegalAccess();
                } else {
                    onError(result.code, result.msg);
                }
            } else {
                onNullResult();
            }
        }
    }

    private static final String NAME_UUID = "uuid";
    private static String sUUIDString;

    private static String getUniqueDeviceId() {
        if (sUUIDString == null) {
            UUID uuid = AshApp.getSerializedObjManager().read(NAME_UUID, UUID.class);
            if (uuid == null) {
                uuid = UUID.randomUUID();
                AshApp.getSerializedObjManager().write(uuid, NAME_UUID);
            }
            sUUIDString = uuid.toString();
        }
        @SuppressLint("HardwareIds") String id = Build.BRAND
                + Build.MODEL //useful for pre-o devices
                + Build.SERIAL
                + Settings.Secure.ANDROID_ID
                + sUUIDString;
        return id;
    }

    private static String sUerAgent;

    private static String getWebUserAgent() {
        if (sUerAgent == null) {
            sUerAgent = WebSettings.getDefaultUserAgent(AshApp.APP_CONTEXT.get());
        }
        return sUerAgent;
    }

    private static String sSignatureMD5;

    private static String getVerification() {
        if (sSignatureMD5 == null) {
            byte[] bytes = getBluetoothId();
           // sSignatureMD5 = DigestUtils.md5Hex(bytes).toUpperCase();
        }
        //TODO: delete this
        sSignatureMD5 = "F1E3F50BE635FD6495D6E0C957B38532";

        return sSignatureMD5;
    }


    //实际是获取签名
    private static native byte[] getBluetoothId();


    public static Map<String, String> generateHeaderMap() {
        String timestamp = String.valueOf(System.currentTimeMillis());
        HashMap<String, String> headers = new HashMap<>();
        headers.put(Constants.HEADER_FIELD_AUTH_TOKEN, AccountInfo.mine().getAccessToken());
        headers.put(Constants.HEADER_FIELD_DEVICE_ID, specEncrypt(getUniqueDeviceId(), timestamp));
        headers.put(Constants.HEADER_FIELD_USER_AGENT, specEncrypt(getWebUserAgent(), timestamp));
        headers.put(Constants.HEADER_FIELD_APP_SIGNATURE, specEncrypt(getVerification(), timestamp));
        headers.put(Constants.HEADER_FIELD_APP_VERSION, AshApp.VERSION_NAME);
        headers.put(Constants.HEADER_FIELD_APP_TIME, timestamp);
        return headers;
    }

    public static Retrofit createGsonRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS).proxy(Proxy.NO_PROXY).build();
        return new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(client)
                .addConverterFactory(AshGsonConverterFactory.create())
                .build();
    }

    public static Retrofit createGsonRetrofit(int connTimeoutInSec) {
        OkHttpClient client = new OkHttpClient.Builder().connectTimeout(connTimeoutInSec, TimeUnit.SECONDS).proxy(Proxy.NO_PROXY).build();
        return new Retrofit.Builder()
                .baseUrl(Constants.API_BASE_URL)
                .client(client)
                .addConverterFactory(AshGsonConverterFactory.create())
                .build();
    }


}
