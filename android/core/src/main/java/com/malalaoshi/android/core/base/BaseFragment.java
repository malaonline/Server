package com.malalaoshi.android.core.base;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.malalaoshi.android.core.stat.StatReporter;

/**
 * Base fragment
 * Created by tianwei on 3/5/16.
 */
public abstract class BaseFragment extends Fragment {
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

    public abstract String getStatName();

    protected ProgressDialog progressDialog;
    private boolean isShowProcessDialog = false;
    private String processMessage = "正在加载数据···";
    private boolean isResume = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    @Override
    public void onResume() {
        super.onResume();
        StatReporter.onResume(getStatName());
        if (isShowProcessDialog&&progressDialog!=null&&!progressDialog.isShowing()){
            progressDialog.setMessage(processMessage);
            progressDialog.show();
        }
        isResume = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        StatReporter.onPause();
        if (isShowProcessDialog&&progressDialog!=null&&progressDialog.isShowing()){
            progressDialog.hide();
        }
        isResume = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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
            progressDialog = new ProgressDialog(getContext());
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

}
