package com.malalaoshi.android.net;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.result.TeacherListResult;

/**
 * More teacher list api
 * Created by tianwei on 4/17/16.
 */
public class MoreTeacherListApi extends BaseApi {

    @Override
    protected String getPath() {
        return "";
    }

    @Override
    public String getUrl(String url) {
        return url;
    }

    @Override
    protected boolean addAuthHeader() {
        return false;
    }

    public TeacherListResult getTeacherList(String nextUrl) throws Exception {
        return httpGet(nextUrl, TeacherListResult.class);
    }
}
