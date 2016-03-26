package com.malalaoshi.android.core.network.api;

import android.text.TextUtils;
import android.util.Log;

import com.malalaoshi.android.core.BuildConfig;
import com.malalaoshi.android.core.MalaContext;
import com.malalaoshi.android.core.network.Callback;
import com.malalaoshi.android.core.network.Constants;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.utils.JsonUtil;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * Base api
 * Created by tianwei on 3/27/16.
 */
public abstract class BaseApi {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    protected String getHost() {
        return BuildConfig.API_HOST;
    }

    protected OkHttpClient getHttpClient() {
        return client;
    }

    protected <T> void httpPost(final String url, String json, final Callback<T> callback, final Class<T> cls) {
        if (TextUtils.isEmpty(json)) {
            json = (new JSONObject()).toString();
        }
        final String requestBody = json;
        RequestBody body = RequestBody.create(JSON, requestBody);
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(getHost() + url)
                .post(body)
                .build();
        MalaContext.exec(new Runnable() {
            @Override
            public void run() {
                try {
                    okhttp3.Response response = getHttpClient().newCall(request).execute();
                    String back = response.body().string();
                    T t = JsonUtil.parseStringData(back, cls);
                    if (callback != null) {
                        callback.setResult(t);
                    }
                } catch (Exception e) {
                    Log.e("MALA", " " + e.toString());
                    if (callback != null) {
                        callback.setResult(null);
                    }
                }
            }
        });
    }

    public <T> void httpPatch(final String url, String json, final Callback<T> callback, final Class<T> cls) {
        if (TextUtils.isEmpty(json)) {
            json = (new JSONObject()).toString();
        }
        RequestBody body = RequestBody.create(JSON, json);
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(getHost() + url)
                .patch(body)
                .addHeader(Constants.AUTH, getToken())
                .build();
        final OkHttpClient client = new OkHttpClient();
        MalaContext.exec(new Runnable() {
            @Override
            public void run() {
                try {
                    okhttp3.Response response = client.newCall(request).execute();
                    String back = response.body().string();
                    T t = JsonUtil.parseStringData(back, cls);
                    if (callback != null) {
                        callback.setResult(t);
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.setResult(null);
                    }
                }
            }
        });
    }

    protected <T> void httpGet(String url, final Callback<T> callback, final Class<T> cls) {
        final Request request = new Request.Builder().url(getHost() + url).build();
        MalaContext.exec(new Runnable() {
            @Override
            public void run() {
                try {
                    okhttp3.Response response = getHttpClient().newCall(request).execute();
                    String back = response.body().string();
                    T t = JsonUtil.parseStringData(back, cls);
                    callback.setResult(t);
                } catch (Exception e) {
                    Log.e("MALA", " " + e.toString());
                    callback.setResult(null);
                }
            }
        });
    }

    private String buildUrl(String url, Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        builder.append(getHost()).append(url);
        if (builder.indexOf("?") < 0) {
            builder.append("?");
        } else {
            builder.append("&");
        }
        if (params == null || params.size() == 0) {
            return builder.toString();
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        if (builder.toString().endsWith("&")) {
            builder.deleteCharAt(builder.toString().length() - 1);
        }
        return builder.toString();
    }

    private String getToken() {
        return Constants.CAP_TOKEN + " " + UserManager.getInstance().getToken();
    }

}
