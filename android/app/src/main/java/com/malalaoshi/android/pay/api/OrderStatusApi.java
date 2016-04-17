package com.malalaoshi.android.pay.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.OrderStatusModel;

/**
 * 请求订单状态
 * Created by tianwei on 4/17/16.
 */
public class OrderStatusApi extends BaseApi {

    private static final String URL_ORDER_STATUS = "/api/v1/orders/%s";

    @Override
    protected String getPath() {
        return URL_ORDER_STATUS;
    }

    public OrderStatusModel getOrderStatus(String orderId) throws Exception {
        String url = String.format(getPath(), orderId);
        return httpGet(url, OrderStatusModel.class);
    }
}
