package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.UnpayOrders;

/**
 * Created by kang on 16/5/10.
 */
public class UnpayOrderCountApi extends BaseApi {

    private static final String URL_UNPAY_ORDERS = "/api/v1/unpaid_count";

    @Override
    protected String getPath() {
        return URL_UNPAY_ORDERS;
    }

    public UnpayOrders get() throws Exception {
        return httpGet(getPath(), UnpayOrders.class);
    }
}
