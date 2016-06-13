package com.malalaoshi.android.course.api;

import com.malalaoshi.android.comment.CommentResult;
import com.malalaoshi.android.core.network.api.BaseApi;

/**
 * Course info api
 * Created by tianwei on 4/17/16.
 */
public class MoreCourseInfoApi extends BaseApi {

    @Override
    protected String getPath() {
        return "";
    }

    @Override
    protected String getUrl(String url) {
        return url;
    }

    public CommentResult getCourseList(String url) throws Exception {
        return httpGet(url, CommentResult.class);
    }
}
