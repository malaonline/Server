package com.malalaoshi.android.core.usercenter.api;

import com.malalaoshi.android.core.network.Callback;
import com.malalaoshi.android.core.network.Constants;
import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.usercenter.entity.AuthUser;
import com.malalaoshi.android.core.usercenter.entity.SendSms;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * User api
 * Created by tianwei on 3/27/16.
 */
public class VerifyCodeApi extends BaseApi {
    private static final String URL_FETCH_VERIFY_CODE = "/api/v1/sms";

    public void get(String phone, Callback<SendSms> callback) {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.ACTION, Constants.SEND);
            json.put(Constants.PHONE, phone);
            httpPost(URL_FETCH_VERIFY_CODE, json.toString(), callback, SendSms.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void check(String phone, String code, Callback<AuthUser> callback) {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.ACTION, Constants.VERIFY);
            json.put(Constants.PHONE, phone);
            json.put(Constants.CODE, code);
            httpPost(URL_FETCH_VERIFY_CODE, json.toString(), callback, AuthUser.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
