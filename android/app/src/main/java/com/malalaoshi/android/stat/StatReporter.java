package com.malalaoshi.android.stat;

/**
 * Stat reporter
 * Created by tianwei on 1/2/16.
 */
public class StatReporter {

    public static void init() {
        StatManager.getInstance().init();
    }

    public enum EventName {
        //Following is just test event.
        APP_LAUNCH
    }

    public static void onAppLaunch() {
        StatManager.getInstance().logEvent(EventName.APP_LAUNCH.name());
    }
}
