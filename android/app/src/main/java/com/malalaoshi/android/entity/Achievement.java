package com.malalaoshi.android.entity;

import android.os.Parcel;

/**
 * Created by kang on 16/1/30.
 */
public class Achievement extends BaseEntity {
    private String title;
    private String img;
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.title);
        dest.writeString(this.img);
    }

    public Achievement() {
    }

    protected Achievement(Parcel in) {
        super(in);
        this.title = in.readString();
        this.img = in.readString();
    }

    public static final Creator<Achievement> CREATOR = new Creator<Achievement>() {
        public Achievement createFromParcel(Parcel source) {
            return new Achievement(source);
        }

        public Achievement[] newArray(int size) {
            return new Achievement[size];
        }
    };
}
