package com.malalaoshi.android.core.usercenter.api;

import com.malalaoshi.android.core.network.Constants;
import com.malalaoshi.android.core.network.api.BaseApi;
import com.malalaoshi.android.core.usercenter.entity.AuthUser;
import com.malalaoshi.android.core.usercenter.entity.SendSms;

import org.json.JSONObject;

/**
 * User api
 * Created by tianwei on 3/27/16.
 */
public class VerifyCodeApi extends BaseApi {

    private static final String URL_FETCH_VERIFY_CODE = "/api/v1/sms";

    @Override
    protected String getPath() {
        return URL_FETCH_VERIFY_CODE;
    }

    @Override
    protected boolean addAuthHeader() {
        return false;
    }

    public SendSms get(String phone) throws Exception {
        JSONObject json = new JSONObject();
        json.put(Constants.ACTION, Constants.SEND);
        json.put(Constants.PHONE, phone);
        return httpPost(URL_FETCH_VERIFY_CODE, json.toString(), SendSms.class);
    }

    public AuthUser check(String phone, String code) throws Exception {
        JSONObject json = new JSONObject();
        json.put(Constants.ACTION, Constants.VERIFY);
        json.put(Constants.PHONE, phone);
        json.put(Constants.CODE, code);
        return httpPost(getPath(), json.toString(), AuthUser.class);
    }
}
