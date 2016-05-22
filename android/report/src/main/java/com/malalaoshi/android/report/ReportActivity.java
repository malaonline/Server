package com.malalaoshi.android.report;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.malalaoshi.android.core.base.BaseTitleActivity;

/**
 * 学生报告样本
 * Created by tianwei on 5/21/16.
 */
public class ReportActivity extends BaseTitleActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTitleBar().setRightVisibility(View.GONE);
        replaceFragment(new ReportFragment());
    }

    @Override
    protected String getStatName() {
        return "学生报告样本";
    }
}
