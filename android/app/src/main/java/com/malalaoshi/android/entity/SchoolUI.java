package com.malalaoshi.android.entity;

/**
 * school add check field for ui display.
 * Created by kang on 16/1/5.
 */
public class SchoolUI {
    private School school;
    private boolean check;

    public SchoolUI(School school) {
        setSchool(school);
        setCheck(false);
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
