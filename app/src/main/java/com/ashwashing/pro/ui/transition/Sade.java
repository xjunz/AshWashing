package com.ashwashing.pro.ui.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class Sade extends Visibility {


    public Sade(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        Animator fade = ObjectAnimator.ofFloat(view, View.ALPHA, 0, 1f);
        Animator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1.2f, 1f);
        Animator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1.2f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(fade, scaleX, scaleY);
        return set;
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        Animator fade = ObjectAnimator.ofFloat(view, View.ALPHA, 1f, 0f);
        Animator scaleX = ObjectAnimator.ofFloat(view, View.SCALE_X, 1, 1.2f);
        Animator scaleY = ObjectAnimator.ofFloat(view, View.SCALE_Y, 1f, 1.2f);
        AnimatorSet set = new AnimatorSet();
        set.playTogether(fade, scaleX, scaleY);
        return set;
    }
}
