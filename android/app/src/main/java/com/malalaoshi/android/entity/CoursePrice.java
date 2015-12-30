package com.malalaoshi.android.entity;

/**
 * Created by kang on 15/12/24.
 */
public class CoursePrice {
    private Grade grade;
    private float price;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "CoursePrice{" +
                "grade=" + grade +
                ", price=" + price +
                '}';
    }



}
