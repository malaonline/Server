package com.malalaoshi.android.usercenter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

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
 * Input student name and sync with server.
 * Created by tianwei on 12/27/15.
 */
public class VerifyStudentNameView extends LinearLayout {

    private Context context;
    @Bind(R.id.et_name)
    protected EditText nameEditView;

    public VerifyStudentNameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.view_verify_student_name, null);
        ButterKnife.bind(rootView);
    }

    @OnClick(R.id.btn_submit)
    protected void onSubmitClick() {
        syncStudentName();
    }

    /**
     * TODO Waiting for API reading
     */
    private void syncStudentName() {
        String url = MalaApplication.getInstance().getMalaHost() + "";//TODO Waiting API
        RequestQueue queue = MalaApplication.getHttpRequestQueue();
        JSONObject json = new JSONObject();
        try {
            json.put("student", nameEditView.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
        queue.add(request);
    }
}
