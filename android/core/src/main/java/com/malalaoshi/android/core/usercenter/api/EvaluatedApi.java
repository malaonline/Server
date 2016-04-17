package com.malalaoshi.android.core.usercenter.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.usercenter.entity.Evaluated;

/**
 * Evaluated api
 * Created by tianwei on 4/17/16.
 */
public class EvaluatedApi extends BaseApi {

    private static final String URL_EVALUATED = "/api/v1/subject/%s/record";

    @Override
    protected String getPath() {
        return URL_EVALUATED;
    }


    public Evaluated get(long subjectId) throws Exception {
        String url = String.format(getPath(), subjectId);
        return httpGet(url, Evaluated.class);
    }
}
