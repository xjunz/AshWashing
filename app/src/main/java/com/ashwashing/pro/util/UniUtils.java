package com.ashwashing.pro.util;

import android.os.Build;

import androidx.annotation.NonNull;

import com.ashwashing.pro.AshApp;
import com.ashwashing.pro.R;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class UniUtils {


    public static boolean illegalPhoneNumber(@NonNull String num) {
        return !num.matches("1([358][0-9]|4[579]|66|7[0135678]|9[89])[0-9]{8}");
    }

    public static String getCurrentDateAndTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS", Locale.CHINA).format(new Date());
    }

    public static String formatDuration(long duration) {
        int min = (int) duration / 60000;
        int sec = (int) (Math.ceil((duration - min * 60000f) / 1000f));
        return AshApp.APP_CONTEXT.get().getString(R.string.min_sec, String.valueOf(min), sec < 10 ? "0" + sec : sec);
    }

    public static String formatDate(long timestamp) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).format(new Date());
    }


    public static int random(int min, int max) {
        Random random = new Random(System.currentTimeMillis());
        return random.nextInt(max - min + 1) + min;
    }

    public static String getEssentialHardwareInfo() {
        return "release: " + Build.VERSION.RELEASE + "\n" +
                "SDK: " + Build.VERSION.SDK_INT + "\n" +
                "brand: " + Build.BRAND + "\n" +
                "model: " + Build.MODEL + "\n" +
                "CPU_ABI: " + Arrays.toString(Build.SUPPORTED_ABIS);
    }

    public static String getVersionInfo() {
        return "version_name: " + AshApp.VERSION_NAME + "\n" + "version_code: " + AshApp.VERSION_CODE;
    }

    public static String blurPhoneNum(String phoneNum) {
        if (phoneNum == null || phoneNum.length() < 11) {
            return phoneNum;
        }
        String f3 = phoneNum.substring(0, 3);
        String e4 = phoneNum.substring(7);
        return f3 + "****" + e4;
    }



}



