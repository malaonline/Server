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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 能力数据分析页
 * Created by tianwei on 5/22/16.
 */
public class ReportCapacityPage extends LinearLayout {

    private static final Map<String, String> NAME;

    static {
        NAME = new HashMap<>();
        NAME.put("spatial", "空间想象");
        NAME.put("abstract", "抽象概括");
        NAME.put("calc", "运算求解");
        NAME.put("appl", "实际应用");
        NAME.put("data", "数据分析");
        NAME.put("reason", "推理论证");
    }

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
            String name = NAME.get(item.getKey());
            if (name == null) {
                name = item.getKey();
            }
            list.add(new AxisModel((int) item.getVal(), name));
        }
        waveView.setList(list);
    }

    public void setData(List<AbilityStructure> data) {
        this.data = data;
        initView();
    }
}
