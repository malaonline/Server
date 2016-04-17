package com.malalaoshi.android.course.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.TimesModel;

/**
 * Course times api
 * Created by tianwei on 4/17/16.
 */
public class CourseTimesApi extends BaseApi {

    private static final String URL_CONCRETE_TIME_SLOT = "/api/v1/concrete/timeslots";

    @Override
    protected String getPath() {
        return URL_CONCRETE_TIME_SLOT;
    }

    public TimesModel get(long teacherId, long hours, String times) throws Exception {
        String url = getPath();
        url += "?hours=" + hours;
        url += "&weekly_time_slots=" + times;
        url += "&teacher=" + teacherId;
        return httpGet(url, TimesModel.class);
    }
}
