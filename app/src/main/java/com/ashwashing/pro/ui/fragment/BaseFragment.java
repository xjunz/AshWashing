package com.ashwashing.pro.ui.fragment;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.ui.activity.BaseActivity;

public class BaseFragment extends Fragment {
    private BaseActivity mBaseActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (requireActivity() instanceof BaseActivity) {
            mBaseActivity = (BaseActivity) requireActivity();
        } else {
            throw new RuntimeException(getClass().getName() + "'s context is not a instance of BaseActivity");
        }
    }


    boolean isNetworkAvailable() {
        return mBaseActivity.isNetworkAvailable();
    }

    protected void countDown(String tag, long total, Runnable onTick, Runnable onFinish) {
        AshApp.getCountDownerManager().getCountDowner(tag).countDown(total, getLifecycle(), onTick, onFinish);
    }


    public long getLeftoverMills(String tag) {
        return AshApp.getCountDownerManager().getCountDowner(tag).getLeftoverMills();
    }
}
