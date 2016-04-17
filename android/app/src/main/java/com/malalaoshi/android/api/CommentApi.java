package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.Comment;

/**
 * Base api
 * Created by tianwei on 4/17/16.
 */
public class CommentApi extends BaseApi {

    private static final String URL_CREATE_COMMENT = "/api/v1/comments";

    @Override
    protected String getPath() {
        return URL_CREATE_COMMENT;
    }

    @Override
    protected boolean addAuthHeader() {
        return false;
    }

    public Comment get(long commentID) throws Exception {
        String url = getPath() + "/" + commentID;
        return httpGet(url, Comment.class);
    }
}
