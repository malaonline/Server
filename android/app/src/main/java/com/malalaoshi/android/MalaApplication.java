package com.malalaoshi.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.malalaoshi.android.stat.StatReporter;
import com.malalaoshi.android.util.MalaContext;
import com.malalaoshi.android.util.UserManager;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by liumengjun on 11/16/15.
 */
public class MalaApplication extends Application {

    private static String TAG = "MalaApplication";
    private static MalaApplication instance;

    private RequestQueue mRequestQueue;
    private String mMalaHost = BuildConfig.API_HOST;

    // 运行信息
    private boolean isNetworkOk;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MalaContext.init();
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        //设置tag和别名(在登录和登出处需要添加设置别名)
        JPushInterface.setAliasAndTags(this, UserManager.getInstance().getUserId(),null,new TagAliasCallback() {
            @Override
            public void gotResult ( int i, String s, Set < String > set){
                Log.d(TAG, "status code:" + i + " alias:" + s );
            }
        });

        StatReporter.init();
        StatReporter.onAppLaunch();


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
}
