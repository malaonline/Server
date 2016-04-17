package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.Comment;

/**
 * Base api
 * Created by tianwei on 4/17/16.
 */
public class PostCommentApi extends BaseApi {

    private static final String URL_CREATE_COMMENT = "/api/v1/comments";

    @Override
    protected String getPath() {
        return URL_CREATE_COMMENT;
    }

    public Comment post(String body) throws Exception {
        return httpPost(getPath(), body, Comment.class);
    }
}
