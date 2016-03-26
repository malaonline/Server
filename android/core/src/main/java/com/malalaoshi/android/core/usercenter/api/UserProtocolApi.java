package com.malalaoshi.android.core.usercenter.api;

import com.malalaoshi.android.core.network.UIResultCallback;
import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.usercenter.entity.UserPolicy;
import com.malalaoshi.android.core.usercenter.UserProtocolActivity;

/**
 * Base api
 * Created by tianwei on 3/27/16.
 */
public class UserProtocolApi extends BaseApi {
    private static final String URL_GET_USER_POLICY = "/api/v1/policy";

    public void get(UIResultCallback<UserProtocolActivity, UserPolicy> callback) {
        httpGet(URL_GET_USER_POLICY, callback, UserPolicy.class);
    }
}
