package com.malalaoshi.android.course.model;

import java.io.Serializable;

/**
 * Course time
 * Created by tianwei on 5/15/16.
 */
public class CourseTimeModel implements Serializable {
    //格式化的日期: 一月一日
    private String date;
    //周几： 周一
    private String week;
    //每天的上课时间表
    private String courseTimes;

    //计时开始过去的天数。用来作为区别天的key
    private long dayOfBegin;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getCourseTimes() {
        courseTimes = courseTimes == null ? "" : courseTimes;
        return courseTimes;
    }

    public void setCourseTimes(String courseTimes) {
        this.courseTimes = courseTimes;
    }

    public long getDayOfBegin() {
        return dayOfBegin;
    }

    public void setDayOfBegin(long dayOfBegin) {
        this.dayOfBegin = dayOfBegin;
    }
}
