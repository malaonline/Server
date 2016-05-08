package com.malalaoshi.android.course;

import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.entity.CourseDateEntity;

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

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

    /**
     * 三天内不能预约
     *
     * @param hours 总的小时数
     * @param times 周时间表
     * @return 上课时间
     */
    public static List<String> calculateCourse(int hours, List<CourseDateEntity> times) {
        Collections.sort(times);
        List<String> list = new ArrayList<>();
        if (hours < 2 || EmptyUtils.isEmpty(times)) {
            return list;
        }
        Calendar calendar = Calendar.getInstance();
        //预约最早是三天后的时间
        calendar.add(Calendar.DATE, 3);
        Date beginDate = calendar.getTime();
        List<String> keys = new ArrayList<>();
        for (int i = 0; i < hours / 2; i++) {
            CourseDateEntity entity = times.get(i % times.size());
            calendar.setTime(beginDate);
            int day = entity.getDay() - calendar.get(Calendar.DAY_OF_WEEK);
            day = day < 0 ? day + 7 : day;
            calendar.add(Calendar.DATE, day);
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
            String time = formatDate(calendar.getTime()) + " " + entity.getStart() + "-" + entity.getEnd();
            list.add(time);
        }
        return list;
    }

    /**
     * 格式化成：2016/3/3
     */
    private static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }
}
