package com.ashwashing.pro.ui.fragment.register;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ashwashing.pro.R;
import com.ashwashing.pro.api.Constants;

import static com.ashwashing.pro.api.bean.AccountInfo.getRegisterInfo;


public class PlatformSelectionFragment extends BaseRegisterFragment {
    private RadioButton mRbQz;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_platform_selection, container, false);
        mRbQz = root.findViewById(R.id.rb_qz);
        return root;
    }


    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public void notifyUndone() {
        //no-op
    }

    public boolean isQzSelected() {
        return mRbQz.isChecked();
    }

    @Override
    public void go() {
        getRegisterInfo().userType = isQzSelected() ? Constants.USER_TYPE_QZ : Constants.USER_TYPE_HY;
    }

    @Override
    public int getStepIndex() {
        return 0;
    }
}
