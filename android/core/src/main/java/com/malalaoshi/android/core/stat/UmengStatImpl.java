package com.malalaoshi.android.core.stat;

import android.content.Context;
import android.util.Log;

import com.malalaoshi.android.core.BuildConfig;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * Umeng stat manager
 * Umeng的文档请查看: git/doc/umeng.md
 * Created by tianwei on 1/2/16.
 */
public class UmengStatImpl implements StatProxy {
    private static final String TAG = "UmengStatManager";
    private WeakReference<Context> contextRef;

    public UmengStatImpl() {
        contextRef = new WeakReference<>(null);
    }

    @Override
    public void init() {
        //TODO get the channel id.
        AnalyticsConfig.setChannel("umeng_android_c_test");
        if (BuildConfig.DEBUG) {
            MobclickAgent.setDebugMode(true);
        }
    }

    @Override
    public void onStart(Context context) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onStart, context=" + context);
        }
    }

    @Override
    public void onStop(Context context) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onStop, context=" + context);
        }
    }

    @Override
    public void logEvent(String name, Map<String, String> params) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "logEvent, Event name: " + name);
        }
        Context context = contextRef.get();
        if (context != null && params instanceof HashMap<?, ?>) {
            MobclickAgent.onEvent(context, name, params);
        }
    }

    @Override
    public void logEvent(String name) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "logEvent, Event name: " + name);
        }
        Context context = contextRef.get();
        if (context != null) {
            MobclickAgent.onEvent(context, name);
        }
    }

    @Override
    public void onResume(Context context) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onResume, context=" + context);
        }
        contextRef = new WeakReference<>(context);
        MobclickAgent.onResume(context.getApplicationContext());
    }

    @Override
    public void onPause(Context context) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPause, context=" + context);
        }
        MobclickAgent.onPause(context.getApplicationContext());
    }
}
