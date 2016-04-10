package com.malalaoshi.android.core.usercenter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.malalaoshi.android.core.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.UIResultCallback;
import com.malalaoshi.android.core.usercenter.api.AddStudentNameApi;
import com.malalaoshi.android.core.usercenter.entity.AddStudentName;
import com.malalaoshi.android.core.utils.MiscUtil;

import de.greenrobot.event.EventBus;

/**
 * Input student name and sync with server.
 * Created by tianwei on 12/27/15.
 */
public class AddStudentNameActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "VerifyStudentNameView";
    protected EditText nameEditView;
    protected TextView submitView;
    private String name;
    private boolean isSubmitViewEnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core__activity_add_student_name);
        nameEditView = (EditText) findViewById(R.id.et_name);
        submitView = (TextView) findViewById(R.id.btn_submit);
        submitView.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_submit) {
            onSubmitClick();
        }
    }

    private void setSendButtonStatus(boolean enable) {
        if (enable != isSubmitViewEnable) {
            isSubmitViewEnable = !isSubmitViewEnable;
            submitView.setEnabled(isSubmitViewEnable);
        }
    }

    private static final class AddStudentNameCallback extends UIResultCallback<AddStudentNameActivity, AddStudentName> {
        public AddStudentNameCallback(AddStudentNameActivity addStudentNameActivity) {
            super(addStudentNameActivity);
        }

        @Override
        public void onResult(@NonNull AddStudentNameActivity activity, AddStudentName addStudentName) {
            if (addStudentName != null && addStudentName.isDone()) {
                MiscUtil.toast("设置学名名字成功");
                activity.updateStuName();
                EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_TIMETABLE_DATA));
                EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_USERCENTER_DATA));
            } else {
                activity.setStudentNameFailed();
            }
            activity.finish();
        }
    }

    protected void onSubmitClick() {
        AddStudentNameApi api = new AddStudentNameApi();
        name = nameEditView.getText().toString();
        if (TextUtils.isEmpty(name)) {
            MiscUtil.toast("姓名不能为空");
            return;
        }
        api.get(name, new AddStudentNameCallback(this));
    }

    private void setStudentNameFailed() {
        Log.i(TAG, "Set student's name failed.");
        MiscUtil.toast("设置学生姓名失败");
    }

    private void updateStuName() {
        if (!TextUtils.isEmpty(name)) {
            UserManager.getInstance().setStuName(name);
        }
    }

    @Override
    protected String getStatName() {
        return "添加学生姓名";
    }
}
