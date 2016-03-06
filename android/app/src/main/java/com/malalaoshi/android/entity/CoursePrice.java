package com.malalaoshi.android.entity;

import android.os.Parcel;

/**
 * Created by kang on 15/12/24.
 */
public class CoursePrice extends BaseEntity {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.grade, 0);
        dest.writeFloat(this.price);
    }

    public CoursePrice() {
    }

    protected CoursePrice(Parcel in) {
        super(in);
        this.grade = in.readParcelable(Grade.class.getClassLoader());
        this.price = in.readFloat();
    }

    public static final Creator<CoursePrice> CREATOR = new Creator<CoursePrice>() {
        public CoursePrice createFromParcel(Parcel source) {
            return new CoursePrice(source);
        }

        public CoursePrice[] newArray(int size) {
            return new CoursePrice[size];
        }
    };
}
