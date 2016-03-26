package com.malalaoshi.android.core;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;

import com.malalaoshi.android.core.stat.StatReporter;

import java.util.List;

/**
 * Base application
 * Created by tianwei on 3/26/16.
 */
public abstract class BaseApplication extends Application {
    @Override
    public final void onCreate() {
        super.onCreate();
        boolean mainProcess = isMainProcess();

        initBase();
        if (mainProcess) {
            initOnMainProcess();
        } else {
            initOnOtherProcess();
        }
        initAlways();
    }

    private void initBase() {
        MalaContext.init(this);
        StatReporter.init();
        StatReporter.onAppLaunch();
    }

    protected abstract void initOnMainProcess();

    protected abstract void initOnOtherProcess();

    protected abstract void initAlways();

    private boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        if (processInfos != null) {
            String mainProcessName = getPackageName();
            int myPid = android.os.Process.myPid();
            for (ActivityManager.RunningAppProcessInfo info : processInfos) {
                if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
