package com.malalaoshi.android.report.page;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.malalaoshi.android.core.utils.ViewUtils;
import com.malalaoshi.android.report.R;
import com.malalaoshi.android.report.entity.AxisModel;
import com.malalaoshi.android.report.view.HorizontalLineView;

import java.util.ArrayList;
import java.util.List;

/**
 * 作业数据分析
 * Created by tianwei on 5/21/16.
 */
public class ReportKnowledgePage extends LinearLayout {

    private static final List<Integer> COLOR_LIST;
    private static final List<String> CONTENT_LIST;

    static {
        COLOR_LIST = new ArrayList<>();
        COLOR_LIST.add(Color.parseColor("#F8DB6B"));
        COLOR_LIST.add(Color.parseColor("#6DC9CE"));
        COLOR_LIST.add(Color.parseColor("#F9877C"));
        COLOR_LIST.add(Color.parseColor("#75CC97"));
        COLOR_LIST.add(Color.parseColor("#88BCDE"));
        COLOR_LIST.add(Color.parseColor("#8BA3CA"));
        COLOR_LIST.add(Color.parseColor("#F7AF63"));
        COLOR_LIST.add(Color.parseColor("#BA9CDA"));
        COLOR_LIST.add(Color.parseColor("#C09C8B"));

        CONTENT_LIST = new ArrayList<>();
        CONTENT_LIST.add("实数");
        CONTENT_LIST.add("函数初步");
        CONTENT_LIST.add("多边形");
        CONTENT_LIST.add("相似");
        CONTENT_LIST.add("全等");
        CONTENT_LIST.add("微积分");
        CONTENT_LIST.add("几何变形");
        CONTENT_LIST.add("圆");
        CONTENT_LIST.add("其他");
    }

    public ReportKnowledgePage(Context context) {
        super(context);
    }

    public ReportKnowledgePage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static ReportKnowledgePage newInstance(ViewGroup parent) {
        return (ReportKnowledgePage) ViewUtils.newInstance(parent, R.layout.report__page_knowledge);
    }

    public static ReportKnowledgePage newInstance(Context context) {
        return (ReportKnowledgePage) ViewUtils.newInstance(context, R.layout.report__page_knowledge);
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
        HorizontalLineView lineView = (HorizontalLineView) findViewById(R.id.view_chart);
        List<AxisModel> list = new ArrayList<>();
        AxisModel model = new AxisModel(10, 30, "实数");
        list.add(model);
        model = new AxisModel(20, 40, "函数初步");
        list.add(model);
        model = new AxisModel(30, 60, "多边形");
        list.add(model);
        model = new AxisModel(25, 60, "圆");
        list.add(model);
        model = new AxisModel(18, 65, "全等");
        list.add(model);
        model = new AxisModel(45, 80, "相似");
        list.add(model);
        model = new AxisModel(59, 80, "几何变形");
        list.add(model);
        lineView.setList(list);
    }

}
