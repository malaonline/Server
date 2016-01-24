package com.malalaoshi.android;

import android.app.Instrumentation;
import android.view.View;

/**
 * Utils for test
 * Created by tianwei on 1/17/16.
 */
public class TestUtils {
    public static void performClick(Instrumentation instrumentation, final View view) {
        instrumentation.runOnMainSync(new Runnable() {
            @Override
            public void run() {
                view.performClick();
            }
        });
    }
}
