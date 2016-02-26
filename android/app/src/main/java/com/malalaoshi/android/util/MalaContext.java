package com.malalaoshi.android.util;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Mala context
 * Created by tianwei on 16-2-1.
 */
public class MalaContext {
    private static ExecutorService es;
    private static Handler handler;

    public static void init() {
        es = Executors.newFixedThreadPool(8);
        handler = new Handler(Looper.getMainLooper());
    }

    public static void exec(Runnable runnable) {
        es.execute(runnable);
    }

    public static void postOnMainThread(Runnable runnable) {
        handler.post(runnable);
    }


}