package com.malalaoshi.android.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.entity.DoneModel;

import java.util.Locale;

/**
 * Base api
 * Created by tianwei on 4/17/16.
 */
public class PostAvatarApi extends BaseApi {

    private static final String URL_PROFILE = "/api/v1/profiles/%s";

    @Override
    protected String getPath() {
        return URL_PROFILE;
    }

    public DoneModel post(String filePath) throws Exception {
        String url = String.format(Locale.getDefault(),
                getPath(), UserManager.getInstance().getProfileId());
        return httpPatchImg(url, filePath, DoneModel.class);
    }
}
