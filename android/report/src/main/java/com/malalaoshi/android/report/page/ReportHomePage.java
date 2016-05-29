package com.malalaoshi.android.report.page;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.malalaoshi.android.report.R;

/**
 * 报告封面
 * Created by tianwei on 5/21/16.
 */
public class ReportHomePage extends LinearLayout {

    public ReportHomePage(Context context) {
        super(context);
        initView();
    }

    public ReportHomePage(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        setBackgroundColor(Color.WHITE);
        LayoutInflater.from(getContext()).inflate(R.layout.report__page_home, this, true);
    }
}
