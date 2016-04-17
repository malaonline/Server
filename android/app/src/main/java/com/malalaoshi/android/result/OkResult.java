package com.malalaoshi.android.result;

import com.malalaoshi.android.core.base.BaseEntity;

/**
 * Ok resuslt
 * Created by tianwei on 4/17/16.
 */
public class OkResult extends BaseEntity {

    private boolean ok;

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }
}
