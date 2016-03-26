package com.malalaoshi.android.core.usercenter.entity;

import com.malalaoshi.android.core.base.BaseEntity;

/**
 * Send sms response
 * Created by tianwei on 3/27/16.
 */
public class AuthUser extends BaseEntity {
    private boolean verified;
    private boolean first_login;
    private String token;
    private String parent_id;
    private String user_id;
    private String profile_id;

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isFirst_login() {
        return first_login;
    }

    public void setFirst_login(boolean first_login) {
        this.first_login = first_login;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getProfile_id() {
        return profile_id;
    }

    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }
}
