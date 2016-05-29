package com.malalaoshi.android.report.page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.malalaoshi.android.core.utils.ViewUtils;
import com.malalaoshi.android.report.R;
import com.malalaoshi.android.report.entity.AxisModel;
import com.malalaoshi.android.report.view.LineView;

import java.util.ArrayList;
import java.util.List;

/**
 * 提分数据分析页
 * Created by tianwei on 5/22/16.
 */
public class ReportScorePage extends LinearLayout {

    public ReportScorePage(Context context) {
        super(context);
    }

    public ReportScorePage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static ReportScorePage newInstance(ViewGroup parent) {
        return (ReportScorePage) ViewUtils.newInstance(parent, R.layout.report__page_score);
    }

    public static ReportScorePage newInstance(Context context) {
        return (ReportScorePage) ViewUtils.newInstance(context, R.layout.report__page_score);
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
        LineView lineView = (LineView) findViewById(R.id.view_line);
        List<AxisModel> list = new ArrayList<>();
        AxisModel model = new AxisModel(60, 55, "实数");
        list.add(model);
        model = new AxisModel(90, 60, "函数初步");
        list.add(model);
        model = new AxisModel(80, 70, "多边形");
        list.add(model);
        model = new AxisModel(85, 60, "圆");
        list.add(model);
        model = new AxisModel(78, 65, "全等");
        list.add(model);
        model = new AxisModel(95, 80, "相似");
        list.add(model);
        model = new AxisModel(100, 80, "几何变换");
        list.add(model);
        lineView.setList(list);
        lineView.setMax(100);
    }
}
