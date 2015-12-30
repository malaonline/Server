package com.malalaoshi.android.entity;

import java.util.Arrays;
import java.util.List;

/**
 * 用于gson解析
 * Created by kang on 15/12/24.
 */
public class GTeacher {
    private Long id;
    private String avatar;
    private String gender;
    private String name;
    private String degree;
    private Double min_price;
    private Double max_price;
    private Long teaching_age;
    private String level;
    private String subject;
    private String grades_shortname;
    private String[] grades;
    private String[] tags;
    private String[] gallery;
    private String[] certificate;
    private List<HighScore> highscore_set;
    private List<CoursePrice> prices;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public Double getMin_price() {
        return min_price;
    }

    public void setMin_price(Double min_price) {
        this.min_price = min_price;
    }

    public Double getMax_price() {
        return max_price;
    }

    public void setMax_price(Double max_price) {
        this.max_price = max_price;
    }

    public Long getTeaching_age() {
        return teaching_age;
    }

    public void setTeaching_age(Long teaching_age) {
        this.teaching_age = teaching_age;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getGrades_shortname() {
        return grades_shortname;
    }

    public void setGrades_shortname(String grades_shortname) {
        this.grades_shortname = grades_shortname;
    }

    public String[] getGrades() {
        return grades;
    }

    public void setGrades(String[] grades) {
        this.grades = grades;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String[] getGallery() {
        return gallery;
    }

    public void setGallery(String[] gallery) {
        this.gallery = gallery;
    }

    public String[] getCertificate() {
        return certificate;
    }

    public void setCertificate(String[] certificate) {
        this.certificate = certificate;
    }

    public List<HighScore> getHighscore_set() {
        return highscore_set;
    }

    public void setHighscore_set(List<HighScore> highscore_set) {
        this.highscore_set = highscore_set;
    }

    public List<CoursePrice> getPrices() {
        return prices;
    }

    public void setPrices(List<CoursePrice> prices) {
        this.prices = prices;
    }

    @Override
    public String toString() {
        return "GTeacher{" +
                "id=" + id +
                ", avatar='" + avatar + '\'' +
                ", gender='" + gender + '\'' +
                ", name='" + name + '\'' +
                ", degree='" + degree + '\'' +
                ", min_price=" + min_price +
                ", max_price=" + max_price +
                ", teaching_age=" + teaching_age +
                ", level='" + level + '\'' +
                ", subject='" + subject + '\'' +
                ", grades_shortname='" + grades_shortname + '\'' +
                ", grades=" + Arrays.toString(grades) +
                ", tags=" + Arrays.toString(tags) +
                ", gallery=" + Arrays.toString(gallery) +
                ", certificate=" + Arrays.toString(certificate) +
                ", highscore_set=" + highscore_set +
                ", prices=" + prices +
                '}';
    }

}
