package com.malalaoshi.android.entity;

/**
 * Created by kang on 16/6/5.
 */
public class ScheduleCourse extends ScheduleItem {
    private Course course;

    public ScheduleCourse(){
        this.type = TYPE_COURSE;
    }

    public ScheduleCourse(Course course) {
        this.course = course;
        this.type = TYPE_COURSE;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
