package com.malalaoshi.android.report.entity;

import java.io.Serializable;

/**
 * 作业颜色列表
 * Created by tianwei on 5/21/16.
 */
public class WorkColorModel implements Serializable {
    private int color;
    private String content;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
