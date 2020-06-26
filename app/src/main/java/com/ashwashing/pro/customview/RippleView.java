package com.ashwashing.pro.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import com.ashwashing.pro.R;


/**
 * 绘制动态水波纹的视图
 */
public class RippleView extends View {
    private boolean mRunning = false;
    private int[] mStrokeWidthArr;
    private int mMaxStrokeWidth;
    private int mRippleCount;
    private int mRippleSpacing;
    private Paint mPaintRipple;
    private int mTargetId;
    private int mCenterWidth;

    public RippleView(Context context) {
        this(context, null);
    }

    public RippleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimary});
        int defRippleColor = ta.getColor(0, 0);
        ta.recycle();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RippleView);
        int rippleColor = typedArray.getColor(R.styleable.RippleView_rippleColor,
                defRippleColor);
        mRippleCount = typedArray.getInt(R.styleable.RippleView_rippleCount, 3);
        mRippleSpacing = typedArray.getDimensionPixelSize(R.styleable.RippleView_rippleSpacing,
                100);
        mRunning = typedArray.getBoolean(R.styleable.RippleView_rippleAutoRunning, true);
        mTargetId = typedArray.getResourceId(R.styleable.RippleView_rippleTarget, NO_ID);
        typedArray.recycle();
        mPaintRipple = new Paint();
        mPaintRipple.setAntiAlias(true);
        mPaintRipple.setStyle(Paint.Style.STROKE);
        mPaintRipple.setColor(rippleColor);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View mTarget = getRootView().findViewById(mTargetId);
        if (mTarget != null) {
            mCenterWidth = mTarget.getMeasuredWidth();
        }

        int size = (mRippleCount * mRippleSpacing) * 2 + mCenterWidth;
        int mWidth = resolveSize(size, widthMeasureSpec);
        int mHeight = resolveSize(size, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
        mMaxStrokeWidth = (mWidth - mCenterWidth) / 2;
        if (mStrokeWidthArr == null) {
            initArray();
        }
    }


    private void initArray() {
        mStrokeWidthArr = new int[mRippleCount];
        for (int i = 0; i < mStrokeWidthArr.length; i++) {
            mStrokeWidthArr[i] = -mMaxStrokeWidth / mRippleCount * i;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mRunning) {
            drawRipple(canvas);
            postInvalidateDelayed(10);
        } else {
            drawWave(canvas);
        }

    }

    private void drawWave(Canvas canvas) {
        for (int i = 1; i <= mRippleCount; i++) {
            mPaintRipple.setStrokeWidth(mRippleCount - i + 1);
            mPaintRipple.setAlpha(255 >> i);
            canvas.drawCircle(getWidth() >> 1, getHeight() >> 1, mCenterWidth / 2f + mRippleSpacing * i, mPaintRipple);
        }
    }

    private void drawRipple(Canvas canvas) {
        for (int strokeWidth : mStrokeWidthArr) {
            if (strokeWidth < 0) {
                continue;
            }
            mPaintRipple.setStrokeWidth(strokeWidth);
            mPaintRipple.setAlpha(160 - 160 * strokeWidth / mMaxStrokeWidth);
            canvas.drawCircle(getWidth() >> 1, getHeight() >> 1, (mCenterWidth >> 1) + (strokeWidth >> 1),
                    mPaintRipple);
        }

        for (int i = 0; i < mStrokeWidthArr.length; i++) {
            if ((mStrokeWidthArr[i] += 1) > mMaxStrokeWidth) {
                mStrokeWidthArr[i] = 0;
            }
        }
    }

    public void start() {
        if (!mRunning) {
            mRunning = true;
            postInvalidate();
        }
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void stop() {
        if (mRunning) {
            mRunning = false;
            initArray();
            postInvalidate();
        }
    }

    private class ViewState extends BaseSavedState {
        boolean running;
        int[] strokeWidthArr;
        int maxStrokeWidth;

        ViewState(Parcelable superState, boolean running, int[] strokeWidthArr, int maxStrokeWidth) {
            super(superState);
            this.running = running;
            this.strokeWidthArr = strokeWidthArr;
            this.maxStrokeWidth = maxStrokeWidth;
        }

        public ViewState(Parcel source) {
            super(source);
            running = source.readByte() == 1;
            source.readIntArray(strokeWidthArr);
            maxStrokeWidth = source.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            //就保存这些状态，因为其他的参数都是只能从xml里读取的
            out.writeByte((byte) (running ? 1 : 0));
            out.writeIntArray(strokeWidthArr);
            out.writeInt(maxStrokeWidth);
        }

    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        return new ViewState(super.onSaveInstanceState(), mRunning, mStrokeWidthArr, mMaxStrokeWidth);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        ViewState viewState = (ViewState) state;
        mRunning = viewState.running;
        mStrokeWidthArr = viewState.strokeWidthArr;
        mMaxStrokeWidth = viewState.maxStrokeWidth;
        // postInvalidate();
    }
}
 
