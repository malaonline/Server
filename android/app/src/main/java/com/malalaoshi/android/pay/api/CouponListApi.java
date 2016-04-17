package com.malalaoshi.android.pay.api;

import com.malalaoshi.android.core.network.api.BaseApi;

/**
 * Coupon list
 * Created by tianwei on 4/17/16.
 */
public class CouponListApi extends BaseApi {


    private static final String URL_COUPON_LIST = "/api/v1/coupons";

    @Override
    protected String getPath() {
        return URL_COUPON_LIST;
    }

    public String get() throws Exception {
        return httpGet(getPath(), String.class);
    }
}
