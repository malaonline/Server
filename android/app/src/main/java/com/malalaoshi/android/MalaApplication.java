package com.malalaoshi.android;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.malalaoshi.android.stat.StatReporter;
import com.malalaoshi.android.util.MalaContext;

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
    private String mMalaHost = "http://172.16.0.207:8000";//BuildConfig.API_HOST;

    // 用户信息
    private String token;
    private String userId;
    private String phoneNo;
    private boolean isLogin;
    private String role;
    // 运行信息
    private boolean isNetworkOk;
    private String parentId;

    @Override
    public void onCreate() {
        super.onCreate();
        this.init();
        instance = this;
        MalaContext.init();
        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        //设置tag和别名(在登录和登出处需要添加设置别名)
        JPushInterface.setAliasAndTags(this,userId,null,new TagAliasCallback() {
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

    public void init() {
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        token = userInfo.getString("token", "");
        userId = userInfo.getString("userId", "");
        phoneNo = userInfo.getString("phoneNo", "");
        isLogin = userInfo.getBoolean("isLogin", false);
        role = userInfo.getString("role", "");
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("token", token).commit();
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("userId", userId).commit();
        this.userId = userId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("phoneNo", phoneNo).commit();
        this.phoneNo = phoneNo;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        userInfo.edit().putBoolean("isLogin", isLogin).commit();
        this.isLogin = isLogin;
    }

    public boolean logout() {
        this.isLogin = false;
        this.token = "";
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        userInfo.edit().putBoolean("isLogin", false).putString("token", "").commit();
        return true;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("role", role).commit();
        this.role = role;
    }

    public boolean isNetworkOk() {
        return isNetworkOk;
    }

    public void setIsNetworkOk(boolean isNetworkOk) {
        this.isNetworkOk = isNetworkOk;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getParentId() {
        return this.parentId;
    }
}
