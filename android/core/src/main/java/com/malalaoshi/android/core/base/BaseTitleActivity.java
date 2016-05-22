package com.malalaoshi.android.core.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.malalaoshi.android.core.R;
import com.malalaoshi.android.core.view.TitleBarView;

/**
 * Add stat tag
 * Created by zl on 15/11/30.
 */
public abstract class BaseTitleActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {

    public static String TAG = BaseTitleActivity.class.getName();

    private TitleBarView titleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initTitleBar();
    }

    private void initTitleBar() {
        titleView = (TitleBarView) findViewById(R.id.title_view);
        if (titleView != null) {
            titleView.setOnTitleBarClickListener(this);
            titleView.setTitle(getStatName());
        }
    }

    protected TitleBarView getTitleBar() {
        return titleView;
    }

    protected int getLayoutId() {
        return R.layout.core__activity_base_title;
    }

    public void setTitle(String title) {
        if (titleView != null) {
            titleView.setTitle(title);
        }
    }

    protected void replaceFragment(Fragment newFragment) {
        replaceFragment(newFragment, null, false);
    }

    protected void replaceFragment(Fragment newFragment, Bundle arguments, boolean isAddStack) {
        if (isFinishing()) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (arguments != null) {
            newFragment.setArguments(arguments);
        }
        transaction.replace(R.id.core__fragment_container, newFragment);
        if (isAddStack) {
            transaction.addToBackStack(null);
        }
        transaction.commitAllowingStateLoss();
    }


    @Override
    public void onTitleLeftClick() {
        finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    @Override
    public void setTitle(int rid) {
        if (titleView != null) {
            titleView.setTitle(rid);
        }
    }

}

