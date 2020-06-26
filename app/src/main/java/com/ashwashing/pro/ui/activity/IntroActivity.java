package com.ashwashing.pro.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.ui.fragment.intro.IntroFragment1;
import com.ashwashing.pro.ui.fragment.intro.IntroFragment2;
import com.ashwashing.pro.ui.fragment.intro.IntroFragment3;
import com.github.paolorotolo.appintro.AppIntro;

public class IntroActivity extends AppIntro {
    private IntroFragment1 mPage1;
    private IntroFragment2 mPage2;
    private IntroFragment3 mPage3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setOffScreenPageLimit(2);
        showSkipButton(false);
        showSeparator(false);
        initSliders();
        addSlide(mPage1);
        addSlide(mPage2);
        addSlide(mPage3);
    }

    private void initSliders() {
        mPage1 = new IntroFragment1();
        mPage2 = new IntroFragment2();
        mPage3 = new IntroFragment3();
    }

    @Override
    public void onBackPressed() {
        if (getPager().getCurrentItem() != 0) {
            super.onBackPressed();
        } else {
            finishAffinity();
        }
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        if (AshApp.getSharedPrefsManager().isAppIntroDone()) {
            AshApp.getSharedPrefsManager().updateRecordedVersionCode();
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);

        } else {
            mPage3.notifyPermissionNotGranted();
        }
    }
}
