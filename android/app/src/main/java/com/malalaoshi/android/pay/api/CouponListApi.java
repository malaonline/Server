package com.malalaoshi.android.pay.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.pay.coupon.CouponResult;

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

    public CouponResult get() throws Exception {
        return httpGet(getPath(), CouponResult.class);
    }
}
