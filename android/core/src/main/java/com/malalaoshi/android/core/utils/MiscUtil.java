package com.malalaoshi.android.core.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.widget.Toast;

import com.malalaoshi.android.core.MalaContext;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mist util
 * Created by tianwei on 1/9/16.
 */
public class MiscUtil {

    private static Handler handler = new Handler(Looper.getMainLooper());

    public static boolean isMobilePhone(String phone) {
        Pattern p = Pattern.compile("^((17[0-9])|(13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    public static void toast(int rid) {
        Toast.makeText(MalaContext.getContext(), rid, Toast.LENGTH_SHORT).show();
    }

    public static void toast(String msg) {
        Toast.makeText(MalaContext.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void runOnMainThread(Runnable task) {
        handler.post(task);
    }

    public static String formatDate(long value) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");
        return format.format(value);
    }

    public static <T> boolean isNotEmpty(Collection<T> list) {
        return list != null && list.size() > 0;
    }

    public static <T> boolean isEmpty(Collection<T> list) {
        return list == null || list.size() == 0;
    }

    public static double getScreenWidth() {
        return getDisplayMetrics().widthPixels;
    }

    public static double getScreenHeight() {
        return getDisplayMetrics().heightPixels;
    }

    public static DisplayMetrics getDisplayMetrics() {
        return MalaContext.getContext().getResources().getDisplayMetrics();
    }

    public static int dp2px(float dpValue) {
        final float scale = MalaContext.getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(float pxValue) {
        final float scale = MalaContext.getContext().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
