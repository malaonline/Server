package com.malalaoshi.android.report.page;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.ViewUtils;
import com.malalaoshi.android.report.R;
import com.malalaoshi.android.report.adapter.WorkColorAdapter;
import com.malalaoshi.android.report.entity.ExerciseErrorDistribution;
import com.malalaoshi.android.report.entity.PieModel;
import com.malalaoshi.android.report.entity.SubjectReport;
import com.malalaoshi.android.report.entity.WorkColorModel;
import com.malalaoshi.android.report.view.PieView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 作业数据分析
 * Created by tianwei on 5/21/16.
 */
public class ReportWorkPage extends LinearLayout {

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
        CONTENT_LIST.add("几何变换");
        CONTENT_LIST.add("圆");
        CONTENT_LIST.add("其他");
    }

    private List<ExerciseErrorDistribution> data;
    private int answerTotalCount;
    private int workTotalCount;

    public ReportWorkPage(Context context) {
        super(context);
    }

    public ReportWorkPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static ReportWorkPage newInstance(ViewGroup parent) {
        return (ReportWorkPage) ViewUtils.newInstance(parent, R.layout.report__page_work);
    }

    public static ReportWorkPage newInstance(Context context, SubjectReport report) {
        ReportWorkPage page = (ReportWorkPage) ViewUtils.newInstance(context, R.layout.report__page_work);
        page.setData(report.getError_rates());
        page.answerTotalCount = report.getExercise_total_nums();
        page.workTotalCount = report.getTotal_nums();
        page.initData();
        return page;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        initGridView();
    }

    private void initGridView() {
        GridView gridView = (GridView) findViewById(R.id.grid_view);
        WorkColorAdapter adapter = new WorkColorAdapter(getContext());
        for (int i = 0; i < COLOR_LIST.size(); i++) {
            WorkColorModel model = new WorkColorModel();
            model.setColor(COLOR_LIST.get(i));
            model.setContent(CONTENT_LIST.get(i));
            adapter.getList().add(model);
        }
        gridView.setAdapter(adapter);
    }

    private void initData() {
        PieView pieView = (PieView) findViewById(R.id.pie_view);
        pieView.setCenterText("错题分布");
        if (EmptyUtils.isEmpty(data)) {
            return;
        }
        List<PieModel> list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            int color = COLOR_LIST.get(i % COLOR_LIST.size());
            list.add(new PieModel(color, (int) data.get(i).getRate()));
        }
        PieModel.calNumByNumber(list);
        pieView.setData(list);
        String answer = String.format(Locale.getDefault(), "累计答题%d道", answerTotalCount);
        ((TextView) findViewById(R.id.tv_answer)).setText(answer);
        String work = String.format(Locale.getDefault(), "作业%d次", workTotalCount);
        ((TextView) findViewById(R.id.tv_work)).setText(work);
    }

    public void setData(List<ExerciseErrorDistribution> data) {
        this.data = data;
    }
}
