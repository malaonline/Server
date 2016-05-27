package com.malalaoshi.android.core.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.utils.DialogUtils;

import cn.jpush.android.api.JPushInterface;

/**
 * Add stat tag
 * Created by zl on 15/11/30.
 */
public abstract class BaseActivity extends AppCompatActivity {
    public static String TAG = BaseActivity.class.getName();

    protected ProgressDialog progressDialog;
    private boolean isShowProcessDialog = false;
    private String processMessage = "正在加载数据···";
    private boolean isResume = false;

    @Override
    protected void onResume() {
        StatReporter.onResume(getStatName());
        JPushInterface.onResume(this);
        if (isShowProcessDialog&&progressDialog!=null&&!progressDialog.isShowing()){
            progressDialog.setMessage(processMessage);
            progressDialog.show();
        }
        isResume = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        StatReporter.onPause();
        JPushInterface.onPause(this);
        if (isShowProcessDialog&&progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.hide();
        }
        isResume = false;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyProcessDialog();

    }

    private void destroyProcessDialog() {
        if (progressDialog!=null){
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    protected void startProcessDialog(String processMessage) {
        isShowProcessDialog = true;
        this.processMessage = processMessage;
        if (progressDialog==null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);   // 设置进度条的形式为圆形转动的进度条
            progressDialog.setCancelable(true);                              // 设置是否可以通过点击Back键取消
            progressDialog.setCanceledOnTouchOutside(false);
        }
        if (isResume&&!progressDialog.isShowing()){
            progressDialog.setMessage(processMessage);
            progressDialog.show();
        }
    }

    protected void stopProcessDialog() {
        isShowProcessDialog = false;
        if (progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.hide();
        }
    }

    protected abstract String getStatName();
}

