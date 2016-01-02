package com.malalaoshi.android.stat;

import android.content.Context;

import java.util.Map;

/**
 * 统计的接口类。
 * Created by tianwei on 1/2/16.
 */
public interface StatProxy {
    void onStart(Context context);

    void onStop(Context context);

    void logEvent(String name, Map<String, String> params);

    void logEvent(String name);

    void onResume(Context context);

    void onPause(Context context);

    void init();
}
