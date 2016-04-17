package com.malalaoshi.android.net;

import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.net.okhttp.UploadFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Network sender
 * Created by tianwei on 1/3/16.
 */
public class NetworkSender {

    private static final String URL_PROFILE = "/api/v1/profiles/%s";

    private static String getToken() {
        return Constants.CAP_TOKEN + " " + UserManager.getInstance().getToken();
    }

    public static void setUserAvatar(String strAvatarLocPath, NetworkListener networkListener) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTH, getToken());
        String profileId = UserManager.getInstance().getProfileId();
        UploadFile.uploadImg(strAvatarLocPath, String.format(URL_PROFILE, profileId), headers, networkListener);
    }
}
