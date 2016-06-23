package com.malalaoshi.android.pay.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.pay.coupon.CouponResult;

/**
 * Coupon list load more
 * Created by tianwei on 4/17/16.
 */
public class CouponListMoreApi extends BaseApi {

    @Override
    protected String getPath() {
        return "";
    }

    @Override
    protected String getUrl(String url) {
        return url;
    }

    public CouponResult loadMore(String url, boolean onlyValid) throws Exception {
        if (onlyValid) {
            url += "&only_valid=true";
        }
        return httpGet(url, CouponResult.class);
    }
}
