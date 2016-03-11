package com.malalaoshi.android.util;

import android.content.SharedPreferences;
import android.text.TextUtils;

import com.malalaoshi.android.MalaApplication;

/**
 * Created by kang on 16/3/9.
 */
public class UserManager {
    private static UserManager instance = new UserManager();
    // 用户信息
    private String token;
    private String userId;
    private String phoneNo;
    private String role;
    private String profileId;
    private String parentId;

    private String stuName;
    private String avatorUrl;
    private String school;
    private String gradeId;
    private String city;

    private UserManager() {
        SharedPreferences userInfo = MalaApplication.getInstance().getSharedPreferences("userInfo", 0);
        token = userInfo.getString("token", "");
        userId = userInfo.getString("userId", "");
        phoneNo = userInfo.getString("phoneNo", "");
        profileId = userInfo.getString("profileId", "");
        parentId = userInfo.getString("parentId", "");
        role = userInfo.getString("role", "");

        stuName = userInfo.getString("studname", "");
        avatorUrl = userInfo.getString("avatorUrl", "");
        school = userInfo.getString("school", "");
        gradeId = userInfo.getString("gradeId", "");
        city = userInfo.getString("city", "");
    }

    public static UserManager getInstance() {
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        SharedPreferences userInfo = MalaApplication.getInstance().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("token", token).commit();
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        SharedPreferences userInfo = MalaApplication.getInstance().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("userId", userId).commit();
        this.userId = userId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        SharedPreferences userInfo = MalaApplication.getInstance().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("phoneNo", phoneNo).commit();
        this.phoneNo = phoneNo;
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(token);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        SharedPreferences userInfo = MalaApplication.getInstance().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("role", role).commit();
        this.role = role;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        SharedPreferences userInfo = MalaApplication.getInstance().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("profileId", profileId).commit();
        this.profileId = profileId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        SharedPreferences userInfo = MalaApplication.getInstance().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("parentId", parentId).commit();
        this.parentId = parentId;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        SharedPreferences userInfo = MalaApplication.getInstance().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("studname", stuName).commit();
        this.stuName = stuName;
    }

    public String getAvatorUrl() {
        return avatorUrl;
    }

    public void setAvatorUrl(String avatorUrl) {
        SharedPreferences userInfo = MalaApplication.getInstance().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("avatorUrl", avatorUrl).commit();
        this.avatorUrl = avatorUrl;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        SharedPreferences userInfo = MalaApplication.getInstance().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("school", school).commit();
        this.school = school;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        SharedPreferences userInfo = MalaApplication.getInstance().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("gradeId", gradeId).commit();
        this.gradeId = gradeId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        SharedPreferences userInfo = MalaApplication.getInstance().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("city", city).commit();
        this.city = city;
    }


    public void logout(){
        SharedPreferences userInfo = MalaApplication.getInstance().getSharedPreferences("userInfo", 0);
        token = "";
        userInfo.edit().putString("token", "").commit();
        userId = "";
        userInfo.edit().putString("userId", "").commit();
        phoneNo = "";
        userInfo.edit().putString("phoneNo", "").commit();
        profileId = "";
        userInfo.edit().putString("profileId", "").commit();
        parentId = "";
        userInfo.edit().putString("parentId", "").commit();
        role = "";
        userInfo.edit().putString("role", "").commit();

        stuName = "";
        userInfo.edit().putString("studname", "").commit();
        avatorUrl = "";
        userInfo.edit().putString("avatorUrl", "").commit();
        school = "";
        userInfo.edit().putString("school", "").commit();
        gradeId = "";
        userInfo.edit().putString("gradeId", "").commit();
        city = "";
        userInfo.edit().putString("city", "").commit();
    }
}
