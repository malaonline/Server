package com.malalaoshi.android.core.base;

import android.support.v7.app.AppCompatActivity;

import com.malalaoshi.android.core.stat.StatReporter;

import cn.jpush.android.api.JPushInterface;

/**
 * Add stat tag
 * Created by zl on 15/11/30.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static String TAG = BaseActivity.class.getName();

    @Override
    protected void onResume() {
        StatReporter.onResume(getStatName());
        JPushInterface.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        StatReporter.onPause();
        JPushInterface.onPause(this);
        super.onPause();
    }

    protected abstract String getStatName();
}

