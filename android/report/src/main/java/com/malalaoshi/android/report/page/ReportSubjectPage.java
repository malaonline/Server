package com.malalaoshi.android.report.page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.malalaoshi.android.core.utils.ViewUtils;
import com.malalaoshi.android.report.R;
import com.malalaoshi.android.report.entity.AxisModel;
import com.malalaoshi.android.report.view.WaveView;

import java.util.ArrayList;
import java.util.List;

/**
 * 题目数据分析页
 * Created by tianwei on 5/22/16.
 */
public class ReportSubjectPage extends LinearLayout {

    public ReportSubjectPage(Context context) {
        super(context);
    }

    public ReportSubjectPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static ReportSubjectPage newInstance(ViewGroup parent) {
        return (ReportSubjectPage) ViewUtils.newInstance(parent, R.layout.report__page_subject);
    }

    public static ReportSubjectPage newInstance(Context context) {
        return (ReportSubjectPage) ViewUtils.newInstance(context, R.layout.report__page_subject);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (isInEditMode()) {
            return;
        }

        initView();
    }

    private void initView() {
        WaveView waveView = (WaveView) findViewById(R.id.view_wave);
        List<AxisModel> list = new ArrayList<>();
        AxisModel model = new AxisModel(15, 30, "3月上");
        list.add(model);
        model = new AxisModel(45, 90, "3月下");
        list.add(model);
        model = new AxisModel(15, 30, "4月上");
        list.add(model);
        model = new AxisModel(20, 40, "4月下");
        list.add(model);
        model = new AxisModel(30, 60, "5月上");
        list.add(model);
        model = new AxisModel(15, 30, "5月下");
        list.add(model);
        model = new AxisModel(15, 30, "6月上");
        list.add(model);
        waveView.setList(list);
        waveView.setMax(100);
    }
}
