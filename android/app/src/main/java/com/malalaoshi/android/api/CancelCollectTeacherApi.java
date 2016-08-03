package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.Comment;
import com.malalaoshi.android.entity.DoneModel;
import com.malalaoshi.android.entity.Order;

/**
 * Base api
 * Created by tianwei on 4/17/16.
 */
public class CancelCollectTeacherApi extends BaseApi {

    private static final String URL_DEL_FAVORITE = "/api/v1/favorites/%d";

    @Override
    protected String getPath() {
        return URL_DEL_FAVORITE;
    }

    public DoneModel delete(Long id) throws Exception {
        String url = String.format(getPath(), id);
        return httpDelete(url, DoneModel.class);
    }
}
