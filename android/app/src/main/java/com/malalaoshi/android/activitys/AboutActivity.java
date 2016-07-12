package com.malalaoshi.android.activitys;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.view.TitleBarView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/3/10.
 */
public class AboutActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {
    @Bind(R.id.titleBar)
    TitleBarView titleBar;

    @Bind(R.id.tv_version_name)
    TextView tvVersionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        titleBar.setOnTitleBarClickListener(this);
        PackageManager pm = getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(getPackageName(),0);
            tvVersionName.setText("版本 V"+pi.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTitleLeftClick() {
        finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    @Override
    protected String getStatName() {
        return "关于";
    }
}
