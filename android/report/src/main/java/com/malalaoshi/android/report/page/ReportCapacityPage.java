package com.malalaoshi.android.report.page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.malalaoshi.android.core.utils.ViewUtils;
import com.malalaoshi.android.report.R;
import com.malalaoshi.android.report.entity.AxisModel;
import com.malalaoshi.android.report.view.SpiderNetView;

import java.util.ArrayList;
import java.util.List;

/**
 * 能力数据分析页
 * Created by tianwei on 5/22/16.
 */
public class ReportCapacityPage extends LinearLayout {

    public ReportCapacityPage(Context context) {
        super(context);
    }

    public ReportCapacityPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static ReportCapacityPage newInstance(ViewGroup parent) {
        return (ReportCapacityPage) ViewUtils.newInstance(parent, R.layout.report__page_capacity);
    }

    public static ReportCapacityPage newInstance(Context context) {
        return (ReportCapacityPage) ViewUtils.newInstance(context, R.layout.report__page_capacity);
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
        SpiderNetView waveView = (SpiderNetView) findViewById(R.id.view_chart);
        List<AxisModel> list = new ArrayList<>();
        AxisModel model =
                new AxisModel(70, "推理论证");
        list.add(model);
        model = new AxisModel(40, "数据分析");
        list.add(model);
        model = new AxisModel(26, "空间想象");
        list.add(model);
        model = new AxisModel(80, "运算求解");
        list.add(model);
        model = new AxisModel(50, "实际应用");
        list.add(model);
        waveView.setList(list);
    }
}
