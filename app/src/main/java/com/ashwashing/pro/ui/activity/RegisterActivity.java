package com.ashwashing.pro.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.R;
import com.ashwashing.pro.ui.fragment.register.BaseRegisterFragment;
import com.ashwashing.pro.ui.fragment.register.HYVerifyFragment;
import com.ashwashing.pro.ui.fragment.register.PlatformSelectionFragment;
import com.ashwashing.pro.ui.fragment.register.QZVerifyFragment;
import com.ashwashing.pro.ui.fragment.register.RegisterFragment;
import com.ashwashing.pro.util.UiUtils;

public class RegisterActivity extends BaseActivity implements BaseRegisterFragment.Callback {

    private ViewPager2 mViewPager;
    private PlatformSelectionFragment mSelectionPage;
    private HYVerifyFragment mHyPage;
    private QZVerifyFragment mQzPage;
    private BaseRegisterFragment[] mPages;
    private RegisterStepAdapter mAdapter;
    private TextView mIndicator;
    private ImageButton mIbNext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initPages();
        mViewPager = findViewById(R.id.vp_register);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setUserInputEnabled(false);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
            }
        });
        mAdapter = new RegisterStepAdapter(getSupportFragmentManager(), getLifecycle());
        mViewPager.setAdapter(mAdapter);
        mIndicator = findViewById(R.id.tv_indicator);
        mIndicator.setText(getString(R.string.indicator_format, 1, BaseRegisterFragment.STEP_COUNT));
        mIbNext = findViewById(R.id.ib_next_step);
        mViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                mIndicator.setText(getString(R.string.indicator_format, position + 1, BaseRegisterFragment.STEP_COUNT));
                if (position == BaseRegisterFragment.STEP_COUNT - 1) {
                    mIbNext.setImageResource(R.drawable.ic_check_24dp);
                } else {
                    mIbNext.setImageResource(R.drawable.ic_keyboard_arrow_right_24dp);
                }
            }
        });

    }

    private void initPages() {
        mHyPage = new HYVerifyFragment();
        mQzPage = new QZVerifyFragment();
        mSelectionPage = new PlatformSelectionFragment();
        RegisterFragment registerPage = new RegisterFragment();
        mPages = new BaseRegisterFragment[]{mSelectionPage, mQzPage, registerPage};
        for (BaseRegisterFragment fragment : mPages) {
            fragment.setCallback(this);
        }
        //don't leave it behind
        mHyPage.setCallback(this);
    }

    @Override
    public void onDone(BaseRegisterFragment fragment) {
        if (fragment.getStepIndex() == BaseRegisterFragment.STEP_COUNT - 1) {
            sendBroadcast(new Intent(AshApp.ACTION_REGISTERED));
            finishAfterTransition();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
        }
    }

    @Override
    public void onUndone(BaseRegisterFragment fragment) {
        fragment.notifyUndone();
    }


    private class RegisterStepAdapter extends FragmentStateAdapter {

        RegisterStepAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
            super(fragmentManager, lifecycle);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return mPages[position];
        }


        @Override
        public int getItemCount() {
            return mPages.length;
        }
    }


    public void showRegisterRationale(View view) {
        UiUtils.createRationale(this, R.string.register_rationale).show();
    }


    public void gotoPrevious(View view) {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
    }

    public void gotoNext(View view) {
        int curIndex = mViewPager.getCurrentItem();
        final BaseRegisterFragment step = mPages[curIndex];
        if (curIndex == 0) {
            if (!mSelectionPage.isQzSelected() && mPages[1].equals(mQzPage)) {
                mPages[1] = mHyPage;
                mViewPager.setAdapter(mAdapter);
            } else if (mSelectionPage.isQzSelected() && !mPages[1].equals(mQzPage)) {
                mPages[1] = mQzPage;
                mViewPager.setAdapter(mAdapter);
            }
            mViewPager.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    onDone(step);
                    mViewPager.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }
        step.go();
    }
}
