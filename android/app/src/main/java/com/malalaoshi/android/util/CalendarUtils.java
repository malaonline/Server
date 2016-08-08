/***********************************************************************************
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2014 Robin Chutaux
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ***********************************************************************************/
package com.malalaoshi.android.util;

import com.malalaoshi.android.adapter.SimpleMonthAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CalendarUtils {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd-HH:mm");

    public static int getDaysInMonth(int month, int year) {
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                return 31;
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
                return 30;
            case Calendar.FEBRUARY:
                return ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) ? 29 : 28;
            default:
                throw new IllegalArgumentException("Invalid Month");
        }
    }

    public static SimpleMonthAdapter.CalendarDay timestampToCalendarDay(Long timestamp) {
        //时间戳转化为Sting或Date
        SimpleMonthAdapter.CalendarDay calendarDay = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(timestamp * 1000);
        try {
            Date date = format.parse(d);


            // 初始化 (重置) Calendar 对象
            Calendar calendar = Calendar.getInstance();
            // 或者用 Date 来初始化 Calendar 对象
            calendar.setTime(date);
            calendarDay = new SimpleMonthAdapter.CalendarDay();
            calendarDay.setDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendarDay;
    }


    public static String timestampToTime(Long timestamp) {
        if (timestamp==null) return null;
        Calendar calendar = null;
        //时间戳转化为Sting或Date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(timestamp * 1000);
        return d;
    }

    /**
     * Format
     *
     * @param seconds the time in seconds
     * @return
     */
    public static String[] format(String seconds) {
        long milliseconds = (long) (Double.valueOf(seconds) * 1000);
        return format.format(new Date(milliseconds)).split("-");
    }

    /**
     * 根据timestamp获取Calendar
     * @param timestamp
     * @return
     */
    public static Calendar timestampToCalendar(Long timestamp) {
        if (timestamp==null) return null;
        Calendar calendar = null;
        //时间戳转化为Sting或Date
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String d = format.format(timestamp * 1000);
        try {
            Date date = format.parse(d);
            // 初始化 (重置) Calendar 对象
            calendar = Calendar.getInstance();
            // 或者用 Date 来初始化 Calendar 对象
            calendar.setTime(date);
           /* calendarDay = new SimpleMonthAdapter.CalendarDay();
            calendarDay.setDayOfBegin(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));*/
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static String formatTime(Calendar calendar){
        if (calendar==null) return "";
        String time = String.format("%02d:%02d",calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));
        return time;
    }

    public static String formatDate(Calendar calendar){
        if (calendar==null) return "";
        String date = String.format("%04d年%2d月",calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1);
        return date;
    }

    public static Calendar getCurrentTime(){
        Calendar cal=Calendar.getInstance();
        return cal;
    }

    public static int compareCurrentDate(Calendar calendar){
        if (calendar==null) return -1;
        Calendar cal=Calendar.getInstance();
        if (calendar.get(Calendar.YEAR)>cal.get(Calendar.YEAR)){
            return 1;
        } else if (calendar.get(Calendar.YEAR)==cal.get(Calendar.YEAR)){
            if (calendar.get(Calendar.DAY_OF_YEAR)>cal.get(Calendar.DAY_OF_YEAR)){
                return 1;
            }else if (calendar.get(Calendar.DAY_OF_YEAR)==cal.get(Calendar.DAY_OF_YEAR)){
                return 0;
            }else{
                return -1;
            }

        }else{
            return -1;
        }
    }

    public static int compareCurrentYear(Calendar calendar){
        if (calendar==null) return -1;
        Calendar cal=Calendar.getInstance();
        if (calendar.get(Calendar.YEAR)>cal.get(Calendar.YEAR)){
            return 1;
        } else if (calendar.get(Calendar.YEAR)==cal.get(Calendar.YEAR)){
           return 0;
        }else{
            return -1;
        }
    }

    /**
     * 根据timestamp获取星期
     */
    /**
     * 返回星期码
     * @param timestamp
     * @return
     * Calendar.SUNDAY
     * Calendar.MONDAY
     * Calendar.TUESDAY
     * Calendar.WEDNESDAY
     * Calendar.THURSDAY
     * Calendar.FRIDAY
     * Calendar.SATURDAY
     */
    public static int getWeekId(Long timestamp) {
        if (timestamp==null) return 0;
        int week = 0;
        Calendar cd = timestampToCalendar(timestamp);
        week = cd.get(Calendar.DAY_OF_WEEK);
        // 获取指定日期转换成星期几
        return week;
    }

    private static String formatWeek(int dateOfWeek) {
        String week = "";
        switch (dateOfWeek) {
            case 1:
                week = "日";
                break;
            case 2:
                week = "一";
                break;
            case 3:
                week = "二";
                break;
            case 4:
                week = "三";
                break;
            case 5:
                week = "四";
                break;
            case 6:
                week = "五";
                break;
            case 7:
                week = "六";
                break;
        }
        return "周" + week;
    }

    public static String getWeekBytimestamp(Long timestamp) {
        int week = getWeekId(timestamp);
        return formatWeek(week);
    }


}
