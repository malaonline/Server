package com.malalaoshi.android.core.usercenter.entity;

import com.malalaoshi.android.core.base.BaseEntity;

/**
 * Add student by parent id
 * Created by tianwei on 3/27/16.
 */
public class AddStudentName extends BaseEntity {
    private boolean done;

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
