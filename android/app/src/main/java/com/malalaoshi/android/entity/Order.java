package com.malalaoshi.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kang on 16/5/5.
 */
public class Order implements Parcelable {
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
    List<String[]> timeslots;

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

    public List<String[]> getTimeslots() {
        return timeslots;
    }

    public void setTimeslots(List<String[]> timeslots) {
        this.timeslots = timeslots;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.teacher);
        dest.writeString(this.teacher_name);
        dest.writeString(this.teacher_avatar);
        dest.writeString(this.school);
        dest.writeString(this.grade);
        dest.writeString(this.subject);
        dest.writeValue(this.hours);
        dest.writeString(this.status);
        dest.writeString(this.order_id);
        dest.writeValue(this.to_pay);
        dest.writeString(this.created_at);
        dest.writeString(this.paid_at);
        dest.writeString(this.charge_channel);
        dest.writeByte(this.evaluated ? (byte) 1 : (byte) 0);
        dest.writeByte(this.is_timeslot_allocated ? (byte) 1 : (byte) 0);
        dest.writeList(this.timeslots);
    }

    public Order() {
    }

    protected Order(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.teacher = in.readString();
        this.teacher_name = in.readString();
        this.teacher_avatar = in.readString();
        this.school = in.readString();
        this.grade = in.readString();
        this.subject = in.readString();
        this.hours = (Integer) in.readValue(Integer.class.getClassLoader());
        this.status = in.readString();
        this.order_id = in.readString();
        this.to_pay = (Double) in.readValue(Double.class.getClassLoader());
        this.created_at = in.readString();
        this.paid_at = in.readString();
        this.charge_channel = in.readString();
        this.evaluated = in.readByte() != 0;
        this.is_timeslot_allocated = in.readByte() != 0;
        this.timeslots = new ArrayList<String[]>();
        in.readList(this.timeslots, String[].class.getClassLoader());
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
