package com.malalaoshi.android.core.usercenter.entity;

import com.malalaoshi.android.core.base.BaseEntity;

/**
 * Evaluated model
 * Created by tianwei on 4/17/16.
 */
public class Evaluated extends BaseEntity {

    private boolean evaluated;

    public boolean isEvaluated() {
        return evaluated;
    }

    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }
}
