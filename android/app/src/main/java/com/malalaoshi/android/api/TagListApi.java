package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.result.TagListResult;

/**
 * Teacher info api
 * Created by tianwei on 4/17/16.
 */
public class TagListApi extends BaseApi {


    private static final String URL_TAGS = "/api/v1/tags";

    @Override
    protected String getPath() {
        return URL_TAGS;
    }

    @Override
    protected boolean addAuthHeader() {
        return false;
    }

    public TagListResult get() throws Exception {
        return httpGet(getPath(), TagListResult.class);
    }
}
