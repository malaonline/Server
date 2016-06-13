package com.malalaoshi.android.course.api;

import com.malalaoshi.android.comment.CommentResult;
import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.Course;

/**
 * Course info api
 * Created by tianwei on 4/17/16.
 */
public class CourseInfoApi extends BaseApi {

    private static final String URL_TIMES_LOTS_BY_ID = "/api/v1/timeslots/%s";
    private static final String URL_COURSE_LIST = "/api/v1/timeslots";

    @Override
    protected String getPath() {
        return URL_TIMES_LOTS_BY_ID;
    }

    public Course getCourseInfo(String courseSubId) throws Exception {
        String url = String.format(getPath(), courseSubId);
        return httpGet(url, Course.class);
    }

    public CommentResult getCourseList() throws Exception {
        return httpGet(URL_COURSE_LIST, CommentResult.class);
    }
}
