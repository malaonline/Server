package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.Teacher;

/**
 * Teacher info api
 * Created by tianwei on 4/17/16.
 */
public class TeacherInfoApi extends BaseApi {


    private static final String URL_TEACHER = "/api/v1/teachers/%s";

    @Override
    protected String getPath() {
        return URL_TEACHER;
    }

    @Override
    protected boolean addAuthHeader() {
        return true;
    }

    public Teacher get(long teacherId) throws Exception {
        String url = String.format(getPath(), teacherId);
        return httpGet(url, Teacher.class);
    }
}
