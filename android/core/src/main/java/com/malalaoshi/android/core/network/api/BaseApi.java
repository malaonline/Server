package com.malalaoshi.android.core.network.api;

import com.malalaoshi.android.core.MalaContext;
import com.malalaoshi.android.core.R;
import com.malalaoshi.android.core.network.Constants;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Base api
 * Created by tianwei on 3/27/16.
 */
public abstract class BaseApi {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();

    protected String getHost() {
        return MalaContext.getContext().getString(R.string.api_host);
    }

    protected abstract String getPath();

    /**
     * 是否添加Token Header.
     *
     * @return true
     */
    protected boolean addAuthHeader() {
        return true;
    }

    protected HashMap<String, String> getHeaders() {
        return null;
    }

    protected OkHttpClient getHttpClient() {
        return client;
    }

    /**
     * 构建URL
     *
     * @param url path
     * @return url
     */
    protected String getUrl(String url) {
        return getHost() + url;
    }

    private void addHeaders(Request.Builder builder) {
        if (addAuthHeader()) {
            builder.addHeader(Constants.AUTH, getToken());
        }
        if (EmptyUtils.isNotEmpty(getHeaders())) {
            for (Map.Entry<String, String> item : getHeaders().entrySet()) {
                builder.addHeader(item.getKey(), item.getValue());
            }
        }
    }


    @SuppressWarnings("unchecked")
    private <T> T http(Request.Builder builder, Class<T> cls) throws IOException {
        addHeaders(builder);
        okhttp3.Response response = getHttpClient().newCall(builder.build()).execute();
        String back = response.body().string();
        checkAuthError(response, back);
        //String类型直接返回
        if (cls.isAssignableFrom(String.class)) {
            return (T) back;
        }
        return JsonUtil.parseStringData(back, cls);
    }

    protected <T> T httpGet(String url, final Class<T> cls) throws Exception {
        final Request.Builder builder = new Request.Builder().url(getUrl(url));
        return http(builder, cls);
    }

    protected <T> T httpPost(final String url, String json, final Class<T> cls) throws Exception {
        RequestBody body = RequestBody.create(JSON, json);
        final Request.Builder builder = new okhttp3.Request.Builder()
                .url(getUrl(url))
                .post(body);
        return http(builder, cls);
    }

    public <T> T httpPatch(final String url, String json, final Class<T> cls) throws Exception {
        RequestBody body = RequestBody.create(JSON, json);
        final Request.Builder builder = new okhttp3.Request.Builder()
                .url(getUrl(url))
                .patch(body);
        return http(builder, cls);
    }

    protected <T> T httpDelete(String url, final Class<T> cls) throws Exception {
        final Request.Builder builder = new Request.Builder().url(getUrl(url)).delete();
        return http(builder, cls);
    }

    /**
     * 如果是403且错误消息是“Invalid token.”错误的话，强制用户登出。
     */
    private void checkAuthError(Response response, String body) {
        if (response == null || body == null) {
            return;
        }
        try {
            JSONObject json = new JSONObject(body);
            if (response.code() == 403 && json.optString("detail").equals("Invalid token.")) {
                UserManager.getInstance().logout();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
