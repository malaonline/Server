package com.malalaoshi.android.entity;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zl on 15/11/26.
 */
public class Teacher extends BaseEntity {
    private String avatar;
    private Character gender;
    private Character degree;
    private User user;

    private Double min_price;
    private Double max_price;

    private Integer teaching_age;
    private String level;
    private String subject;
    private String grades_shortname;
    private String[] grades;
    private String[] tags;

    private String[] photo_set;
    private String[] certificate_set;
    private List<HighScore> highscore_set;
    private List<CoursePrice> prices;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public Character getDegree() {
        return degree;
    }

    public void setDegree(Character degree) {
        this.degree = degree;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getTeaching_age() {
        return teaching_age;
    }

    public void setTeaching_age(Integer teaching_age) {
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

    public String[] getPhoto_set() {
        return photo_set;
    }

    public void setPhoto_set(String[] photo_set) {
        this.photo_set = photo_set;
    }

    public String[] getCertificate_set() {
        return certificate_set;
    }

    public void setCertificate_set(String[] certificate_set) {
        this.certificate_set = certificate_set;
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

    public Double getMin_price() {
        return min_price;
    }

    public void setMin_price(Double minPrice) {
        if (Double.isNaN(minPrice)) {
            this.min_price = null;
        } else {
            this.min_price = minPrice;
        }
    }

    public Double getMax_price() {
        return max_price;
    }

    public void setMax_price(Double maxPrice) {
        if (Double.isNaN(maxPrice)) {
            this.max_price = null;
        } else {
            this.max_price = maxPrice;
        }
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", gender=" + gender +
                ", degree=" + degree +
                ", user=" + user +
                ", min_price=" + min_price +
                ", max_price=" + max_price +
                ", teaching_age=" + teaching_age +
                ", level='" + level + '\'' +
                ", subject='" + subject + '\'' +
                ", grades_shortname='" + grades_shortname + '\'' +
                ", grades=" + Arrays.toString(grades) +
                ", tags=" + Arrays.toString(tags) +
                ", photo_set=" + Arrays.toString(photo_set) +
                ", certificate_set=" + Arrays.toString(certificate_set) +
                ", highscore_set=" + highscore_set +
                ", prices=" + prices +
                '}';
    }
}
