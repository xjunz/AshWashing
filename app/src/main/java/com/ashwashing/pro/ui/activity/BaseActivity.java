package com.ashwashing.pro.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.net.ConnectivityManagerCompat;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.R;
import com.ashwashing.pro.util.UiUtils;

import java.lang.reflect.Method;


@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    private static ConnectivityManager sConnManager;
    private IntentFilter mNetworkIntentFilter;
    private NetworkReceiver mNetworkReceiver;
    private ConnectivityManager.NetworkCallback mNetworkCallback;
    private Dialog mNetworkAlertDialog;
    private OnBackPressedCallback mBackPressEater = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            //no-op
        }
    };
    private boolean mIsTop;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (sConnManager == null) {
            sConnManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        }
        mNetworkAlertDialog = UiUtils.createAlert(this, R.string.msg_network_unavailable).setCancelable(false).create();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mNetworkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(Network network) {
                    super.onAvailable(network);
                    onNetworkAvailable();
                }

                @Override
                public void onLost(Network network) {
                    super.onLost(network);
                    onNetworkUnavailable();
                }

                @Override
                public void onUnavailable() {
                    super.onUnavailable();
                    onNetworkUnavailable();
                }
            };
           sConnManager.registerDefaultNetworkCallback(mNetworkCallback);
        } else {
            mNetworkReceiver = new NetworkReceiver();
            mNetworkIntentFilter = new IntentFilter();
            mNetworkIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerNetworkReceiverOrNetworkCallback();
        }
    }

    private void registerNetworkReceiverOrNetworkCallback() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, mNetworkIntentFilter);
        } else {
            sConnManager.registerDefaultNetworkCallback(mNetworkCallback);
        }
    }

    private void unregisterNetworkReceiverOrNetworkCallback() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            unregisterReceiver(mNetworkReceiver);
        } else {
            sConnManager.unregisterNetworkCallback(mNetworkCallback);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        registerNetworkReceiverOrNetworkCallback();
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterNetworkReceiverOrNetworkCallback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsTop = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mIsTop = true;
    }

    boolean shouldShowNetworkUnavailableAlert() {
        return true;
    }

    void onNetworkAvailable() {
        dismissNetworkUnavailableAlert();
    }


    void onNetworkUnavailable() {
        //Show only when this activity is the most front
        if (mIsTop && shouldShowNetworkUnavailableAlert()) {
            showNetworkUnavailableAlert();
        }
    }


    private class NetworkReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
                NetworkInfo info = ConnectivityManagerCompat.getNetworkInfoFromBroadcast(sConnManager, intent);
                if (info != null && info.isAvailable()) {
                    onNetworkAvailable();
                } else {
                    onNetworkUnavailable();
                }
            }
        }
    }

    public boolean isNetworkAvailable() {
        NetworkInfo info = sConnManager.getActiveNetworkInfo();
        return info != null && info.isAvailable();
    }

    void showNetworkUnavailableAlert() {
        if (mNetworkAlertDialog != null && !mNetworkAlertDialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mNetworkAlertDialog.show();
                }
            });
        }
    }

    void dismissNetworkUnavailableAlert() {
        if (mNetworkAlertDialog != null && mNetworkAlertDialog.isShowing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mNetworkAlertDialog.dismiss();
                }
            });
        }
    }

    public static void hideIme(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showIme(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService
                (Context.INPUT_METHOD_SERVICE);
        // the public methods don't seem to work for me, so try reflection.
        try {
            Method showSoftInputUnchecked = InputMethodManager.class.getMethod(
                    "showSoftInputUnchecked", int.class, ResultReceiver.class);
            showSoftInputUnchecked.setAccessible(true);
            showSoftInputUnchecked.invoke(imm, 0, null);
        } catch (Exception e) {
            // ho hum
        }
    }

    /**
     * Set whether to block back press event or not
     *
     * @param block whether to block or not
     */
    void setBlockBackPress(boolean block) {
        if (block) {
            getOnBackPressedDispatcher().addCallback(mBackPressEater);
        } else {
            mBackPressEater.remove();
        }
    }

    /**
     * Adapter for {@link android.transition.Transition.TransitionListener}
     */
    static class TransitionListenerAdapter implements Transition.TransitionListener {

        @Override
        public void onTransitionStart(Transition transition) {

        }

        @Override
        public void onTransitionEnd(Transition transition) {

        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }
    }


    public void countDown(String tag, long total, Runnable onTick, Runnable onFinish) {
        AshApp.getCountDownerManager().getCountDowner(tag).countDown(total, getLifecycle(), onTick, onFinish);
    }


    public long getLeftoverMills(String tag) {
        return AshApp.getCountDownerManager().getCountDowner(tag).getLeftoverMills();
    }


}
