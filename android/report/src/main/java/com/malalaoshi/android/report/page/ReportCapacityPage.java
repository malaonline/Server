package com.malalaoshi.android.report.page;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.ViewUtils;
import com.malalaoshi.android.report.R;
import com.malalaoshi.android.report.entity.AbilityStructure;
import com.malalaoshi.android.report.entity.AxisModel;
import com.malalaoshi.android.report.view.SpiderNetView;

import java.util.ArrayList;
import java.util.List;

/**
 * 能力数据分析页
 * Created by tianwei on 5/22/16.
 */
public class ReportCapacityPage extends LinearLayout {

    private List<AbilityStructure> data;

    public ReportCapacityPage(Context context) {
        super(context);
    }

    public ReportCapacityPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static ReportCapacityPage newInstance(ViewGroup parent) {
        return (ReportCapacityPage) ViewUtils.newInstance(parent, R.layout.report__page_capacity);
    }

    public static ReportCapacityPage newInstance(Context context, List<AbilityStructure> abilities) {
        ReportCapacityPage page = (ReportCapacityPage) ViewUtils.newInstance(context, R.layout.report__page_capacity);
        page.setData(abilities);
        return page;
    }

    private void initView() {
        SpiderNetView waveView = (SpiderNetView) findViewById(R.id.view_chart);
        List<AxisModel> list = new ArrayList<>();
        if (EmptyUtils.isEmpty(data)) {
            return;
        }
        for (AbilityStructure item : data) {
            list.add(new AxisModel((int) item.getVal(), item.getKey()));
        }
        waveView.setList(list);
    }

    public void setData(List<AbilityStructure> data) {
        this.data = data;
        initView();
    }
}
