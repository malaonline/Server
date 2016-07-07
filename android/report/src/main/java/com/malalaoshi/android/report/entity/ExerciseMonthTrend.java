package com.malalaoshi.android.report.entity;

import android.support.annotation.NonNull;

import com.malalaoshi.android.core.utils.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 练习按月分布
 * Created by tianwei on 6/4/16.
 */
public class ExerciseMonthTrend implements Serializable, Comparable<ExerciseMonthTrend> {
    private int total_item;
    private int error_item;
    private int year;
    private int month;
    private int day;
    private String monthPart;
    private long time;

    public ExerciseMonthTrend(int year, int month, int day, int total, int error) {
        setYear(year);
        setMonth(month);
        setDay(day);
        setTotal_item(total);
        setError_item(error);
        setMonthPart(DateUtils.formatMonthPart(month, day));
        setTime(new Date(year - 1900, month - 1, day).getTime());
    }

    public int getTotal_item() {
        return total_item;
    }

    public void setTotal_item(int total_item) {
        this.total_item = total_item;
    }

    public int getError_item() {
        return error_item;
    }

    public void setError_item(int error_item) {
        this.error_item = error_item;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getMonthPart() {
        return monthPart;
    }

    public void setMonthPart(String monthPart) {
        this.monthPart = monthPart;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int compareTo(@NonNull ExerciseMonthTrend another) {
        if (this.getTime() > another.getTime()) {
            return 1;
        }
        return -1;
    }
}
