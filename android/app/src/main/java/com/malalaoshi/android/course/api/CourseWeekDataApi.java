package com.malalaoshi.android.course.api;

import com.malalaoshi.android.core.network.api.BaseApi;

/**
 * Week data
 * Created by tianwei on 4/17/16.
 */
public class CourseWeekDataApi extends BaseApi {

    private static final String URL_TEACHER_VALID_TIME = "/api/v1/teachers/%s/weeklytimeslots";

    @Override
    protected String getPath() {
        return URL_TEACHER_VALID_TIME;
    }

    public String get(long teacherId, long schoolId) throws Exception {
        String url = String.format(URL_TEACHER_VALID_TIME, teacherId + "");
        url += "?" + "school_id=" + schoolId;
        return httpGet(url, String.class);
    }
}
