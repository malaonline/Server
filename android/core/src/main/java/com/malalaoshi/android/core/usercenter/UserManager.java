package com.malalaoshi.android.core.usercenter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.malalaoshi.android.core.MalaContext;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.usercenter.entity.AuthUser;

import de.greenrobot.event.EventBus;

/**
 * User manager
 * Created by kang on 16/3/9.
 */
public class UserManager {

    /**
     * 登录成功
     */
    public static final String ACTION_LOGINED = "com.malalaoshi.android.account.ACTION_LOGINED";

    /**
     * 登出
     */
    public static final String ACTION_LOGOUT = "com.malalaoshi.android.account.ACTION_LOGOUT";

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
    private Long cityId;

    private UserManager() {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
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
        cityId = userInfo.getLong("cityId", -1);
    }

    public static UserManager getInstance() {
        return instance;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("token", token).apply();
        this.token = token;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("userId", userId).apply();
        this.userId = userId;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("phoneNo", phoneNo).apply();
        this.phoneNo = phoneNo;
    }

    public boolean isLogin() {
        return !TextUtils.isEmpty(token);
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("role", role).apply();
        this.role = role;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("profileId", profileId).apply();
        this.profileId = profileId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("parentId", parentId).apply();
        this.parentId = parentId;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("studname", stuName).apply();
        this.stuName = stuName;
    }

    public String getAvatorUrl() {
        return avatorUrl;
    }

    public void setAvatorUrl(String avatorUrl) {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("avatorUrl", avatorUrl).apply();
        this.avatorUrl = avatorUrl;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("school", school).apply();
        this.school = school;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("gradeId", gradeId).apply();
        this.gradeId = gradeId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
        userInfo.edit().putString("city", city).apply();
        this.city = city;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
        userInfo.edit().putLong("cityId", cityId).apply();
        this.cityId = cityId;
    }

    public void logout() {
        SharedPreferences userInfo = MalaContext.getContext().getSharedPreferences("userInfo", 0);
        token = "";
        userInfo.edit().putString("token", "").apply();
        userId = "";
        userInfo.edit().putString("userId", "").apply();
        phoneNo = "";
        userInfo.edit().putString("phoneNo", "").apply();
        profileId = "";
        userInfo.edit().putString("profileId", "").apply();
        parentId = "";
        userInfo.edit().putString("parentId", "").apply();
        role = "";
        userInfo.edit().putString("role", "").apply();

        stuName = "";
        userInfo.edit().putString("studname", "").apply();
        avatorUrl = "";
        userInfo.edit().putString("avatorUrl", "").apply();
        school = "";
        userInfo.edit().putString("school", "").apply();
        gradeId = "";
        userInfo.edit().putString("gradeId", "").apply();
        //city = "";
        //userInfo.edit().putString("city", "").apply();

        //Login success broadcast
        Intent intent = new Intent(ACTION_LOGOUT);
        MalaContext.getLocalBroadcastManager().sendBroadcast(intent);
        //发送退出通知
        EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_LOGOUT_SUCCESS));
    }

    /**
     * Success login, Update login info
     *
     * @param user Login user
     */
    public void login(AuthUser user) {
        setToken(user.getToken());
        setParentId(user.getParent_id());
        setProfileId(user.getProfile_id());
        setUserId(user.getUser_id());

        //Login success broadcast
        Intent intent = new Intent(ACTION_LOGINED);
        MalaContext.getLocalBroadcastManager().sendBroadcast(intent);
        //发送登录成功通知
        EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_LOGIN_SUCCESS));
    }

    public void startLoginActivity() {
        Intent intent = new Intent(MalaContext.getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MalaContext.getContext().startActivity(intent);
    }
}
