package com.malalaoshi.android.report.page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.malalaoshi.android.core.MalaContext;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.ViewUtils;
import com.malalaoshi.android.report.R;
import com.malalaoshi.android.report.entity.AxisModel;
import com.malalaoshi.android.report.entity.ScoreAnalyses;
import com.malalaoshi.android.report.view.LineView;

import java.util.ArrayList;
import java.util.List;

/**
 * 提分数据分析页
 * Created by tianwei on 5/22/16.
 */
public class ReportScorePage extends RelativeLayout {

    private List<ScoreAnalyses> data;
    private LineView lineView;

    public ReportScorePage(Context context) {
        super(context);
    }

    public ReportScorePage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static ReportScorePage newInstance(ViewGroup parent) {
        return (ReportScorePage) ViewUtils.newInstance(parent, R.layout.report__page_score);
    }

    public static ReportScorePage newInstance(Context context, List<ScoreAnalyses> score_analyses) {
        ReportScorePage page = (ReportScorePage) ViewUtils.newInstance(context, R.layout.report__page_score);
        page.setData(score_analyses);
        return page;
    }

    private void initView() {
        lineView = (LineView) findViewById(R.id.view_line);
        List<AxisModel> list = new ArrayList<>();
        if (EmptyUtils.isEmpty(data)) {
            return;
        }
        for (ScoreAnalyses item : data) {
            list.add(new AxisModel(Math.round(item.getMy_score() * 100), Math.round(item.getAve_score() * 100),
                    item.getName()));
        }
        lineView.setList(list);
        lineView.setMax(100);
        initTest();
    }

    /**
     * Just for test
     */
    private void initTest() {
        if (!MalaContext.isDebug()) {
            return;
        }
        findViewById(R.id.ll_test).setVisibility(VISIBLE);
        findViewById(R.id.btn_down).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lineView.updateTestData(false);
            }
        });
        findViewById(R.id.btn_up).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lineView.updateTestData(true);
            }
        });
    }

    public void setData(List<ScoreAnalyses> data) {
        this.data = data;
        initView();
    }
}
