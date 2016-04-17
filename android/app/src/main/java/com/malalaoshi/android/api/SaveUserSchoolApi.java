package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.DoneModel;

/**
 * Save school name
 * Created by tianwei on 4/17/16.
 */
public class SaveUserSchoolApi extends BaseApi {

    private static final String URL_SAVE_CHILD_SCHOOL = "/api/v1/parents/%s";

    @Override
    protected String getPath() {
        return URL_SAVE_CHILD_SCHOOL;
    }

    public DoneModel saveUserSchoolApi(String body) throws Exception {
        return httpPatch(getPath(), body, DoneModel.class);
    }
}
