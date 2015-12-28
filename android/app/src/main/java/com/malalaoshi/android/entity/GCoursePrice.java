package com.malalaoshi.android.entity;

/**
 * Created by kang on 15/12/24.
 */
public class GCoursePrice {
    private Long grade;
    private int price;
    private String name;
    private float rebate;

    public float getRebate() {
        return rebate;
    }

    public void setRebate(float rebate) {
        this.rebate = rebate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Long getGrade() {
        return grade;
    }

    public void setGrade(Long grade) {
        this.grade = grade;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "GCoursePrice{" +
                "grade=" + grade +
                ", price=" + price +
                ", name='" + name + '\'' +
                ", rebate=" + rebate +
                '}';
    }

}
