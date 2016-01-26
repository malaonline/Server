package com.malalaoshi.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liumengjun on 12/31/15.
 */
public class BaseEntity implements Parcelable {
    protected Long id;
    protected String name;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    protected boolean isChecked;

    public BaseEntity() {
    }

    public BaseEntity(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public BaseEntity(Long id, String name, boolean isChecked) {
        this.id = id;
        this.name = name;
        this.isChecked = isChecked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "BaseEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeByte(isChecked ? (byte) 1 : (byte) 0);
    }

    protected BaseEntity(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.isChecked = in.readByte() != 0;
    }

    public static final Creator<BaseEntity> CREATOR = new Creator<BaseEntity>() {
        public BaseEntity createFromParcel(Parcel source) {
            return new BaseEntity(source);
        }

        public BaseEntity[] newArray(int size) {
            return new BaseEntity[size];
        }
    };
}
