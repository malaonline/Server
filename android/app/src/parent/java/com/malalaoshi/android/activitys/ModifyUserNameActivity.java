package com.malalaoshi.android.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

import com.malalaoshi.android.R;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.net.Constants;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.util.UIResultCallback;
import com.malalaoshi.android.util.UserManager;
import com.malalaoshi.android.view.TitleBarView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/1/25.
 */
public class ModifyUserNameActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {

    public static String TAG = "ModifyUserNameActivity";
    public static int RESULT_CODE_NAME = 0x001;
    public static String EXTRA_USER_NAME = "username";

    @Bind(R.id.titleBar)
    TitleBarView titleBar;

    @Bind(R.id.et_value)
    EditText etUserName;

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_modify_singlevalue);
        ButterKnife.bind(this);
        titleBar.setOnTitleBarClickListener(this);
        initDatas();
    }

    private void initDatas() {
        Intent intent = getIntent();
        userName = intent.getStringExtra(EXTRA_USER_NAME);
        if (userName == null) {
            userName = "";
        }
        etUserName.setMaxLines(30);
        etUserName.setText(userName);
        etUserName.setSelection(userName.length());
        titleBar.setTitle("更改名字");
    }

    @Override
    public void onTitleLeftClick() {
        this.finish();
    }

    @Override
    public void onTitleRightClick() {
        postModifyUserName();

    }

    private void postModifyUserName() {
        userName = etUserName.getText().toString();
        if (TextUtils.isEmpty(userName)) {
            MiscUtil.toast(R.string.usercenter_student_empty);
            return;
        }

        JSONObject json = new JSONObject();
        try {
            json.put(Constants.STUDENT_NAME, userName);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        NetworkSender.saveChildName(json, new UIResultCallback<String>() {
            @Override
            protected void onResult(String s) {

            }
        });

        NetworkSender.saveChildName(json, new UIResultCallback<String>() {
            @Override
            protected void onResult(String json) {
                try {
                    JSONObject jo = new JSONObject(json);
                    if (jo.optBoolean(Constants.DONE, false)) {
                        Log.i(TAG, "Set student's name succeed : " + json);
                        MiscUtil.toast(R.string.usercenter_set_student_succeed);
                        updateStuName(userName);
                        setActivityResult();
                        finish();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setStudentNameFailed();
            }
        });
    }

    private void setStudentNameFailed() {
        Log.i(TAG, "Set student's name failed.");
        MiscUtil.toast(R.string.usercenter_set_student_failed);
    }

    private void updateStuName(String name) {
        if (!TextUtils.isEmpty(name)) {
            UserManager.getInstance().setStuName(name);
        }
    }

    private void setActivityResult() {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_NAME, userName);
        setResult(RESULT_CODE_NAME, intent);
    }
}
