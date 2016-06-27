package com.malalaoshi.android.entity;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zl on 15/11/26.
 */
public class Teacher extends BaseEntity {
    private String avatar;
    private String gender;
    private String degree;
    private User user;

    private Double min_price;
    private Double max_price;

    private Integer teaching_age;
    private Integer level;
    private String subject;
    private String grades_shortname;
    private String[] grades;
    private String[] tags;

    private String[] photo_set;
    private List<Achievement> achievement_set;
    private List<HighScore> highscore_set;
    private List<CoursePrice> prices;
    public List<Achievement> getAchievement_set() {
        return achievement_set;
    }

    public void setAchievement_set(List<Achievement> achievement_set) {
        this.achievement_set = achievement_set;
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

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
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
                "avatar='" + avatar + '\'' +
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
                ", achievement_set=" + achievement_set +
                ", highscore_set=" + highscore_set +
                ", prices=" + prices +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.avatar);
        dest.writeString(this.gender);
        dest.writeString(this.degree);
        dest.writeParcelable(this.user, flags);
        dest.writeValue(this.min_price);
        dest.writeValue(this.max_price);
        dest.writeValue(this.teaching_age);
        dest.writeValue(this.level);
        dest.writeString(this.subject);
        dest.writeString(this.grades_shortname);
        dest.writeStringArray(this.grades);
        dest.writeStringArray(this.tags);
        dest.writeStringArray(this.photo_set);
        dest.writeTypedList(achievement_set);
        dest.writeList(this.highscore_set);
        dest.writeTypedList(prices);
    }

    public Teacher() {
    }

    protected Teacher(Parcel in) {
        super(in);
        this.avatar = in.readString();
        this.gender = in.readString();
        this.degree = in.readString();
        this.user = in.readParcelable(User.class.getClassLoader());
        this.min_price = (Double) in.readValue(Double.class.getClassLoader());
        this.max_price = (Double) in.readValue(Double.class.getClassLoader());
        this.teaching_age = (Integer) in.readValue(Integer.class.getClassLoader());
        this.level = (Integer) in.readValue(Integer.class.getClassLoader());
        this.subject = in.readString();
        this.grades_shortname = in.readString();
        this.grades = in.createStringArray();
        this.tags = in.createStringArray();
        this.photo_set = in.createStringArray();
        this.achievement_set = in.createTypedArrayList(Achievement.CREATOR);
        this.highscore_set = new ArrayList<HighScore>();
        in.readList(this.highscore_set, List.class.getClassLoader());
        this.prices = in.createTypedArrayList(CoursePrice.CREATOR);
    }

    public static final Creator<Teacher> CREATOR = new Creator<Teacher>() {
        public Teacher createFromParcel(Parcel source) {
            return new Teacher(source);
        }

        public Teacher[] newArray(int size) {
            return new Teacher[size];
        }
    };
}
