package com.malalaoshi.android.core.stat;

import android.content.Context;
import android.util.Log;

import com.malalaoshi.android.core.BuildConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Stat Manager
 * Created by tianwei on 1/2/16.
 */
public class StatManager implements StatProxy {
    private static final StatManager instance = new StatManager();
    private static final String TAG = "StatManager";

    private List<StatProxy> proxies;

    private StatManager() {
        proxies = new ArrayList<>();
        //Add umeng stat.
        //proxies.add(new UmengStatImpl());
        //proxies.add(new GoogleStatImpl());
    }

    public static StatManager getInstance() {
        return instance;
    }

    @Override
    public void init() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Stat Manager init");
        }
        for (StatProxy proxy : proxies) {
            proxy.init();
        }
    }

    @Override
    public void onStart(Context context) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onStart, context=" + context);
        }
        for (StatProxy proxy : proxies) {
            proxy.onStart(context);
        }
    }

    @Override
    public void onStop(Context context) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onStop, context=" + context);
        }
        for (StatProxy proxy : proxies) {
            proxy.onStop(context);
        }
    }

    @Override
    public void logEvent(String name, Map<String, String> params) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "logEvent event name :" + name);
        }
        for (StatProxy proxy : proxies) {
            proxy.logEvent(name, params);
        }
    }

    @Override
    public void logEvent(String name) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "logEvent, event name: " + name);
        }
        for (StatProxy proxy : proxies) {
            proxy.logEvent(name);
        }
    }

    @Override
    public void onResume(Context context) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onResume, context=" + context);
        }
        for (StatProxy proxy : proxies) {
            proxy.onResume(context);
        }
    }

    @Override
    public void onPause(Context context) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPause, context=" + context);
        }
        for (StatProxy proxy : proxies) {
            proxy.onPause(context);
        }
    }
}
