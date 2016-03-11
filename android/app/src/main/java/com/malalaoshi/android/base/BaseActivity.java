package com.malalaoshi.android.base;

import android.support.v7.app.AppCompatActivity;

import com.malalaoshi.android.stat.StatManager;

import cn.jpush.android.api.JPushInterface;

/**
 * Add stat tag
 * Created by zl on 15/11/30.
 */
public class BaseActivity extends AppCompatActivity {
    public static String TAG = BaseActivity.class.getName();
    @Override
    protected void onResume() {
        StatManager.getInstance().onResume(this);
        JPushInterface.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        StatManager.getInstance().onPause(this);
        JPushInterface.onPause(this);
        super.onPause();
    }
}

