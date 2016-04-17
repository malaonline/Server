package com.malalaoshi.android.core.usercenter.entity;

import com.malalaoshi.android.core.base.BaseEntity;

/**
 * User profile
 * Created by tianwei on 4/17/16.
 */
public class UserProfile extends BaseEntity {
    private long id;
    private String gender;
    private String avatar;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
