package com.malalaoshi.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kang on 16/2/17.
 */
public class Course implements Parcelable, Comparable<Course> {
    private Integer id;
    private String grade;
    private String subject;
    private boolean is_passed;
    private Long start;
    private Long end;
    private boolean is_commented;
    private String school;
    private Teacher teacher;
    private Comment comment;
    private boolean is_expired;

    public Course() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public boolean is_passed() {
        return is_passed;
    }

    public void setIs_passed(boolean is_passed) {
        this.is_passed = is_passed;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public boolean is_commented() {
        return is_commented;
    }

    public void setIs_commented(boolean is_commented) {
        this.is_commented = is_commented;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public boolean is_expired() {
        return is_expired;
    }

    public void setIs_expired(boolean is_expired) {
        this.is_expired = is_expired;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.subject);
        dest.writeByte(is_passed ? (byte) 1 : (byte) 0);
        dest.writeValue(this.start);
        dest.writeValue(this.end);
        dest.writeByte(is_commented ? (byte) 1 : (byte) 0);
        dest.writeString(this.school);
        dest.writeParcelable(this.teacher, 0);
        dest.writeParcelable(this.comment, flags);
    }

    protected Course(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.subject = in.readString();
        this.is_passed = in.readByte() != 0;
        this.start = (Long) in.readValue(Long.class.getClassLoader());
        this.end = (Long) in.readValue(Long.class.getClassLoader());
        this.is_commented = in.readByte() != 0;
        this.school = in.readString();
        this.teacher = in.readParcelable(Teacher.class.getClassLoader());
        this.comment = in.readParcelable(Comment.class.getClassLoader());
    }

    public static final Parcelable.Creator<Course> CREATOR = new Parcelable.Creator<Course>() {
        public Course createFromParcel(Parcel source) {
            return new Course(source);
        }

        public Course[] newArray(int size) {
            return new Course[size];
        }
    };

    @Override
    public int compareTo(Course another) {
        return this.start.compareTo(another.start);
    }
}
