package com.malalaoshi.android.entity;

/**
 * Created by kang on 16/5/5.
 */
public class Order {
    private Long id;
    private String teacher;
    private String teacher_avatar;
    private String school;
    private String grade;
    private String subject;
    private Integer hours;
    private String status;
    private String order_id;
    private Double to_pay;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public String getTeacher_avatar() {
        return teacher_avatar;
    }

    public void setTeacher_avatar(String teacher_avatar) {
        this.teacher_avatar = teacher_avatar;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public Double getTo_pay() {
        return to_pay;
    }

    public void setTo_pay(Double to_pay) {
        this.to_pay = to_pay;
    }
}
