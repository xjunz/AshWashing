package com.ashwashing.pro.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.ashwashing.pro.R;


/**
 * 绘制卡券外观的RelativeLayout
 */
public class CouponRelativeLayout extends RelativeLayout {
    private Paint mPaint;
    private float mRadius;
    private float mGap;
    private int mCircleNum;
    private float mMargin;

    public CouponRelativeLayout(@NonNull Context context) {
        this(context, null);
    }

    public CouponRelativeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CouponRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CouponRelativeLayout, defStyleAttr, 0);
        mRadius = a.getDimensionPixelSize(R.styleable.CouponRelativeLayout_circleRadius, 10);
        mGap = a.getDimensionPixelSize(R.styleable.CouponRelativeLayout_circleGap, 8);
        int fillColor = a.getColor(R.styleable.CouponRelativeLayout_circleFillColor, Color.WHITE);
        a.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setDither(true);
        mPaint.setColor(fillColor);
        mPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mMargin == 0) {
            mMargin = (int) (w - mGap) % (2 * mRadius + mGap);
        }
        mCircleNum = (int) ((w - mGap) / (2 * mRadius + mGap));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < mCircleNum; i++) {
            float x = mGap + mRadius + mMargin / 2 + ((mGap + mRadius * 2) * i);
            canvas.drawCircle(x, 0, mRadius, mPaint);
            canvas.drawCircle(x, getHeight(), mRadius, mPaint);
        }
    }

}
