package com.ashwashing.pro.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.R;
import com.ashwashing.pro.ui.fragment.HomeFragment;
import com.ashwashing.pro.ui.fragment.MineFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class MainActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "tag";
    private BottomNavigationView mNavigation;
    private FragmentManager mFragmentManager;
    private HomeFragment mFragmentHome;
    private MineFragment mFragmentMine;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //check hardware compatibility
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            new AlertDialog.Builder(this).setMessage(R.string.msg_unsupported_device)
                    .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //shut down VM
                            System.exit(0);
                        }
                    }).setCancelable(false).show();
            return;
        }

        //check app intro
        AshApp.SharedPrefsManager SPM = AshApp.getSharedPrefsManager();
        //版本3之前，未适配安卓Q的FINE_LOCATION权限
        if (SPM.getRecordedVersionCode() < 3) {
            SPM.setIsAppIntroDone(false);
        }
        if (!SPM.isAppIntroDone()) {
            Intent i = new Intent(this, IntroActivity.class);
            //clear task cuz we don't need to handle activity result
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            return;
        } else {
            SPM.updateRecordedVersionCode();
        }

        setContentView(R.layout.activity_main);
        initFragment();
        initViews();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                if (mFragmentMine.isAdded()) {
                    mFragmentManager.popBackStack("mine", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                } else {
                    //check to prevent monkeys
                    if (!mFragmentHome.isAdded()) {
                        mFragmentManager.beginTransaction().replace(R.id.fragment_container, mFragmentHome).commit();
                    } else {
                        return false;
                    }
                }
                return true;
            case R.id.navigation_mine:
                if (!mFragmentMine.isAdded()) {
                    mFragmentManager.beginTransaction().addToBackStack("mine").addSharedElement(mFragmentHome.getToolbar(), getString(R.string.tn_toolbar)).replace(R.id.fragment_container, mFragmentMine).commit();
                } else {
                    return false;
                }
                return true;
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //sync navigation state to current fragment
        if (mNavigation.getSelectedItemId() == R.id.navigation_mine) {
            mNavigation.getMenu().getItem(0).setChecked(true);
        }
    }


    private void initViews() {
        mNavigation = findViewById(R.id.navigation);
        mNavigation.setOnNavigationItemSelectedListener(this);
        mNavigation.setSelectedItemId(R.id.navigation_home);
    }

    private void initFragment() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentHome = new HomeFragment();
        mFragmentMine = new MineFragment();
        mFragmentMine.setSharedElementEnterTransition(new ChangeBounds());
        Transition set = TransitionInflater.from(this).inflateTransition(R.transition.mine_enter);
        mFragmentMine.setEnterTransition(set);
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.BOTTOM);
        slide.setInterpolator(AnimationUtils.loadInterpolator(getApplicationContext(), android.R.interpolator.fast_out_slow_in));
        mFragmentHome.setExitTransition(slide);
    }


    public void invokeBluetooth(View view) {
        mFragmentHome.invokeBluetooth(view);
    }

    public void showUserInfo(View view) {
        mFragmentMine.showUserInfo(view);
    }
}
