package com.malalaoshi.android.report.entity;

import java.io.Serializable;

/**
 * 数据轴数据
 * Created by tianwei on 5/22/16.
 */
public class AxisModel implements Serializable {

    // Y轴 一值
    private int yValue;
    //Y轴 二值
    private int y2Value;
    //X轴
    private String xValue;

    public AxisModel(int y, int y2, String txt) {
        setyValue(y);
        setY2Value(y2);
        setxValue(txt);
    }

    public AxisModel(int y, String txt) {
        setyValue(y);
        setxValue(txt);
    }

    public int getyValue() {
        return yValue;
    }

    public void setyValue(int yValue) {
        this.yValue = yValue;
    }

    public int getY2Value() {
        return y2Value;
    }

    public void setY2Value(int y2Value) {
        this.y2Value = y2Value;
    }

    public String getxValue() {
        return xValue == null ? "" : xValue;
    }

    public void setxValue(String xValue) {
        this.xValue = xValue;
    }
}
