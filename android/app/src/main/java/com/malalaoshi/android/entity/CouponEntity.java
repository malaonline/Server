package com.malalaoshi.android.entity;

/**
 * Coupon entity
 * Created by tianwei on 1/24/16.
 */
public class CouponEntity {
    String name;
    String amount;
    long expired_at;
    boolean used;
    String description;
    String id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
