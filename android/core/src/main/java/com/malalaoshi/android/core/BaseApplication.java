package com.malalaoshi.android.core;

import android.app.ActivityManager;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.UserManager;

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
        IntentFilter intentFilter = new IntentFilter(UserManager.ACTION_LOGINED);
        intentFilter.addAction(UserManager.ACTION_LOGOUT);
        MalaContext.getLocalBroadcastManager().registerReceiver(loginReceiver, intentFilter);
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

    @Override
    public void onTerminate() {
        super.onTerminate();
        MalaContext.getLocalBroadcastManager().unregisterReceiver(loginReceiver);
    }

    private final BroadcastReceiver loginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (UserManager.ACTION_LOGINED.equals(intent.getAction())) {
                onUserLogined();
            }else if (UserManager.ACTION_LOGOUT.equals(intent.getAction() )){
                onUserLogout();
            }
        }
    };

    protected abstract void onUserLogined();
    protected abstract void onUserLogout();
}
