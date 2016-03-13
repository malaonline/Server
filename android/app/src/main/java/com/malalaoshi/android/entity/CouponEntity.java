package com.malalaoshi.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Coupon entity
 * Created by tianwei on 1/24/16.
 */
public class CouponEntity implements Parcelable {
    String name;
    String amount;
    long expired_at;
    boolean used;
    String description;
    int id;
    String useType;
    boolean check;

    public String getUseType() {
        return useType;
    }

    public void setUseType(String userType) {
        this.useType = userType;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setExpired_at(long expired_at) {
        this.expired_at = expired_at;
    }

    public long getExpired_at() {
        return expired_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.amount);
        dest.writeLong(this.expired_at);
        dest.writeByte(used ? (byte) 1 : (byte) 0);
        dest.writeString(this.description);
        dest.writeInt(this.id);
        dest.writeString(this.useType);
        dest.writeByte(check ? (byte) 1 : (byte) 0);
    }

    public CouponEntity() {
    }

    protected CouponEntity(Parcel in) {
        this.name = in.readString();
        this.amount = in.readString();
        this.expired_at = in.readLong();
        this.used = in.readByte() != 0;
        this.description = in.readString();
        this.id = in.readInt();
        this.useType = in.readString();
        this.check = in.readByte() != 0;
    }

    public static final Creator<CouponEntity> CREATOR = new Creator<CouponEntity>() {
        public CouponEntity createFromParcel(Parcel source) {
            return new CouponEntity(source);
        }

        public CouponEntity[] newArray(int size) {
            return new CouponEntity[size];
        }
    };
}
