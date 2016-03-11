package com.malalaoshi.android.entity;

import java.util.Date;

/**
 * Created by zl on 15/11/26.
 */
public class User {
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
}
