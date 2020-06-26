package com.ashwashing.pro.customview;

import android.content.Context;
import android.graphics.Outline;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.Nullable;

public class CircularImageView extends ForegroundImageView{
    public CircularImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(getPaddingLeft(),getPaddingTop() ,getWidth()-getPaddingLeft() ,getHeight()-getPaddingTop() );
            }
        });
        setClipToOutline(true);
    }
}
