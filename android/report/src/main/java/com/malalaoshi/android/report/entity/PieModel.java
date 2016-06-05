package com.malalaoshi.android.report.entity;

import com.malalaoshi.android.core.utils.EmptyUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 饼状图数据体
 * Created by tianwei on 5/21/16.
 */
public class PieModel implements Serializable {

    //颜色
    private int color;
    //百分比
    private int num;
    //360角度
    private float beginAngle;
    //360角度
    private float swapAngle;

    public PieModel(Integer color, int num) {
        setColor(color);
        setNum(num);
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
        if (num < 0) {
            this.num = 0;
        }
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

    /**
     * 数量求百分比
     */
    public static void calNumByNumber(List<PieModel> list) {
        if (EmptyUtils.isEmpty(list)) {
            return;
        }
        int totalNum = 0;
        for (PieModel model : list) {
            totalNum += model.getNum();
        }
        float endAngle = 0;
        float swapAngle;
        for (PieModel model : list) {
            swapAngle = model.getNum() * 360f / totalNum;
            if (endAngle + swapAngle > 360) {
                model.setSwapAngle(360 - endAngle);
            } else {
                model.setSwapAngle(swapAngle);
            }
            model.setBeginAngle(endAngle - 90); //逆时针旋转90度
            endAngle = endAngle + model.getSwapAngle();
        }
    }
}
