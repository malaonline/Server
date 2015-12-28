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
    private Double minPrice;
    private Double maxPrice;
    private Long teaching_age;
    private GLevel level;
    private Long subject;
    private Long[] grades;
    private Long[] tags;
    private String[] gallery;
    private String[] certificate;
    private List<GHighScore> highscore_set;
    private List<GCoursePrice> prices;
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

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Long getTeaching_age() {
        return teaching_age;
    }

    public void setTeaching_age(Long teaching_age) {
        this.teaching_age = teaching_age;
    }

    public GLevel getLevel() {
        return level;
    }

    public void setLevel(GLevel level) {
        this.level = level;
    }

    public Long getSubject() {
        return subject;
    }

    public void setSubject(Long subject) {
        this.subject = subject;
    }

    public Long[] getGrades() {
        return grades;
    }

    public void setGrades(Long[] grades) {
        this.grades = grades;
    }

    public Long[] getTags() {
        return tags;
    }

    public void setTags(Long[] tags) {
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

    public List<GHighScore> getHighscore_set() {
        return highscore_set;
    }

    public void setHighscore_set(List<GHighScore> highscore_set) {
        this.highscore_set = highscore_set;
    }

    public List<GCoursePrice> getPrices() {
        return prices;
    }

    public void setPrices(List<GCoursePrice> prices) {
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
                ", minPrice=" + minPrice +
                ", maxPrice=" + maxPrice +
                ", teaching_age=" + teaching_age +
                ", level=" + level +
                ", subject=" + subject +
                ", grades=" + Arrays.toString(grades) +
                ", tags=" + Arrays.toString(tags) +
                ", gallery=" + Arrays.toString(gallery) +
                ", certificate=" + Arrays.toString(certificate) +
                ", highscore_set=" + highscore_set +
                ", prices=" + prices +
                '}';
    }


}
