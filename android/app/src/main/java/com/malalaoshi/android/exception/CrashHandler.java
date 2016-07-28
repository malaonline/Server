package com.malalaoshi.android.exception;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by kang on 16/7/27.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    private static CrashHandler instance;
    private Context context;

    public static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }

    public void init(Context ctx) {
        this.context = ctx;
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 核心方法，当程序crash 会回调此方法， Throwable中存放这错误日志
     */
    @Override
    public void uncaughtException(Thread arg0, Throwable arg1) {
        StackTraceElement[] stackTrace = arg1.getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            Toast.makeText(context,"程序出错了",Toast.LENGTH_SHORT).show();
            Log.e("CrashHandler", "file:" + stackTrace[i].getFileName() + " class:" + stackTrace[i].getClassName() + " method:" + stackTrace[i].getMethodName() + " line:" + stackTrace[i].getLineNumber() + "\n");
        }
        arg1.printStackTrace();
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

