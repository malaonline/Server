package com.malalaoshi.android.pay.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.CreateChargeEntity;

import org.json.JSONObject;

/**
 * Fetch charge api
 * Created by tianwei on 4/17/16.
 */
public class FetchChargeApi extends BaseApi {

    private static final String URL_CREATE_COURSE_ORDER = "/api/v1/orders/%s";

    @Override
    protected String getPath() {
        return URL_CREATE_COURSE_ORDER;
    }

    public String getCharge(String orderId, CreateChargeEntity entity) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        final JSONObject json;
        json = new JSONObject(mapper.writeValueAsString(entity));
        final String url = String.format(URL_CREATE_COURSE_ORDER, orderId);
        return httpPatch(url, json.toString(), String.class);
    }
}
