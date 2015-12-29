package com.malalaoshi.android.entity;

/**
 * Created by kang on 15/12/24.
 */
public class GHighScore {
    String name;
    int increased_scores;
    String school_name;
    String admitted_to;

    public String getAdmitted_to() {
        return admitted_to;
    }

    public void setAdmitted_to(String admitted_to) {
        this.admitted_to = admitted_to;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIncreased_scores() {
        return increased_scores;
    }

    public void setIncreased_scores(int increased_scores) {
        this.increased_scores = increased_scores;
    }

    public String getSchool_name() {
        return school_name;
    }

    public void setSchool_name(String school_name) {
        this.school_name = school_name;
    }

    @Override
    public String toString() {
        return "HighScore{" +
                "name='" + name + '\'' +
                ", increased_scores=" + increased_scores +
                ", school_name='" + school_name + '\'' +
                ", admitted_to='" + admitted_to + '\'' +
                '}';
    }
}
