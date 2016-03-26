package com.malalaoshi.android.core.usercenter.entity;

import com.malalaoshi.android.core.base.BaseEntity;

/**
 * User policy
 * Created by tianwei on 3/27/16.
 */
public class UserPolicy extends BaseEntity {
    private String content;
    private String updated_at;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
