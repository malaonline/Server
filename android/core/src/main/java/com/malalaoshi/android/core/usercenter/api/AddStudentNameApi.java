package com.malalaoshi.android.core.usercenter.api;

import com.malalaoshi.android.core.network.Callback;
import com.malalaoshi.android.core.network.Constants;
import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.usercenter.entity.AddStudentName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

/**
 * Set student name
 * Created by tianwei on 3/27/16.
 */
public class AddStudentNameApi extends BaseApi {

    private static final String URL_SAVE_CHILD_NAME = "/api/v1/parents/%s";

    public void get(String name, Callback<AddStudentName> callback) {
        String url = String.format(Locale.getDefault(),
                URL_SAVE_CHILD_NAME, UserManager.getInstance().getParentId());
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.STUDENT_NAME, name);
            httpPatch(url, json.toString(), callback, AddStudentName.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
