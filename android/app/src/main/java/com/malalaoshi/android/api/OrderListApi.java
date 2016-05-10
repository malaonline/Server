package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.result.OrderListResult;
import com.malalaoshi.android.result.SchoolListResult;

import java.util.HashMap;

/**
 * Get shcool list
 * Created by tianwei on 4/17/16.
 */
public class OrderListApi extends BaseApi {
    private static final String URL_ORDERS = "/api/v1/orders";

    @Override
    protected String getPath() {
        return URL_ORDERS;
    }

    public OrderListResult get() throws Exception {
        return httpGet(getPath(), OrderListResult.class);
    }
}
