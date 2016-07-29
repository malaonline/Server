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
import com.malalaoshi.android.report.entity.KnowledgePointAccuracy;
import com.malalaoshi.android.report.view.HorizontalLineView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 作业数据分析
 * Created by tianwei on 5/21/16.
 */
public class ReportKnowledgePage extends RelativeLayout {

    private List<KnowledgePointAccuracy> data;
    private HorizontalLineView lineView;

    public ReportKnowledgePage(Context context) {
        super(context);
    }

    public ReportKnowledgePage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static ReportKnowledgePage newInstance(ViewGroup parent) {
        return (ReportKnowledgePage) ViewUtils.newInstance(parent, R.layout.report__page_knowledge);
    }

    public static ReportKnowledgePage newInstance(Context context, List<KnowledgePointAccuracy> know) {
        ReportKnowledgePage page = (ReportKnowledgePage) ViewUtils
                .newInstance(context, R.layout.report__page_knowledge);
        page.setData(know);
        return page;
    }

    private void initView() {
        final int threshold = 9;
        lineView = (HorizontalLineView) findViewById(R.id.view_chart);
        if (EmptyUtils.isEmpty(data)) {
            return;
        }
        boolean needMerge = data.size() > threshold;
        int size = needMerge ? threshold : data.size();
        List<AxisModel> list = new ArrayList<>(size);
        Collections.sort(data);
        int total = 0;
        int correct = 0;
        for (KnowledgePointAccuracy item : data) {
            if (list.size() == threshold - 1 && data.size() != threshold) {
                total += item.getTotal_item();
                correct += item.getRight_item();
            } else {
                list.add(new AxisModel(item.getRight_item(), item.getTotal_item(), item.getName()));
            }
        }
        Collections.shuffle(list);
        if (total != 0) {
            list.add(new AxisModel(correct, total, "其它"));
        }
        lineView.setList(list);
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

    public void setData(List<KnowledgePointAccuracy> data) {
        this.data = data;
        initView();
    }
}
