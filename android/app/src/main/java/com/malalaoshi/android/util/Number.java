package com.malalaoshi.android.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * Created by zl on 15/12/14.
 */
public class Number{
    public static DecimalFormat dfDecimal0 = new DecimalFormat("0");

    /**
     * 使用java正则表达式去掉多余的.与0
     * @param d
     * @return
     */
    public static String subZeroAndDot(double d){
        String s = String.format("%.2f",d);
        if(s.indexOf(".") > 0){
            s = s.replaceAll("0+?$", "");//去掉多余的0
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
        }
        return s;
    }
}
