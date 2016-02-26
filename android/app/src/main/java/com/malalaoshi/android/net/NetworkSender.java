package com.malalaoshi.android.net;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.entity.CouponEntity;
import com.malalaoshi.android.entity.CreateChargeEntity;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Network sender
 * Created by tianwei on 1/3/16.
 */
public class NetworkSender {
    private static final String URL_FETCH_VERIFY_CODE = "/api/v1/sms";
    private static final String URL_GET_USER_POLICY = "/api/v1/policy";
    private static final String URL_SAVE_CHILD_NAME = "/api/v1/parent";
    private static final String URL_COUPON_LIST = "/api/v1/coupons";
    private static final String URL_CREATE_COURSE_ORDER = "/api/v1/orders";
    private static List<CouponEntity> couponList;

    public static void verifyCode(final Map<String, String> params, final NetworkListener listener) {
        postStringRequest(URL_FETCH_VERIFY_CODE, params, listener);
    }

    private static void postStringRequest(String url, final Map<String, String> params, final NetworkListener listener) {
        stringRequest(Request.Method.POST, url, params, listener);
    }

    private static void stringRequest(int method, String url,
                                      final Map<String, String> params, final NetworkListener listener) {
        url = MalaApplication.getInstance().getMalaHost() + url;
        RequestQueue queue = MalaApplication.getHttpRequestQueue();
        StringRequest request = new StringRequest(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                if (listener != null) {
                    listener.onSucceed(s);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (listener != null) {
                    listener.onFailed(volleyError);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        queue.add(request);
    }

    private static void postJsonRequest(String url, final Map<String, String> headers,
                                        JSONObject json, final NetworkListener listener) {
        jsonRequest(Request.Method.POST, url, headers, json, listener);
    }

    private static void jsonRequest(int method, String url, final Map<String, String> headers,
                                    JSONObject json, final NetworkListener listener) {
        url = MalaApplication.getInstance().getMalaHost() + url;
        RequestQueue queue = MalaApplication.getHttpRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(method, url, json, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject jsonObject) {
                listener.onSucceed(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onFailed(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
            }
        };
        queue.add(request);
    }

    private static void getStringRequest(String url, final Map<String, String> params, final NetworkListener listener) {
        stringRequest(Request.Method.GET, url, params, listener);
    }

    public static void getUserProtocol(NetworkListener listener) {
        getStringRequest(URL_GET_USER_POLICY, new HashMap<String, String>(), listener);
    }

    public static void saveChildName(JSONObject params, NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTH, Constants.CAP_TOKEN + " " + MalaApplication.getInstance().getToken());
        headers.put(Constants.CAP_CONTENT_TYPE, Constants.JSON);
        //TODO tianwei Waiting for sms verification api to get parentId
        String parentId = MalaApplication.getInstance().getParentId();
        jsonRequest(Request.Method.PATCH, URL_SAVE_CHILD_NAME + parentId, headers, params, listener);
    }

    public static void getCouponList(NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        stringRequest(Request.Method.GET, URL_COUPON_LIST, headers, listener);
    }

    public static void createCourseOrder(JSONObject json, NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTH, Constants.CAP_TOKEN + " " + MalaApplication.getInstance().getToken());
        postJsonRequest(URL_CREATE_COURSE_ORDER, headers, json, listener);
    }

    public static void getCharge(String orderId, CreateChargeEntity entity, NetworkListener listener) {
        if (entity == null) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        JSONObject json;
        try {
            json = new JSONObject(mapper.writeValueAsString(entity));
        } catch (Exception e) {
            return;
        }
        String url = URL_CREATE_COURSE_ORDER + "/" + orderId;
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTH, Constants.CAP_TOKEN + " " + MalaApplication.getInstance().getToken());
        jsonRequest(Request.Method.PATCH, url, headers, json, listener);
    }
}
