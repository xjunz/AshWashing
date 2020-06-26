package com.ashwashing.pro.ui.fragment.register;

import android.view.Gravity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.ashwashing.pro.R;
import com.ashwashing.pro.customview.AshToast;
import com.ashwashing.pro.ui.fragment.BaseFragment;

public abstract class BaseRegisterFragment extends BaseFragment {
    public static final int STEP_COUNT = 3;
    private Callback mCallback;


    public abstract boolean isDone();

    public abstract void notifyUndone();

    public abstract void go();

    public interface Callback {
        void onDone(BaseRegisterFragment fragment);

        void onUndone(BaseRegisterFragment fragment);
    }

    public void setCallback(@NonNull Callback callback) {
        mCallback = callback;
    }

    Callback getCallback() {
        return mCallback;
    }

    public abstract int getStepIndex();

    void showErrorToast(String msg) {
        AshToast toast = AshToast.makeText(requireContext(), msg, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 400);
        toast.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        toast.show();
    }

    void showErrorToast(int msgRes) {
        AshToast toast = AshToast.makeText(requireContext(), msgRes, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 400);
        toast.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        toast.show();
    }

    void showShortToast(int msgRes) {
        AshToast toast = AshToast.makeText(requireContext(), msgRes, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 400);
        toast.show();
    }
}