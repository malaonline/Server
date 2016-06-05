package com.malalaoshi.android.core.utils;

/**
 * 年级
 * Created by tianwei on 6/5/16.
 */
public class GradeUtils {

    public static String getGradeName(int gradeId) {
        switch (gradeId) {
            case 1:
                return "小学一年级";
            case 2:
                return "小学二年级";
            case 3:
                return "小学三年级";
            case 4:
                return "小学四年级";
            case 5:
                return "小学五年级";
            case 6:
                return "小学六年级";
            case 7:
                return "初中一年级";
            case 8:
                return "初中二年级";
            case 9:
                return "初中三年级";
            case 10:
                return "高中一年级";
            case 11:
                return "高中二年级";
            case 12:
                return "高中三年级";
        }
        return null;
    }
}
