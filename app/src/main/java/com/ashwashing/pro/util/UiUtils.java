package com.ashwashing.pro.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.R;
import com.ashwashing.pro.customview.AshToast;

/**
 * Utilities for UI related affairs
 */
public class UiUtils {
    public static AlertDialog.Builder createRationale(Context context, int rationaleRes) {
        return new AlertDialog.Builder(context).setMessage(rationaleRes)
                .setTitle(R.string.rationale)
                .setPositiveButton(android.R.string.ok, null);
    }

    public static AlertDialog.Builder createAlert(Context context, int alertRes) {
        return new AlertDialog.Builder(context).setMessage(alertRes)
                .setTitle(R.string.alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, null);
    }

    public static AlertDialog.Builder createAlert(Context context, String alert) {
        return new AlertDialog.Builder(context).setMessage(alert)
                .setTitle(R.string.alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.ok, null);
    }

    public static void showShortToast(Object msg) {
        if (msg == null) {
            msg = "null";
        }
        Toast.makeText(AshApp.APP_CONTEXT.get(), msg.toString(), Toast.LENGTH_SHORT).show();
    }


    public static void showShortAshToast(Activity context, int msgRes) {
        AshToast.makeText(context, msgRes, Toast.LENGTH_SHORT).show();
    }

    public static void showShortAshToast(Activity context, String msg) {
        AshToast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public static void showErrorToast(Activity context, String msg) {
        if (msg == null) {
            msg = "null";
        }
        AshToast.makeText(context, msg, Toast.LENGTH_SHORT).setBackgroundColor(context.getResources().getColor(R.color.colorAccent)).show();
    }

    public static void showErrorToast(Activity context, @StringRes int msgRes) {
        AshToast.makeText(context, msgRes, Toast.LENGTH_SHORT).setBackgroundColor(context.getResources().getColor(R.color.colorAccent)).show();
    }


    public static void makeVisible(@NonNull View... views) {
        for (View view : views) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void makeInvisible(@NonNull View... views) {
        for (View view : views) {
            view.setVisibility(View.INVISIBLE);
        }
    }

    public static void makeGone(@NonNull View... views) {
        for (View view : views) {
            view.setVisibility(View.GONE);
        }
    }

    public static void animateGone(@NonNull final View view) {
        view.animate().alpha(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                makeGone(view);
            }
        }).start();
    }

    public static void animateVisible(@NonNull final View view) {
        view.setAlpha(0);
        view.animate().alpha(1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                makeVisible(view);
            }
        }).start();
    }

    public static void animateInvisible(@NonNull final View view) {
        view.animate().alpha(0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                makeVisible(view);
            }
        }).start();
    }


    public static void animateSwitchImageRes(final ImageView imageView, final int res, final Runnable finalAction) {
        imageView.animate().alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        imageView.setImageResource(res);
                        if (finalAction != null) {
                            finalAction.run();
                        }
                        imageView.animate().alpha(1f).setListener(null).start();
                    }
                }).start();
    }

    public static void animateSwitchTextRes(final TextView textView, final int res, final Runnable finalAction) {
        textView.animate().alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        textView.setText(res);
                        if (finalAction != null) {
                            finalAction.run();
                        }
                        textView.animate().alpha(1f).setListener(null).start();
                    }
                }).start();
    }

    public static void animateSwitchText(final TextView textView, final String text, final Runnable finalAction) {
        textView.animate().alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        textView.setText(text);
                        if (finalAction != null) {
                            finalAction.run();
                        }
                        textView.animate().alpha(1f).setListener(null).start();
                    }
                }).start();
    }

   /* public static Dialog createProgressDialog(Context context, int titleRes) {
        Dialog dialog = new AlertDialog.Builder(context).create();
        dialog.setContentView(R.layout.dialog_progress);
        TextView tvTitle = dialog.findViewById(R.id.tv_title);
        Objects.requireNonNull(tvTitle).setText(titleRes);
        dialog.setCancelable(false);
        return dialog;
    }
*/

    public static Dialog createProgressDialog(Context context, int titleRes) {
        @SuppressLint("InflateParams") View content = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
        TextView tvTitle = content.findViewById(R.id.tv_title);
        tvTitle.setText(titleRes);
        return new AlertDialog.Builder(context).setView(content).create();
    }


    public static void setBackgroundTint(View view, @ColorRes int colorRes) {
        view.setBackgroundTintList(ColorStateList.valueOf(view.getContext().getResources().getColor(colorRes)));
    }


}
