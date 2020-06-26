package com.ashwashing.pro.ui.fragment.intro;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.Fragment;
import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.R;

public class IntroFragment3 extends Fragment implements View.OnClickListener {

    private TextView mTvCaption;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final int REQUEST_FINE_LOCATION = 0xa1;
    private Button mBtnOperate;
    private ImageView mIvIntro;
    private Activity mContext;
    private boolean mHasRequested;
    private boolean mShouldRefresh;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = (Activity) context;
    }


    private boolean hasFINELocationPermission() {
        int result = PermissionChecker.checkSelfPermission(mContext, FINE_LOCATION);
        //先检查有没有被app ops拒绝
        if (result == PermissionChecker.PERMISSION_DENIED_APP_OP || result == PermissionChecker.PERMISSION_DENIED) {
            return false;
        } else {
            return result == PermissionChecker.PERMISSION_GRANTED;
        }
    }

    private boolean isFINELocationPermissionBanned() {
        return mHasRequested && !ActivityCompat.shouldShowRequestPermissionRationale(mContext, FINE_LOCATION);
    }

    private void gotoApplicationDetails() {
        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.setData(Uri.parse("package:" + mContext.getPackageName()));
        mContext.startActivity(i);
        mShouldRefresh = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View content = inflater.inflate(R.layout.fragment_intro_3, container, false);
        mTvCaption = content.findViewById(R.id.tv_desc);
        mBtnOperate = content.findViewById(R.id.btn_operate);
        mIvIntro = content.findViewById(R.id.iv_intro);
        mBtnOperate.setOnClickListener(this);
        return content;
    }

    private void refreshState() {
        if (hasFINELocationPermission()) {
            //set done
            AshApp.getSharedPrefsManager().setIsAppIntroDone(true);

            mIvIntro.setImageResource(R.drawable.ic_sentiment_very_satisfied_144dp);
            mTvCaption.setText(R.string.permission_granted);
            mBtnOperate.setEnabled(false);
            mBtnOperate.animate().alpha(0).start();

        } else if (isFINELocationPermissionBanned()) {
            mIvIntro.setImageResource(R.drawable.ic_sentiment_very_dissatisfied_144dp);
            mTvCaption.setText(R.string.permission_denied);
            mBtnOperate.setVisibility(View.VISIBLE);
            mBtnOperate.setText(R.string.goto_setting);
        } else {
            mIvIntro.setImageResource(R.drawable.ic_sentiment_dissatisfied_144dp);
            mTvCaption.setText(R.string.permission_denied);
            mBtnOperate.setVisibility(View.VISIBLE);
            mBtnOperate.setText(R.string.retry);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_FINE_LOCATION) {
            if (FINE_LOCATION.equals(permissions[0])) {
                mHasRequested = true;
                refreshState();
            }
        }
    }

    public void notifyPermissionNotGranted() {
        ObjectAnimator swing = ObjectAnimator.ofFloat(mIvIntro, View.TRANSLATION_X, 0, -25, 20, -15, 10, -5, 0);
        swing.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mShouldRefresh) {
            refreshState();
            mShouldRefresh = false;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_operate) {
            CharSequence text = mBtnOperate.getText();
            if (getString(R.string.request_permission).contentEquals(text) || getString(R.string.retry).contentEquals(text)) {
                requestPermissions(new String[]{FINE_LOCATION}, REQUEST_FINE_LOCATION);
            } else {
                gotoApplicationDetails();
            }
        }
    }
}
