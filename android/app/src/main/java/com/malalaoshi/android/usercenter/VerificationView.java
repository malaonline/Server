package com.malalaoshi.android.usercenter;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Verify the phone of user by verify code.
 * Created by tianwei on 12/27/15.
 */
public class VerificationView extends RelativeLayout {

    private Context context;

    @Bind(R.id.et_code)
    protected EditText codeEditView;
    @Bind(R.id.et_Phone)
    protected EditText phoneEditView;
    private static final String FETCH_CODE_URL = "/api/v1/sms";

    public VerificationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.activity_usercenter, null);
        addView(rootView);
        ButterKnife.bind(rootView);
    }

    @OnClick(R.id.tv_fetch_code)
    protected void onFetchCodeClick() {
        if (TextUtils.isEmpty(phoneEditView.getText())) {
            toast("手机号不能为空！");
            return;
        }
        fetchVerifyCode();
    }

    @OnClick(R.id.btn_verify)
    protected void onVerifyClick() {
        Verify();
    }

    @OnClick(R.id.btn_user_protocal)
    protected void openUserProtocal() {
        if (context != null && context instanceof UsercenterActivity) {
            ((UsercenterActivity) context).getHandler()
                    .obtainMessage(UsercenterActivity.MSG_USER_PROTOCOL).sendToTarget();
        }
    }

    /**
     * TODO Waiting for API reading
     */
    private void fetchVerifyCode() {
        String url = MalaApplication.getInstance().getMalaHost() + FETCH_CODE_URL;
        RequestQueue queue = MalaApplication.getHttpRequestQueue();
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.ACTION, "send");
            json.put(Constants.PHONE, phoneEditView.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (jsonObject != null && jsonObject.optBoolean(Constants.SENT, false)) {
                            fetchCodeFaild();
                        } else {
                            fetchSucceeded();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        fetchCodeFaild();
                    }
                });
        queue.add(request);
    }

    private void toast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    private void fetchSucceeded() {
        toast("验证码已发送");
    }

    private void fetchCodeFaild() {
        toast("验验证码发送失败，请稍候重试！");
    }

    private void Verify() {
        String url = MalaApplication.getInstance().getMalaHost() + FETCH_CODE_URL;
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.ACTION, "verify");
            json.put(Constants.PHONE, phoneEditView.getText().toString());
            json.put(Constants.CODE, codeEditView.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        RequestQueue queue = MalaApplication.getHttpRequestQueue();
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        if (jsonObject == null) {
                            verifyFaild();
                            return;
                        }
                        if (jsonObject.optBoolean(Constants.VERIFIED, false)) {
                            verifySucceeded(jsonObject);
                        } else {
                            verifyFaild();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
        queue.add(request);
    }

    private void verifyFaild() {
    }

    private void verifySucceeded(JSONObject json) {
        UserProfile.getInstance().setUserVerifyToken(json.optString(Constants.TOKEN));
        if (json.optBoolean(Constants.FIRST_LOGIN, false)) {
            if (context != null && context instanceof UsercenterActivity) {
                ((UsercenterActivity) context).getHandler()
                        .obtainMessage(UsercenterActivity.MSG_VERIFY_NAME).sendToTarget();
            }
        }
    }
}
