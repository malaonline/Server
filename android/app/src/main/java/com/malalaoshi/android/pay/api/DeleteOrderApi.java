package com.malalaoshi.android.pay.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.result.OkResult;

/**
 * Delete order api
 * Created by tianwei on 4/17/16.
 */
public class DeleteOrderApi extends BaseApi {


    private static final String URL_CANCEL_ORDER = "/api/v1/orders/%s";

    @Override
    protected String getPath() {
        return URL_CANCEL_ORDER;
    }

    public OkResult delete(String orderId) throws Exception {
        String url = String.format(URL_CANCEL_ORDER, orderId);
        return httpDelete(url, OkResult.class);
    }
}
