package com.malalaoshi.android.core.usercenter.api;

import com.malalaoshi.android.core.network.Constants;
import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.usercenter.entity.AddStudentName;

import org.json.JSONObject;

import java.util.Locale;

/**
 * Set student name
 * Created by tianwei on 3/27/16.
 */
public class AddStudentNameApi extends BaseApi {

    private static final String URL_SAVE_CHILD_NAME = "/api/v1/parents/%s";

    @Override
    protected String getPath() {
        return URL_SAVE_CHILD_NAME;
    }

    public AddStudentName get(String name) throws Exception {
        String url = String.format(Locale.getDefault(),
                getPath(), UserManager.getInstance().getParentId());
        JSONObject json = new JSONObject();
        json.put(Constants.STUDENT_NAME, name);
        return httpPatch(url, json.toString(), AddStudentName.class);
    }
}
