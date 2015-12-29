package com.malalaoshi.android.usercenter;

import android.content.Context;
import android.content.SharedPreferences;

import com.malalaoshi.android.MalaApplication;

/**
 * Created by tianwei on 12/26/15.
 */
public class UserProfile {
    private static final String USER_VERIFY_TOKEN = "user_verify_token";
    private static UserProfile instance = new UserProfile();
    private static final String PF_NAME = "user_profile";
    private SharedPreferences preferences;

    private String userVerifyToken;

    private UserProfile() {
        preferences = MalaApplication.getInstance().getSharedPreferences(PF_NAME, Context.MODE_PRIVATE);
    }

    public static UserProfile getInstance(){
        return instance;
    }

    public String getUserVerifyToken() {
        return userVerifyToken == null ? preferences.getString(USER_VERIFY_TOKEN, "") : userVerifyToken;
    }

    private SharedPreferences.Editor getEditor() {
        return MalaApplication.getInstance().getSharedPreferences(PF_NAME, Context.MODE_PRIVATE).edit();
    }

    public void setUserVerifyToken(String userVerifyToken) {
        this.userVerifyToken = userVerifyToken;
        getEditor().putString(USER_VERIFY_TOKEN, userVerifyToken).apply();
    }

}
