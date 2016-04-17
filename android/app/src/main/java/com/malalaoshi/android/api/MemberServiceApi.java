package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.result.MemberServiceListResult;

/**
 * Member service
 * Created by tianwei on 4/17/16.
 */
public class MemberServiceApi extends BaseApi {

    private static final String URL_GET_MEMBER_SERVICES = "/api/v1/memberservices";

    @Override
    protected String getPath() {
        return URL_GET_MEMBER_SERVICES;
    }

    @Override
    protected boolean addAuthHeader() {
        return false;
    }

    public MemberServiceListResult get() throws Exception {
        return httpGet(getPath(), MemberServiceListResult.class);
    }
}
