package com.malalaoshi.android.report.entity;

import java.io.Serializable;

/**
 * 饼状图数据体
 * Created by tianwei on 5/21/16.
 */
public class PieModel implements Serializable {

    private int color;
    private int num;
    private float beginAngle;
    private float swapAngle;

    public PieModel(int color, int startAngle, int swapAngle) {
        setColor(color);
        setBeginAngle(startAngle);
        setSwapAngle(swapAngle);
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public float getBeginAngle() {
        return beginAngle;
    }

    public void setBeginAngle(float beginAngle) {
        this.beginAngle = beginAngle;
    }

    public float getSwapAngle() {
        return swapAngle;
    }

    public void setSwapAngle(float swapAngle) {
        this.swapAngle = swapAngle;
    }
}
