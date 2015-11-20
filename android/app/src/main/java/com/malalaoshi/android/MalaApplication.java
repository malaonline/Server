package com.malalaoshi.android;

import android.app.Application;
import android.content.SharedPreferences;

/**
 * Created by liumengjun on 11/16/15.
 */
public class MalaApplication extends Application {

    private static MalaApplication instance;

    private String userId;
    private String phoneNo;
    private boolean isLogin;
    private String role;

    @Override
    public void onCreate() {
        instance = this;
        this.init();
        super.onCreate();
    }

    public static MalaApplication getInstance() {
        return instance;
    }

    public void init() {
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        userId = userInfo.getString("userId", "");
        phoneNo = userInfo.getString("phoneNo", "");
        isLogin = userInfo.getBoolean("isLogin", false);
        role = userInfo.getString("role", "");
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        SharedPreferences userInfo = getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("role", role).commit();
        this.role = role;
    }
}
