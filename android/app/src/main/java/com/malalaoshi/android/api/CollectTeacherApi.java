package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.entity.DoneModel;
import com.malalaoshi.android.net.Constants;

import org.json.JSONObject;

/**
 * Base api
 * Created by tianwei on 4/17/16.
 */
public class CollectTeacherApi extends BaseApi {

    private static final String URL_ADD_FAVORITE = "/api/v1/favorites";

    @Override
    protected String getPath() {
        return URL_ADD_FAVORITE;
    }

    public DoneModel post(Long id) throws Exception {
        JSONObject json = new JSONObject();
        json.put(Constants.TEACHER_ID, id);
        return httpPost(getPath(), json.toString(), DoneModel.class);
    }
}
