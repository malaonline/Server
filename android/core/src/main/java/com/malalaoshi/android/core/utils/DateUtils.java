package com.malalaoshi.android.core.utils;

/**
 * 日期
 * Created by tianwei on 6/5/16.
 */
public class DateUtils {

    /**
     * 格式化到如下格式: 4月上 4月下. 旬怎么翻译?
     */
    public static String formatMonthPart(int month, int day) {
        return monthToChinese(month) + "月" + (day < 15 ? "上" : "下");
    }

    private static String monthToChinese(int month) {
        switch (month) {
            case 1:
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            case 7:
                return "七";
            case 8:
                return "八";
            case 9:
                return "九";
            case 10:
                return "十";
            case 11:
                return "十一";
            case 12:
                return "十二";
            default:
                throw new RuntimeException("非法月份");
        }
    }
}
