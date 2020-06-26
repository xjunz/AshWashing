package com.ashwashing.pro.ui.transition;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.RectEvaluator;
import android.content.Context;
import android.graphics.Rect;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;


/**
 * A visibility transition expands from top when appearing (like a curtain) and collapse when disappearing
 * It's feasible to implement animation from other directions.
 *
 * @author xjunz 2019/3/31
 */

public class ClipSlideDown extends Visibility {

    private static final Property<View, Rect> CLIP_BOUNDS = new Property<View, Rect>(Rect.class, "clipBounds") {
        @Override
        public Rect get(View object) {
            return object.getClipBounds();
        }

        @Override
        public void set(View object, Rect value) {
            object.setClipBounds(value);
        }
    };

    public ClipSlideDown(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Animator onAppear(ViewGroup sceneRoot, final View view, TransitionValues startValues, TransitionValues endValues) {
        Rect endRect = new Rect(0, 0, view.getWidth(), view.getHeight());
        Rect startRect = new Rect(0, 0, endRect.right, 0);
        return ObjectAnimator.ofObject(view, CLIP_BOUNDS, new RectEvaluator(), startRect, endRect);
    }

    @Override
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        Rect startRect = new Rect(0, 0, view.getWidth(), view.getHeight());
        Rect endRect = new Rect(0, view.getHeight() / 2, startRect.right, view.getHeight() / 2);
        return ObjectAnimator.ofObject(view, CLIP_BOUNDS, new RectEvaluator(), startRect, endRect);
    }

}
