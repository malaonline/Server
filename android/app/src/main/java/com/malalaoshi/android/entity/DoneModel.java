package com.malalaoshi.android.entity;

/**
 * Done model
 * Created by tianwei on 4/17/16.
 */
public class DoneModel extends com.malalaoshi.android.core.base.BaseEntity {
    private boolean done;
    private boolean ok;
    private Long teacher;

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public Long getTeacher() {
        return teacher;
    }

    public void setTeacher(Long teacher) {
        this.teacher = teacher;
    }
}
