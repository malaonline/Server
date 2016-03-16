package com.malalaoshi.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by zl on 15/11/26.
 */
public class User implements Parcelable {
    private Long id;
    private String student_name;
    private String student_school_name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudent_name() {
        return student_name;
    }

    public void setStudent_name(String student_name) {
        this.student_name = student_name;
    }

    public String getStudent_school_name() {
        return student_school_name;
    }

    public void setStudent_school_name(String student_school_name) {
        this.student_school_name = student_school_name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.student_name);
        dest.writeString(this.student_school_name);
    }

    public User() {
    }

    protected User(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.student_name = in.readString();
        this.student_school_name = in.readString();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
