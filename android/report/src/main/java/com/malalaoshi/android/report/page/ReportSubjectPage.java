package com.malalaoshi.android.report.page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.ViewUtils;
import com.malalaoshi.android.report.R;
import com.malalaoshi.android.report.entity.AxisModel;
import com.malalaoshi.android.report.entity.ExerciseMonthTrend;
import com.malalaoshi.android.report.view.WaveView;

import java.util.ArrayList;
import java.util.List;

/**
 * 题目数据分析页
 * Created by tianwei on 5/22/16.
 */
public class ReportSubjectPage extends LinearLayout {

    private List<ExerciseMonthTrend> data;

    public ReportSubjectPage(Context context) {
        super(context);
    }

    public ReportSubjectPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static ReportSubjectPage newInstance(ViewGroup parent) {
        return (ReportSubjectPage) ViewUtils.newInstance(parent, R.layout.report__page_subject);
    }

    public static ReportSubjectPage newInstance(Context context, List<ExerciseMonthTrend> month_trend) {
        ReportSubjectPage page = (ReportSubjectPage) ViewUtils.newInstance(context, R.layout.report__page_subject);
        page.setData(month_trend);
        return page;
    }

    private void initView() {
        WaveView waveView = (WaveView) findViewById(R.id.view_wave);
        List<AxisModel> list = new ArrayList<>();
        if (EmptyUtils.isEmpty(data)) {
            return;
        }
        int max = 0;
        for (ExerciseMonthTrend item : data) {
            list.add(new AxisModel(item.getError_item(), item.getTotal_item(), item.getMonthPart()));
            if (max < item.getTotal_item()) {
                max = item.getTotal_item();
            }
        }
        waveView.setList(list);
        waveView.setMax(max);
    }

    public void setData(List<ExerciseMonthTrend> data) {
        this.data = data;
        initView();
    }
}
