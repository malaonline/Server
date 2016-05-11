package com.malalaoshi.android.entity;

/**
 * Created by kang on 16/5/5.
 */
public class Order {
    private Long id;
    private String teacher;
    private String teacher_name;
    private String teacher_avatar;
    private String school;
    private String grade;
    private String subject;
    private Integer hours;
    private String status;
    private String order_id;
    private Double to_pay;
    private String created_at;
    private String paid_at;
    private String charge_channel;
    private boolean evaluated;
    private boolean is_timeslot_allocated;

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

    public String getTeacher_name() {
        return teacher_name;
    }

    public void setTeacher_name(String teacher_name) {
        this.teacher_name = teacher_name;
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getPaid_at() {
        return paid_at;
    }

    public void setPaid_at(String paid_at) {
        this.paid_at = paid_at;
    }

    public String getCharge_channel() {
        return charge_channel;
    }

    public void setCharge_channel(String charge_channel) {
        this.charge_channel = charge_channel;
    }

    public boolean isEvaluated() {
        return evaluated;
    }

    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    public boolean is_timeslot_allocated() {
        return is_timeslot_allocated;
    }

    public void setIs_timeslot_allocated(boolean is_timeslot_allocated) {
        this.is_timeslot_allocated = is_timeslot_allocated;
    }
}
