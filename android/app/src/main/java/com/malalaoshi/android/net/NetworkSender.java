package com.malalaoshi.android.net;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.entity.CreateChargeEntity;
import com.malalaoshi.android.net.okhttp.UploadFile;
import com.malalaoshi.android.util.MalaContext;
import com.malalaoshi.android.util.UIResultCallback;
import com.malalaoshi.android.util.UserManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

/**
 * Network sender
 * Created by tianwei on 1/3/16.
 */
public class NetworkSender {
    private static final String URL_FETCH_VERIFY_CODE = "/api/v1/sms";
    private static final String URL_GET_USER_POLICY = "/api/v1/policy";
    private static final String URL_SAVE_CHILD_NAME = "/api/v1/parents/%s";
    private static final String URL_COUPON_LIST = "/api/v1/coupons";
    private static final String URL_SCHOOL = "/api/v1/schools";
    private static final String URL_TEACHER = "/api/v1/teachers/%s";
    private static final String URL_SUBJECT = "/api/v1/subjects";
    private static final String URL_TEACHER_VALID_TIME = "/api/v1/teachers/%s/weeklytimeslots";
    private static final String URL_CREATE_COURSE_ORDER = "/api/v1/orders/%s";
    private static final String URL_GET_COMMENT = "/api/v1/comments/%s";
    private static final String URL_CREATE_COMMENT = "/api/v1/comments";
    private static final String URL_TIMES_LOTS = "/api/v1/timeslots";
    private static final String URL_CONCRETE_TIME_SLOT = "/api/v1/concrete/timeslots";
    private static final String URL_EVALUATED = "/api/v1/subject/%s/record";
    private static final String URL_GET_PROFILE = "/api/v1/profiles/%s";
    private static final String URL_SET_PROFILE = "/api/v1/profiles/%s";
    private static final String URL_PARENT = "/api/v1/parents";
    private static final String URL_SAVE_CHILD_SCHOOL = "/api/v1/parents/%s";

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private static final String URL_TIMES_LOTS_BY_ID = "/api/v1/timeslots/%s";
    //private static List<CouponEntity> couponList;
    public static void verifyCode(final Map<String, String> params, final NetworkListener listener) {
        postStringRequest(URL_FETCH_VERIFY_CODE, params, listener);
    }

    private static void postStringRequest(String url, final Map<String, String> params, final NetworkListener listener) {
        url = MalaApplication.getInstance().getMalaHost() + url;
        RequestQueue queue = MalaApplication.getHttpRequestQueue();
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
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

    private static void stringRequest(int method, String url, final Map<String, String> headers, final NetworkListener listener) {
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
            public Map<String, String> getHeaders() throws AuthFailureError {
                return headers;
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

    public static void httpPatch(final String url, final String json, final UIResultCallback<String> callback) {
        final String uri = MalaApplication.getInstance().getMalaHost() + url;
        MalaContext.exec(new Runnable() {
            @Override
            public void run() {
                RequestBody body = RequestBody.create(JSON, json);
                okhttp3.Request request = new okhttp3.Request.Builder()
                        .url(uri)
                        .patch(body)
                        .addHeader(Constants.AUTH, getToken())
                        .build();
                OkHttpClient client = new OkHttpClient();
                try {
                    okhttp3.Response response = client.newCall(request).execute();
                    if (callback != null) {
                        callback.setResult(response.body().string());
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.setResult(null);
                    }
                }
            }
        });
    }

    private static void getStringRequest(String url, final Map<String, String> params, final NetworkListener listener) {
        stringRequest(Request.Method.GET, url, params, listener);
    }

    private static String getToken() {
        return Constants.CAP_TOKEN + " " + UserManager.getInstance().getToken();
    }

    public static void getUserProtocol(NetworkListener listener) {
        getStringRequest(URL_GET_USER_POLICY, new HashMap<String, String>(), listener);
    }

    public static void saveChildName(JSONObject params, UIResultCallback<String> listener) {
        String parentId = UserManager.getInstance().getParentId();
        httpPatch(String.format(URL_SAVE_CHILD_NAME, parentId), params.toString(), listener);
    }

    public static void getCouponList(NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTH, getToken());
        stringRequest(Request.Method.GET, URL_COUPON_LIST, headers, listener);
    }

    public static void createCourseOrder(JSONObject json, NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTH, getToken());
        postJsonRequest("/api/v1/orders", headers, json, listener);
    }

    public static void getCharge(String orderId, CreateChargeEntity entity, final UIResultCallback callback) {
        if (entity == null) {
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        final JSONObject json;
        try {
            json = new JSONObject(mapper.writeValueAsString(entity));
            final String url = String.format(URL_CREATE_COURSE_ORDER, orderId);
            httpPatch(url, json.toString(), callback);
        } catch (Exception e) {
            Log.e("MALA", "Json error");
        }
    }

    public static void getTimetable(NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTH, getToken());
        stringRequest(Request.Method.GET, URL_TIMES_LOTS, headers, listener);

    }

    public static void getSchoolList(NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.REGION, getToken());
        stringRequest(Request.Method.GET, URL_SCHOOL, headers, listener);
    }

