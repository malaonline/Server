package com.malalaoshi.android.report.entity;

import java.io.Serializable;

/**
 * 能力结构分析
 * Created by tianwei on 6/4/16.
 */
public class AbilityStructure implements Serializable {
    private String key;
    private float val;

    public AbilityStructure(int val, String key) {
        setVal(val);
        setKey(key);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public float getVal() {
        return val;
    }

    public void setVal(float val) {
        this.val = val;
    }
}
