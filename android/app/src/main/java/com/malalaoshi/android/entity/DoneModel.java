package com.malalaoshi.android.entity;

/**
 * Done model
 * Created by tianwei on 4/17/16.
 */
public class DoneModel extends com.malalaoshi.android.core.base.BaseEntity {
    private boolean done;

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }
}
