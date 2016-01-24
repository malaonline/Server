package com.malalaoshi.android.event;

import com.malalaoshi.android.entity.CouponEntity;

/**
 * Post when one coupon is chose.
 * Created by tianwei on 1/24/16.
 */
public class ChoiceCouponEvent {
    public final CouponEntity COUPON;

    public ChoiceCouponEvent(CouponEntity entity) {
        this.COUPON = entity;
    }
}