    public static void getTeacherInfo(String teacherId, NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        stringRequest(Request.Method.GET, String.format(URL_TEACHER, teacherId), headers, listener);
    }

    public static void getComment(String commentId, NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        stringRequest(Request.Method.GET, String.format(URL_GET_COMMENT, commentId), headers, listener);
    }

    public static void submitComment(JSONObject params, NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTH, getToken());
        headers.put(Constants.CAP_CONTENT_TYPE, Constants.JSON);
        jsonRequest(Request.Method.POST, URL_CREATE_COMMENT, headers, params, listener);
    }

    public static void getCourseWeek(Long teacherId, Long schoolId, NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        String url = String.format(URL_TEACHER_VALID_TIME, teacherId + "");
        url += "?" + "school_id=" + schoolId;
        headers.put(Constants.AUTH, getToken());
        stringRequest(Request.Method.GET, url, headers, listener);
    }

    public static void fetchCourseTimes(Long teacherId, String times, String hours, NetworkListener listener) {
        String url = URL_CONCRETE_TIME_SLOT;
        url += "?hours=" + hours;
        url += "&weekly_time_slots=" + times;
        url += "&teacher=" + teacherId;
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTH, getToken());
        stringRequest(Request.Method.GET, url, headers, listener);
    }

    public static void getSubjectList(NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        stringRequest(Request.Method.GET, URL_SUBJECT, headers, listener);
    }

    public static void getEvaluated(Long id, NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTH, getToken());
        String url = String.format(URL_EVALUATED, id);
        stringRequest(Request.Method.GET, url, headers, listener);
    }

    public static void getUserPolicy(NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        stringRequest(Request.Method.GET, String.format(URL_GET_PROFILE, UserManager.getInstance().getProfileId()), headers, listener);
    }

    public static void getStuInfo(NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTH, getToken());
        stringRequest(Request.Method.GET, URL_PARENT, headers, listener);
    }

    public static void saveChildSchool(JSONObject params, UIResultCallback<String> listener) {
        String parentId = UserManager.getInstance().getParentId();
        httpPatch(String.format(URL_SAVE_CHILD_SCHOOL, parentId), params.toString(), listener);
    }

    public static void setUserAvatar(String strAvatorLocPath, NetworkListener networkListener) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTH, getToken());
        //headers.put(Constants.CAP_CONTENT_TYPE, Constants.JSON);
        String profileId = UserManager.getInstance().getProfileId();
        UploadFile.uploadImg(strAvatorLocPath, String.format(URL_SET_PROFILE, profileId), headers, networkListener);
    }

    public static void getCourseInfo(String courseSubId, NetworkListener listener) {
        Map<String, String> headers = new HashMap<>();
        headers.put(Constants.AUTH, getToken());
        stringRequest(Request.Method.GET, String.format(URL_TIMES_LOTS_BY_ID, courseSubId), headers, listener);
    }
}
