package com.malalaoshi.android.report.page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.malalaoshi.android.core.utils.ViewUtils;
import com.malalaoshi.android.report.R;

/**
 * 报告封面
 * Created by tianwei on 5/21/16.
 */
public class ReportHomePage extends LinearLayout {

    public ReportHomePage(Context context) {
        super(context);
    }

    public ReportHomePage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static ReportHomePage newInstance(ViewGroup parent) {
        return (ReportHomePage) ViewUtils.newInstance(parent, R.layout.report__page_home);
    }

    public static ReportHomePage newInstance(Context context) {
        return (ReportHomePage) ViewUtils.newInstance(context, R.layout.report__page_home);
    }

}
