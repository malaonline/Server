package com.malalaoshi.android.pay.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;

import org.json.JSONObject;

/**
 * Create order api
 * Created by tianwei on 4/17/16.
 */
public class CreateOrderApi extends BaseApi {
    @Override
    protected String getPath() {
        return "/api/v1/orders";
    }

    public CreateCourseOrderResultEntity createOrder(JSONObject json) throws Exception {
        return httpPost(getPath(), json.toString(), CreateCourseOrderResultEntity.class);
    }
}
