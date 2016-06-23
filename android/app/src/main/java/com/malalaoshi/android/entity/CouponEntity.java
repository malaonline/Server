package com.malalaoshi.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Coupon entity
 * Created by tianwei on 1/24/16.
 */
public class CouponEntity implements Parcelable {
    private int id;
    private String name;
    private String amount;
    private long expired_at;
    private String expiredDate;
    private boolean used;
    private String description;
    private boolean check;
    private int mini_total_price;

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

    public String getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(String expiredDate) {
        this.expiredDate = expiredDate;
    }

    public int getMini_total_price() {
        return mini_total_price;
    }

    public void setMini_total_price(int mini_total_price) {
        this.mini_total_price = mini_total_price;
    }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.amount);
        dest.writeLong(this.expired_at);
        dest.writeString(this.expiredDate);
        dest.writeByte(this.used ? (byte) 1 : (byte) 0);
        dest.writeString(this.description);
        dest.writeByte(this.check ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mini_total_price);
    }

    public CouponEntity() {}

    protected CouponEntity(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.amount = in.readString();
        this.expired_at = in.readLong();
        this.expiredDate = in.readString();
        this.used = in.readByte() != 0;
        this.description = in.readString();
        this.check = in.readByte() != 0;
        this.mini_total_price = in.readInt();
    }

    public static final Creator<CouponEntity> CREATOR = new Creator<CouponEntity>() {
        @Override
        public CouponEntity createFromParcel(Parcel source) {return new CouponEntity(source);}

        @Override
        public CouponEntity[] newArray(int size) {return new CouponEntity[size];}
    };
}
