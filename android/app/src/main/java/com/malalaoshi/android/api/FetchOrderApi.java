package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.result.OrderListResult;

/**
 * Created by kang on 16/5/10.
 */
public class FetchOrderApi extends BaseApi {
    private static final String URL_ORDERS = "/api/v1/orders/%s";

    @Override
    protected String getPath() {
        return URL_ORDERS;
    }

    public Order get(String orderId) throws Exception {
        String url = String.format(getPath(), orderId);
        return httpGet(url, Order.class);
    }
}
