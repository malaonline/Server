package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.NoticeMessage;

/**
 * Created by kang on 16/5/10.
 */
public class NoticeMessageApi extends BaseApi {

    private static final String URL_UNPAY_ORDERS = "/api/v1/my_center";

    @Override
    protected String getPath() {
        return URL_UNPAY_ORDERS;
    }

    public NoticeMessage get() throws Exception {
        return httpGet(getPath(), NoticeMessage.class);
    }
}
