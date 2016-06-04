package com.malalaoshi.android.entity;

import com.malalaoshi.android.util.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kang on 15/12/29.
 */
public class Schedule {
    private HashMap<String,List<Course>> scheduleIndex;
    private List<List<Course>> scheduleData;

    public Schedule(){
        scheduleIndex = new HashMap<>();
        scheduleData = new ArrayList<>();
    }

    public void addMoreData(List<Course> courses){
        Collections.sort(courses);
        if (courses!=null&&courses.size()>0){
            for (int i = 0; i < courses.size(); i++) {
                Course course = courses.get(i);
                Calendar calendar = CalendarUtils.timestampToCalendar(course.getStart());
                String key = CalendarUtils.formatDate(calendar);
                //指定月的课程信息
                List<Course> tempCourses = scheduleIndex.get(key );
                if (tempCourses == null) {
                    tempCourses = new ArrayList<>();
                    scheduleData.add(tempCourses);
                    scheduleIndex.put(key, tempCourses);
                }else{
                    Collections.sort(tempCourses);
                }
                tempCourses.add(course);

            }
        }
    }

    public void insert(int position, List<Course> courses){
        Collections.sort(courses);
        if (courses!=null&&courses.size()>0){
            for (int i = 0; i < courses.size(); i++) {
                Course course = courses.get(courses.size()-i-1);
                Calendar calendar = CalendarUtils.timestampToCalendar(course.getStart());
                String key = CalendarUtils.formatDate(calendar);
                //指定月的课程信息
                List<Course> tempCourses = scheduleIndex.get(key );
                if (tempCourses == null) {
                    tempCourses = new ArrayList<>();
                    scheduleData.add(position,tempCourses);
                    scheduleIndex.put(key, tempCourses);
                }else{
                    Collections.sort(tempCourses);
                }
                tempCourses.add(course);
            }
        }
    }

    public void clear(){
        scheduleIndex.clear();
        scheduleData.clear();
    }


    public void addAll(List<Course> listCourse){
        Collections.sort(listCourse);
        if (listCourse!=null&&listCourse.size()>0){
            for (int i = 0; i < listCourse.size(); i++) {
                Course course = listCourse.get(i);
                Calendar calendar = CalendarUtils.timestampToCalendar(course.getStart());
                String key = CalendarUtils.formatDate(calendar);
                //指定月的课程信息
                List<Course> tempCourses = scheduleIndex.get(key );
                if (tempCourses == null) {
                    tempCourses = new ArrayList<>();
                    scheduleData.add(tempCourses);
                    scheduleIndex.put(key, tempCourses);
                }
                tempCourses.add(course);

            }
        }
    }

    public List<Course> get(int position){
        return scheduleData.get(position);
    }

    public int size(){
        return scheduleData.size();
    }

}
