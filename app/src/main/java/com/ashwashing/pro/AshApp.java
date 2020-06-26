package com.ashwashing.pro;

import android.app.Application;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

import com.ashwashing.pro.api.bean.OrderInfo;
import com.ashwashing.pro.util.IOUtils;

import org.apaches.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class AshApp extends Application {


    public static final String ACTION_LOG_OUT = "ash.account.action.ACTION_LOG_OUT";
    public static final String ACTION_LOG_IN = "ash.account.action.ACTION_LOG_IN";
    public static final String ACTION_REGISTERED = "ash.account.action.ACTION_REGISTERED";
    public static final String ACTION_SBP_UPDATED = "ash.account.action.ACTION_SBP_UPDATED";
    private static final String DATA_SHARED_PREFS_NAME = "data";
    private static SharedPreferences sSharedPrefs;
    private static SharedPrefsManager sSharedPrefsManager;
    private static SerializedObjManager sSerializedObjManager;
    private static CountDownerManager sCountDownerManager;
    private static OrderManager sOrderManager;
    public static int VERSION_CODE;
    public static String VERSION_NAME;
    public static WeakReference<Context> APP_CONTEXT;
    public static String DATA_PATH;
    public static String LOG_DIR;
    private static ClipboardManager sClipboardManager;

    @Override
    public void onCreate() {
        super.onCreate();
        System.loadLibrary("DexVmpHelper");
        System.loadLibrary("APKProtect");
        sClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        sSharedPrefs = getApplicationContext().getSharedPreferences(DATA_SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        sSharedPrefsManager = new SharedPrefsManager();
        sSerializedObjManager = new SerializedObjManager();
        sCountDownerManager = new CountDownerManager();
        sOrderManager = new OrderManager();
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            VERSION_CODE = packageInfo.versionCode;
            VERSION_NAME = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            if (BuildConfig.DEBUG)
                e.printStackTrace();
        }
        APP_CONTEXT = new WeakReference<>(getApplicationContext());
        DATA_PATH = getApplicationContext().getFilesDir().getPath();
    }

    public static void copyToClipboard(CharSequence content) {
        sClipboardManager.setPrimaryClip(new ClipData("This content is from 'com.ashwashing.pro'", new String[]{"text/plain"}, new ClipData.Item(content)));
    }

    @Contract(pure = true)
    public static SharedPrefsManager getSharedPrefsManager() {
        return sSharedPrefsManager;
    }

    @Contract(pure = true)
    public static SerializedObjManager getSerializedObjManager() {
        return sSerializedObjManager;
    }

    @Contract(pure = true)
    public static OrderManager getOrderManager() {
        return sOrderManager;
    }

    public class SerializedObjManager {
        private SerializedObjManager() {
        }

        @NotNull
        private String generatePathFor(String name) {
            return AshApp.DATA_PATH + File.separator + DigestUtils.md5Hex(name);
        }

        public void write(Object object, String filename) {
            IOUtils.serializeToStorage(object, generatePathFor(filename));
        }

        public <T> T read(String filename, Class<T> clazz) {
            return IOUtils.deserializeFromStorage(generatePathFor(filename), clazz);
        }

        public void delete(String filename) {
            File file = new File(generatePathFor(filename));
            if (file.exists()) {
                if (!file.delete()) {
                    throw new RuntimeException("can't delete " + filename);
                }
            }
        }
    }

    public static class SharedPrefsManager {
        private static final String key_recorded_version_code = "rvc";
        private static final String key_is_first_run = "ifr";
        private static final String key_is_app_intro_done = "iaid";
        private static final String key_last_login_username = "llun";

        private SharedPrefsManager() {
        }

        public String getLastLoginUserName() {
            return sSharedPrefs.getString(key_last_login_username, null);
        }

        public void setLastLoginUserName(String name) {
            sSharedPrefs.edit().putString(key_last_login_username, name).apply();
        }

        public boolean isFirstRun() {
            if (sSharedPrefs.getBoolean(key_is_first_run, true)) {
                sSharedPrefs.edit().putBoolean(key_is_first_run, false).apply();
                return true;
            }
            return false;
        }

        public void setIsAppIntroDone(boolean done) {
            sSharedPrefs.edit().putBoolean(key_is_app_intro_done, done).apply();
        }

        public boolean isAppIntroDone() {
            return sSharedPrefs.getBoolean(key_is_app_intro_done, false);
        }

        private void setRecordedVersionCode(int versionCode) {
            sSharedPrefs.edit().putInt(key_recorded_version_code, versionCode).apply();
        }

        public int getRecordedVersionCode() {
            return sSharedPrefs.getInt(key_recorded_version_code, -1);
        }

        public boolean isAppUpdated() {
            return VERSION_CODE > getRecordedVersionCode();
        }

        public void updateRecordedVersionCode() {
            setRecordedVersionCode(VERSION_CODE);
        }
    }

    public static CountDownerManager getCountDownerManager() {
        return sCountDownerManager;
    }


    public static class CountDownerManager {
        private static Map<String, CountDowner> sCountDownerMap;

        private CountDownerManager() {
            sCountDownerMap = new HashMap<>();
        }

        public CountDowner getCountDowner(String tag) {
            CountDowner countDowner = sCountDownerMap.get(tag);
            if (countDowner == null) {
                countDowner = new CountDowner(tag);
            }
            return countDowner;
        }

        public static class CountDowner {
            private long mLeftoverMills;
            private String mTag;
            private CountDownTimer mTimer;

            private CountDowner(String tag) {
                mTag = tag;
            }

            public long getLeftoverMills() {
                return mLeftoverMills;
            }

            /**
             * A global count-down implementation across the whole application lifecycle.
             * This timer will run util app process is terminated.Useful for a context-unrelated count-down.
             * Whenever this method is called, the last count down will be abandoned.
             *
             * @param totalMills   total count-down mills
             * @param lifecycle    for observation in case of lifecycle issues
             * @param onTickAction action to be run when {@link CountDownTimer#onTick(long)}
             * @param finalAction  action to be run when {@link CountDownTimer#onFinish()}
             */
            public void countDown(long totalMills, @NonNull final Lifecycle lifecycle, @NonNull final Runnable onTickAction, @NonNull final Runnable finalAction) {

                if (sCountDownerMap.containsKey(mTag)) {
                    CountDowner countDowner = sCountDownerMap.get(mTag);
                    if (countDowner != null) {
                        countDowner.mTimer.cancel();
                        sCountDownerMap.remove(mTag);
                    }
                }
                sCountDownerMap.put(mTag, this);
                mLeftoverMills = totalMills;
                mTimer = new CountDownTimer(mLeftoverMills, 1000L) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        mLeftoverMills = millisUntilFinished;
                        if (lifecycle.getCurrentState() != Lifecycle.State.DESTROYED) {
                            onTickAction.run();
                        }
                    }

                    @Override
                    public void onFinish() {
                        mLeftoverMills = 0;
                        if (lifecycle.getCurrentState() != Lifecycle.State.DESTROYED) {
                            finalAction.run();
                        }
                        sCountDownerMap.remove(mTag);
                    }
                };
                mTimer.start();
            }
        }
    }

    public static class OrderManager {
        static final String ORDER_COUNT_DOWN_TAG = "ash.tag.countdown.ORDER";
        public static final long ORDER_COUNT_DOWN_DURATION = 5 * 60 * 1000;
        private static OrderInfo sLatestOrderInfo;

        private OrderManager() {
        }

        public CountDownerManager.CountDowner getCountDowner() {
            return AshApp.getCountDownerManager().getCountDowner(ORDER_COUNT_DOWN_TAG);
        }

        public OrderInfo getLatest() {
            return sLatestOrderInfo;
        }

        public void setLatest(OrderInfo info) {
            sLatestOrderInfo = info;
        }

        public void removeLatest() {
            sLatestOrderInfo = null;
        }

        public boolean hasLatest() {
            if (sLatestOrderInfo == null) {
                return false;
            }
            return getCountDowner().getLeftoverMills() != 0;
        }
    }


}

