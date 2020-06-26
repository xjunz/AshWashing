package com.ashwashing.pro.ui.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple transition that animate the elevation of a View.
 */
public class Elevate extends Transition {
    private static final String PROPNAME_ELEVATION = "ash.template:Elevate:elevation";

    public Elevate(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public String[] getTransitionProperties() {
        return new String[]{PROPNAME_ELEVATION};
    }

    private void captureValues(TransitionValues values) {
        values.values.put(PROPNAME_ELEVATION, values.view.getElevation());
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return super.createAnimator(sceneRoot, startValues, endValues);
        } else {
            return ObjectAnimator.ofFloat(endValues.view, View.Z, (Float) startValues.values.get(PROPNAME_ELEVATION), (Float) endValues.values.get(PROPNAME_ELEVATION));
        }

    }
}
