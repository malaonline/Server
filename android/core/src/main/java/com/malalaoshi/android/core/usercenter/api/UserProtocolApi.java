package com.malalaoshi.android.core.usercenter.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.usercenter.entity.UserPolicy;

/**
 * Base api
 * Created by tianwei on 3/27/16.
 */
public class UserProtocolApi extends BaseApi {
    private static final String URL_GET_USER_POLICY = "/api/v1/policy";

    @Override
    protected String getPath() {
        return URL_GET_USER_POLICY;
    }

    @Override
    protected boolean addAuthHeader() {
        return false;
    }

    public UserPolicy get() throws Exception {
        return httpGet(getPath(), UserPolicy.class);
    }
}
