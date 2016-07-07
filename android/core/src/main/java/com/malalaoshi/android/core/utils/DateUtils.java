package com.malalaoshi.android.core.utils;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * 日期
 * Created by tianwei on 6/5/16.
 */
public class DateUtils {

    private static final String NO_HYPHEN_DATE = "yyyyMMdd";

    private static final String HOUR_MIN = "HH:mm";

    private static final String DATE_FULL = "yyyy年MM月dd";

    /**
     * 格式化到如下格式: 4月上 4月下. 旬怎么翻译?
     */
    public static String formatMonthPart(int month, int day) {
        return monthToChinese(month) + "月" + (day < 15 ? "上" : "下");
    }

    /**
     * 格式化的格式如下: 20160101
     */
    public static String formatNoHyphenDate(long ms) {
        SimpleDateFormat format = new SimpleDateFormat(NO_HYPHEN_DATE, Locale.getDefault());
        try {
            return format.format(ms);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 格式化的格式如下: 10:15
     */
    public static String formatHourMin(long ms) {
        SimpleDateFormat format = new SimpleDateFormat(HOUR_MIN, Locale.getDefault());
        try {
            return format.format(ms);
        } catch (Exception e) {
            return null;
        }
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

    /**
     * 格式化后如下:2016年1月1. 没有日
     */
    public static String formatFull(long ms) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FULL, Locale.getDefault());
        try {
            return format.format(ms);
        } catch (Exception e) {
            return "";
        }
    }
}
