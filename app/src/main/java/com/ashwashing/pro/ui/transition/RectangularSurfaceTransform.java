package com.ashwashing.pro.ui.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.RectEvaluator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.util.Property;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import com.ashwashing.pro.R;


/**
 * A transition between two rectangular surfaces using clip bounds and fade foreground animations.
 * One surface should larger in both width & height than the other otherwise the clip bounds can not perform normally.
 * Usually applied between a Button{@link android.widget.Button} and a dialog-styled surface.
 *
 * @author xjunz 2019/2/27
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class RectangularSurfaceTransform extends Transition {

    private static final String PROPNAME_RECT = "ash:RectangularSurfaceTransform:rect";
    private static final String[] PROPERTIES = new String[]{
            PROPNAME_RECT
    };

    private int mStartColor = Color.TRANSPARENT;


    public RectangularSurfaceTransform(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RectTransform);
        mStartColor = ta.getColor(R.styleable.RectTransform_startColor, mStartColor);
        ta.recycle();
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        transitionValues.values.put(PROPNAME_RECT, new Rect(view.getLeft(),view.getTop(), view.getRight(), view.getBottom()));
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
    public String[] getTransitionProperties() {
        return PROPERTIES;
    }


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

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }

        Rect startRect = (Rect) startValues.values.get(PROPNAME_RECT);
        Rect endRect = (Rect) endValues.values.get(PROPNAME_RECT);

        if (endRect == null || startRect == null) {
            return null;
        }

        View target = endValues.view;

        boolean fromLarger = endRect.width() < startRect.width();

        //force resize the smaller view for clip bounds
        if (fromLarger) {
            target.measure(View.MeasureSpec.makeMeasureSpec(startRect.width(), View.MeasureSpec.EXACTLY)
                    , View.MeasureSpec.makeMeasureSpec(startRect.height(), View.MeasureSpec.EXACTLY));
            target.layout(startRect.left, startRect.top, startRect.right, startRect.bottom);
        }


        final Drawable overlay = new ColorDrawable(mStartColor);
        overlay.setBounds(0, 0, target.getWidth(), target.getHeight());
        target.getOverlay().add(overlay);

        ObjectAnimator fadeOverlay = ObjectAnimator.ofInt(overlay, "alpha", fromLarger ? new int[]{0, 255} : new int[]{255, 0});


        int startLeft = (endRect.width() - startRect.width()) / 2;
        int startTop = (endRect.height() - startRect.height()) / 2;
        Rect rectSmall = new Rect(startLeft, startTop, startLeft + startRect.width(), startTop + startRect.height());
        Rect rectLarge = new Rect(0, 0, target.getWidth(), target.getHeight());

        if (fromLarger) {
            startLeft *= -1;
            startTop *= -1;
            rectSmall = new Rect(startLeft, startTop, startLeft + endRect.width(), startTop + endRect.height());
        }

        ObjectAnimator changeClipBounds = ObjectAnimator.ofObject(target, CLIP_BOUNDS, new RectEvaluator(), !fromLarger ? new Rect[]{rectSmall
                , rectLarge} : new Rect[]{rectLarge, rectSmall});


        float transX = startRect.centerX() - endRect.centerX();
        float transY = startRect.centerY() - endRect.centerY();
        ObjectAnimator translate = ObjectAnimator.ofFloat(target, View.TRANSLATION_X, View.TRANSLATION_Y
                , fromLarger ? getPathMotion().getPath(0, 0, -transX, -transY) : getPathMotion().getPath(transX, transY, 0, 0));


        AnimatorSet set = new AnimatorSet();
        set.playTogether(changeClipBounds, translate, fadeOverlay);
        set.setInterpolator(AnimationUtils.loadInterpolator(target.getContext(), android.R.interpolator.fast_out_slow_in));

        return set;

    }
}
