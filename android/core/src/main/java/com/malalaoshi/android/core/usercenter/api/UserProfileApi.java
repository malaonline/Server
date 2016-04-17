package com.malalaoshi.android.core.usercenter.api;

import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.usercenter.entity.UserProfile;

/**
 * User profile api
 * Created by tianwei on 4/17/16.
 */
public class UserProfileApi extends BaseApi {

    private static final String URL_PROFILE = "/api/v1/profiles/%s";

    @Override
    protected String getPath() {
        return URL_PROFILE;
    }

    public UserProfile get() throws Exception {
        String url = String.format(getPath(), UserManager.getInstance().getProfileId());
        return httpGet(url, UserProfile.class);
    }

}
