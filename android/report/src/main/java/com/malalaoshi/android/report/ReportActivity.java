package com.malalaoshi.android.report;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.malalaoshi.android.core.base.BaseTitleActivity;

/**
 * 学生报告样本
 * Created by tianwei on 5/21/16.
 */
public class ReportActivity extends BaseTitleActivity {

    /**
     * 如果subject_id=-1。打开数学模板报告
     */
    public static final String EXTRA_SUBJECT = "subject_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTitleBar().setRightVisibility(View.GONE);
        replaceFragment(Fragment.instantiate(this, ReportFragment.class.getName(), getIntent().getExtras()));
    }

    @Override
    protected String getStatName() {
        return "学生报告样本";
    }

    public static void launch(Activity activity, long subject) {
        Intent intent = new Intent(activity, ReportActivity.class);
        intent.putExtra(EXTRA_SUBJECT, subject);
        activity.startActivity(intent);
    }
}
