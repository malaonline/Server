package com.malalaoshi.android.course;

import android.util.Log;

import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.course.model.CourseTimeModel;
import com.malalaoshi.android.entity.CourseDateEntity;
import com.malalaoshi.android.util.CalendarUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Course helper
 * Created by tianwei on 5/8/16.
 */
public class CourseHelper {

    private static final long MS_OF_ONE_DAY = 24 * 3600 * 1000;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM月dd日", Locale.getDefault());

    /**
     * 包括今天的48小时内不能预约
     * CourseDateEntity星期排列是周一到周日1～7.Calendar是周日到周六1~7.要做一个转化
     *
     * @param hours 总的小时数
     * @param times 周时间表
     * @return 上课时间
     */
    public static List<CourseTimeModel> calculateCourse(int hours, List<CourseDateEntity> times) {
        Collections.sort(times);
        List<CourseTimeModel> list = new ArrayList<>();
        if (hours < 2 || EmptyUtils.isEmpty(times)) {
            return list;
        }
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        //预约最早是第三天的时间(也就是今天，明天不可预约)
        int weekDateOfNow = calendar.get(Calendar.DAY_OF_WEEK);
        //calendar.add(Calendar.DATE, 2);
        //可一预约的开始日期
        //long beginDay = getDayFromBegin(calendar.getTimeInMillis());
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < hours / 2; i++) {
            //上课时间排期,按小时数循环
            calendar.setTime(now);
            CourseDateEntity entity = times.get(i % times.size());
            int realWeek = (entity.getDay() + 1) % 7;
            calendar.add(Calendar.DATE, realWeek - weekDateOfNow);
            //按星期几找到日期
            while (isCourseExpire(calendar, entity)) {
                calendar.add(Calendar.DAY_OF_WEEK, 7);
            }
            String key;
            //如果这周的同一时间已经选了，那就下一周。
            while (true) {
                key = " " + calendar.get(Calendar.YEAR) + calendar.get(Calendar.WEEK_OF_YEAR) + entity.getId();
                if (keys.contains(key)) {
                    calendar.add(Calendar.DATE, 7);
                } else {
                    keys.add(key);
                    break;
                }
            }
            long dayKey = calendar.getTimeInMillis() / MS_OF_ONE_DAY;
            CourseTimeModel model = null;
            for (CourseTimeModel item : list) {
                if (item.getDayOfBegin() == dayKey) {
                    model = item;
                }
            }
            if (model == null) {
                model = new CourseTimeModel();
                model.setDayOfBegin(dayKey);
                model.setDate(formatDate(calendar.getTime()));
                model.setWeek(formatWeek(calendar.get(Calendar.DAY_OF_WEEK)));
                list.add(model);
            }
            model.setCourseTimes(model.getCourseTimes() + entity.getStart() + "-" + entity.getEnd() + " ");
        }
        Collections.sort(list);
        return list;
    }

    /**
     * 课程是否过期
     */
    private static boolean isCourseExpire(Calendar calendar, CourseDateEntity entity) {
        Date now = new Date();
        try {
            int hours = Integer.valueOf(entity.getStart().split(":")[0]);
            int minutes = Integer.valueOf(entity.getStart().split(":")[1]);
            calendar.set(Calendar.HOUR_OF_DAY, hours);
            calendar.set(Calendar.MINUTE, minutes);
            return now.getTime() > calendar.getTimeInMillis();
        } catch (Exception e) {
            Log.i("MALA", "isCourseExpire: " + e.getMessage());
        }
        return false;
    }

    /**
     * 得出从计时开始流去的天数
     *
     * @param time ms
     * @return 天数
     */
    private static long getDayFromBegin(long time) {
        return time / MS_OF_ONE_DAY;
    }

    /**
     * 格式化成：2016/3/3
     */

    private static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
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

    public static List<CourseTimeModel> courseTimes(List<String[]> timeslots) {
        //Collections.sort(times);
        List<CourseTimeModel> list = new ArrayList<>();
        if (EmptyUtils.isEmpty(timeslots)) {
            return list;
        }
        CourseTimeModel model = null;
        Calendar lastCalendar = null;
        for (int i = 0; i < timeslots.size(); i++) {
            String[] data = timeslots.get(i);
            if (data.length == 2) {
                String start = data[0];
                String end = data[1];
                Calendar startCalendar = CalendarUtils.timestampToCalendar(Long.valueOf(start));
                Calendar endCalendar = CalendarUtils.timestampToCalendar(Long.valueOf(end));

                if (lastCalendar == null) {
                    model = null;
                } else {
                    if (startCalendar.get(Calendar.DAY_OF_YEAR) == lastCalendar.get(Calendar.DAY_OF_YEAR)
                            && startCalendar.get(Calendar.DAY_OF_MONTH) == lastCalendar.get(Calendar.DAY_OF_MONTH)) {

                    } else {
                        model = null;
                    }
                }
                lastCalendar = startCalendar;

                if (model == null) {
                    model = new CourseTimeModel();
                    model.setDate(formatDate(lastCalendar.getTime()));
                    model.setWeek(formatWeek(lastCalendar.get(Calendar.DAY_OF_WEEK)));
                    list.add(model);
                }//DecimalFormat df = new DecimalFormat("0.00");
                //df.format();
                String courseTimes = String.format("%02d:%02d-%02d:%02d", startCalendar.get(Calendar.HOUR_OF_DAY),
                        startCalendar.get(Calendar.MINUTE), endCalendar.get(Calendar.HOUR_OF_DAY),
                        endCalendar.get(Calendar.MINUTE));
                model.setCourseTimes(model.getCourseTimes() + courseTimes + " ");
            }
        }
        return list;
    }


}
