package com.malalaoshi.android.entity;

import android.os.Parcel;

/**
 * Created by kang on 16/1/5.
 */
public class School extends BaseEntity implements Comparable<School>{
    private String address;
    private String thumbnail;
    private Double distance;
    private Double region;   //城市
    private boolean center;
    private Double longitude;
    private Double latitude;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Double getRegion() {
        return region;
    }

    public void setRegion(Double region) {
        this.region = region;
    }

    public boolean isCenter() {
        return center;
    }

    public void setCenter(boolean center) {
        this.center = center;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    @Override
    public int compareTo(School another) {
        Double d = another.getDistance();
        return this.distance.compareTo(d);
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.address);
        dest.writeString(this.thumbnail);
        dest.writeValue(this.distance);
        dest.writeValue(this.region);
        dest.writeByte(center ? (byte) 1 : (byte) 0);
        dest.writeValue(this.longitude);
        dest.writeValue(this.latitude);
    }

    public School() {
    }

    protected School(Parcel in) {
        super(in);
        this.address = in.readString();
        this.thumbnail = in.readString();
        this.distance = (Double) in.readValue(Double.class.getClassLoader());
        this.region = (Double) in.readValue(Double.class.getClassLoader());
        this.center = in.readByte() != 0;
        this.longitude = (Double) in.readValue(Double.class.getClassLoader());
        this.latitude = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Creator<School> CREATOR = new Creator<School>() {
        public School createFromParcel(Parcel source) {
            return new School(source);
        }

        public School[] newArray(int size) {
            return new School[size];
        }
    };
}
