package com.malalaoshi.android.base;

import android.support.v7.app.AppCompatActivity;

import com.malalaoshi.android.stat.StatManager;

/**
 * Add stat tag
 * Created by zl on 15/11/30.
 */
public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onResume() {
        StatManager.getInstance().onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        StatManager.getInstance().onPause(this);
        super.onPause();
    }
}
