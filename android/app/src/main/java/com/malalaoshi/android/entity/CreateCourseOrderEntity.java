package com.malalaoshi.android.entity;

import java.io.Serializable;
import java.util.List;

/**
 * Create course order entity
 * Created by tianwei on 2/27/16.
 */
public class CreateCourseOrderEntity  extends JsonBodyBase implements Serializable {
    private long teacher;
    private long school;
    private long grade;
    private long subject;
    private Long coupon;
    private long hours;
    private List<Integer> weekly_time_slots;

    public long getTeacher() {
        return teacher;
    }

    public void setTeacher(long teacher) {
        this.teacher = teacher;
    }

    public long getSchool() {
        return school;
    }

    public void setSchool(long school) {
        this.school = school;
    }

    public long getGrade() {
        return grade;
    }

    public void setGrade(long grade) {
        this.grade = grade;
    }

    public long getSubject() {
        return subject;
    }

    public void setSubject(long subject) {
        this.subject = subject;
    }

    public Long getCoupon() {
        return coupon;
    }

    public void setCoupon(long coupon) {
        this.coupon = coupon;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(long hours) {
        this.hours = hours;
    }

    public List<Integer> getWeekly_time_slots() {
        return weekly_time_slots;
    }

    public void setWeekly_time_slots(List<Integer> weekly_time_slots) {
        this.weekly_time_slots = weekly_time_slots;
    }

}
