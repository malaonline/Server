package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.result.CourseListResult;

/**
 * Load time api
 * Created by tianwei on 4/17/16.
 */
public class TimeTableApi extends BaseApi {


    private static final String URL_TIMES_LOTS = "/api/v1/timeslots";

    @Override
    protected String getPath() {
        return URL_TIMES_LOTS;
    }

    public CourseListResult get() throws Exception {
        return httpGet(getPath(), CourseListResult.class);
    }
}
