package com.malalaoshi.android.usercenter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


/**
 * Verification the phone and student's name
 * Created by tianwei on 12/26/15.
 */
public class SmsAuthActivity extends Activity implements ViewController {

    public static int RESULT_CODE_LOGIN_SUCCESSED = 200;
    private List<View> views;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = new ArrayList<>();
        onChangeView(null, true, VerificationView.class);
    }

    @Override
    public void onBackPressed() {
        if (!back()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onBack() {
        back();
    }

    private boolean back() {
        if (views.size() > 0) {
            View view = views.remove(views.size() - 1);
            if (view != null && view.getParent() == null) {
                setContentView(view);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onFinish() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        if (views != null) {
            views.clear();
            views = null;
        }
        super.onDestroy();
    }

    @Override
    public void onChangeView(View currentView, boolean pullToStack, Class<? extends View> clazz) {
        if (pullToStack && currentView != null) {
            views.add(currentView);
        }
        View view = null;
        if (clazz == UserProtocolView.class) {
            view = new UserProtocolView(this, null);
        } else if (clazz == VerificationView.class) {
            view = new VerificationView(this, null);
        } else if (clazz == VerifyStudentNameView.class) {
            view = new VerifyStudentNameView(this, null);
        }
        if (view != null) {
            setContentView(view);
        }
    }

    public void setActivityResult(Intent intent){
        setResult(RESULT_CODE_LOGIN_SUCCESSED,intent);
    }
}
