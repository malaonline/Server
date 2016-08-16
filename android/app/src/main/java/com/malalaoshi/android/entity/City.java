package com.malalaoshi.android.entity;

import android.os.Parcel;

/**
 * Created by kang on 16/8/16.
 */
public class City extends BaseEntity {
    private String pinyin;

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.pinyin);
    }

    public City() {
    }

    protected City(Parcel in) {
        super(in);
        this.pinyin = in.readString();
    }

    public static final Creator<City> CREATOR = new Creator<City>() {
        @Override
        public City createFromParcel(Parcel source) {
            return new City(source);
        }

        @Override
        public City[] newArray(int size) {
            return new City[size];
        }
    };

    @Override
    public String toString() {
        return "City{" +
                "pinyin='" + pinyin + '\'' +
                '}';
    }
}
