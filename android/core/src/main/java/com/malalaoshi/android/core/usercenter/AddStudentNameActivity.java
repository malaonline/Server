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
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
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
        if (submitView != null) {
            submitView.setOnClickListener(this);
        }
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

    private void onAddStudentNameSuccess(@NonNull AddStudentName data) {
        if (data.isDone()) {
            MiscUtil.toast("设置学生名字成功");
            updateStuName();
            EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_UPDATE_USER_NAME_SUCCESS));
        } else {
            onAddStudentNameFailed();
        }
        finish();
    }

    private void onAddStudentNameFailed() {
        nameEditView.setText("");
        Log.i(TAG, "Set student's name failed.");
        MiscUtil.toast("设置学生姓名失败");
    }

    protected void onSubmitClick() {
        name = nameEditView.getText().toString();
        if (TextUtils.isEmpty(name)) {
            MiscUtil.toast("姓名不能为空");
            return;
        }
        ApiExecutor.exec(new AddStudentNameRequest(this, name));
    }

    private static final class AddStudentNameRequest extends BaseApiContext<AddStudentNameActivity, AddStudentName> {

        private String name;

        public AddStudentNameRequest(AddStudentNameActivity addStudentNameActivity, String name) {
            super(addStudentNameActivity);
            this.name = name;
        }

        @Override
        public AddStudentName request() throws Exception {
            return new AddStudentNameApi().get(this.name);
        }

        @Override
        public void onApiSuccess(@NonNull AddStudentName data) {
            get().onAddStudentNameSuccess(data);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().onAddStudentNameFailed();
        }
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
