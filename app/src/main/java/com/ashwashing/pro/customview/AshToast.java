package com.ashwashing.pro.customview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import com.ashwashing.pro.R;

public class AshToast extends Toast {
    private TextView mTvMsg;
    private ImageView mIvIcon;

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public AshToast(Context context) {
        super(context);
        @SuppressLint("InflateParams") View content = LayoutInflater.from(context).inflate(R.layout.widget_toast, null);
        mTvMsg = content.findViewById(R.id.tv_toast_msg);
        mIvIcon = content.findViewById(R.id.iv_toast_icon);
        setView(content);
    }

    public AshToast setIcon(@DrawableRes int iconRes) {
        mIvIcon.setImageResource(iconRes);
        return this;
    }

    public AshToast setBackgroundColor(@ColorInt int color) {
        getView().setBackgroundTintList(ColorStateList.valueOf(color));
        return this;
    }

    public static AshToast makeText(Context context, @StringRes int resId, int duration)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    public static AshToast makeText(Context context, CharSequence text, int duration) {
        AshToast toast = new AshToast(context);
        toast.mTvMsg.setText(text);
        toast.setDuration(duration);
        return toast;
    }



}
