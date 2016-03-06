package com.malalaoshi.android.entity;

public class CoursePriceUI {
    private String gradePrice;
    private CoursePrice price;
    private boolean check;

    public CoursePriceUI(CoursePrice price) {
        setPrice(price);
    }

    public String getGradePrice() {
        return gradePrice;
    }

    public void setGradePrice(String gradePrice) {
        this.gradePrice = gradePrice;
    }

    public CoursePrice getPrice() {
        return price;
    }

    public void setPrice(CoursePrice price) {
        this.price = price;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
