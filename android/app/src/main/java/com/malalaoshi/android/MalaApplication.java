package com.malalaoshi.android;

import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.malalaoshi.android.core.BaseApplication;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.exception.CrashHandler;
import com.malalaoshi.android.push.MalaPushClient;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by liumengjun on 11/16/15.
 */
public class MalaApplication extends BaseApplication {

    private static String TAG = "MalaApplication";
    private static MalaApplication instance;

    private RequestQueue mRequestQueue;
    private String mMalaHost = BuildConfig.API_HOST;

    // 运行信息
    private boolean isNetworkOk;
    public boolean isFirstStartApp = true;

    private RefWatcher refWatcher;

    @Override
    protected void initOnMainProcess() {

    }

    @Override
    protected void initOnOtherProcess() {

    }

    @Override
    protected void initAlways() {
        instance = this;
        //启动应用后设置用户初始化并设置用户别名
        MalaPushClient.getInstance().init();
        MalaPushClient.getInstance().setAliasAndTags(UserManager.getInstance().getUserId(), null);
        refWatcher = LeakCanary.install(this);
        CrashHandler.getInstance().init(this);//初始化全局异常管理
    }

    public static RefWatcher getRefWatcher(Context context) {
        MalaApplication application = (MalaApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    public static MalaApplication getInstance() {
        return instance;
    }

    public static RequestQueue getHttpRequestQueue() {
        return getInstance().getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public String getMalaHost() {
        return mMalaHost;
    }

    public boolean isNetworkOk() {
        return isNetworkOk;
    }

    public void setIsNetworkOk(boolean isNetworkOk) {
        this.isNetworkOk = isNetworkOk;
    }

    @Override
    protected void onUserLogined() {
        //设置tag和别名(在登录和登出处需要添加设置别名)
        Log.d(TAG,"用户登录后设置JPush别名uid:"+UserManager.getInstance().getUserId());
        MalaPushClient.getInstance().setAliasAndTags(UserManager.getInstance().getUserId(), null);
    }

    @Override
    protected void onUserLogout() {
        //退出登录后,应该清空jpush别名,重置tags
        Log.d(TAG,"用户退出登录后清空JPush别名uid:"+UserManager.getInstance().getUserId());
        MalaPushClient.getInstance().setAliasAndTags(UserManager.getInstance().getUserId(), null);
    }
}
