package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.result.OrderListResult;

import java.util.HashMap;

/**
 * Created by kang on 16/5/6.
 */
public class MoreOrderListApi extends BaseApi {

    @Override
    protected String getPath() {
        return "";
    }

    @Override
    public String getUrl(String url) {
        return url;
    }


    public OrderListResult getOrderList(String nextUrl) throws Exception {
        return httpGet(nextUrl, OrderListResult.class);
    }
}