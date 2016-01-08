package com.malalaoshi.android.entity;

/**
 * Created by kang on 16/1/5.
 */
public class School extends BaseEntity implements Comparable<School>{
    private String address;
    private String thumbnail;
    private Double region;
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


    @Override
    public int compareTo(School another) {
        Double d = another.getRegion();
        return this.region.compareTo(d);
    }
}
