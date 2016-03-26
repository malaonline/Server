package com.malalaoshi.android.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Mala context
 * Created by tianwei on 16-2-1.
 */
public class MalaContext {
    private static ExecutorService es;
    private static Handler handler;
    private static Context context;
    private static LocalBroadcastManager localBroadcastManager;

    public static void init(Context context) {
        es = Executors.newFixedThreadPool(8);
        handler = new Handler(Looper.getMainLooper());
        MalaContext.context = context;
        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public static void exec(Runnable runnable) {
        es.execute(runnable);
    }

    public static void postOnMainThread(Runnable runnable) {
        handler.post(runnable);
    }

    public static Context getContext() {
        return context;
    }

    public static LocalBroadcastManager getLocalBroadcastManager() {
        return localBroadcastManager;
    }
}