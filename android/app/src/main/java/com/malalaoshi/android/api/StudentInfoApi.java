package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.result.UserListResult;

/**
 * Get student info
 * Created by tianwei on 4/17/16.
 */
public class StudentInfoApi extends BaseApi {

    private static final String URL_PARENT = "/api/v1/parents";

    @Override
    protected String getPath() {
        return URL_PARENT;
    }

    public UserListResult getStudentInfo() throws Exception {
        return httpGet(getPath(), UserListResult.class);
    }
}
