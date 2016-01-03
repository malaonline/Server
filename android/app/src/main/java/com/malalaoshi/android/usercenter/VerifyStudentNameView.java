package com.malalaoshi.android.usercenter;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.malalaoshi.android.R;
import com.malalaoshi.android.net.Constants;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.util.MiscUtil;

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

    private static final String TAG = "VerifyStudentNameView";
    @Bind(R.id.et_name)
    protected EditText nameEditView;
    @Bind(R.id.btn_submit)
    protected TextView submitView;

    private boolean isSubmitViewEnable;

    public VerifyStudentNameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.view_verify_student_name, this);
        setOrientation(VERTICAL);
        ButterKnife.bind(this, rootView);
        nameEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setSendButtonStatus(!TextUtils.isEmpty(charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setSendButtonStatus(boolean enable) {
        if (enable != isSubmitViewEnable) {
            isSubmitViewEnable = !isSubmitViewEnable;
            submitView.setEnabled(isSubmitViewEnable);
        }
    }

    @OnClick(R.id.btn_submit)
    protected void onSubmitClick() {
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.STUDENT_NAME, nameEditView.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        NetworkSender.saveChildName(json, new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                try {
                    JSONObject jo = new JSONObject(json.toString());
                    if (jo.optBoolean(Constants.DONE, false)) {
                        Log.i(TAG, "Set student's name succeed : " + json.toString());
                        MiscUtil.toast(R.string.usercenter_set_student_succeed);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setStudentNameFailed();
            }

            @Override
            public void onFailed(VolleyError error) {
                setStudentNameFailed();
            }
        });
    }

    private void setStudentNameFailed() {
        Log.i(TAG, "Set student's name failed.");
        MiscUtil.toast(R.string.usercenter_set_student_failed);
    }
}
