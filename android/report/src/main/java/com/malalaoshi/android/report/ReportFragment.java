package com.malalaoshi.android.report;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.base.BaseTitleActivity;
import com.malalaoshi.android.core.contants.Subject;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.utils.DateUtils;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.GradeUtils;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.report.adapter.ReportAdapter;
import com.malalaoshi.android.report.api.SubjectReportApi;
import com.malalaoshi.android.report.entity.ExerciseMonthTrend;
import com.malalaoshi.android.report.entity.SubjectReport;
import com.malalaoshi.android.report.page.ReportCapacityPage;
import com.malalaoshi.android.report.page.ReportHomePage;
import com.malalaoshi.android.report.page.ReportKnowledgePage;
import com.malalaoshi.android.report.page.ReportScorePage;
import com.malalaoshi.android.report.page.ReportSubjectPage;
import com.malalaoshi.android.report.page.ReportWorkPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 学生报告
 * Created by tianwei on 5/21/16.
 */
public class ReportFragment extends BaseFragment {

    //页面上的点
    private View dotView;
    //页面上的点的容器
    private View dotViewContainer;
    //科目
    private long subjectId;
    private ReportHomePage homePage;
    private TextView statusButton;
    private ImageView statusImageView;
    private View statusLayout;

    private List<View> pageList;
    private ViewPager viewPager;

    private AnimationDrawable animationDrawable;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report__fragmet_folder, container, false);
        dotView = view.findViewById(R.id.dot_view);
        dotViewContainer = view.findViewById(R.id.dot_view_container);
        initIntent();
        initStatusView(view);
        initViewPager(view);
        requestData();
        return view;
    }

    private void initIntent() {
        Bundle bundle = getArguments();
        if (bundle == null) {
            return;
        }
        subjectId = bundle.getLong(ReportActivity.EXTRA_SUBJECT, Integer.MIN_VALUE);
        if (subjectId == Integer.MIN_VALUE) {
            MiscUtil.toast("哎呀，这个报告找不到了。");
            getActivity().finish();
        }
    }

    private void initStatusView(View view) {
        animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.core__refresh_refreshing);
        statusButton = (TextView) view.findViewById(R.id.tv_status);
        statusLayout = view.findViewById(R.id.ll_status);
        statusImageView = (ImageView) view.findViewById(R.id.iv_status);
        statusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beginLoadData();
            }
        });
    }

    private void beginLoadData() {
        ApiExecutor.exec(new FetchSubjectReport(ReportFragment.this, subjectId));
        updateUI(0);
    }

    private void initViewPager(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        pageList = new ArrayList<>();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updatePageIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (getActivity() instanceof BaseTitleActivity) {
            String title = UserManager.getInstance().getStuName() + "的";
            title += Subject.getSubjectName(subjectId) + "报告";
            ((BaseTitleActivity) getActivity()).setTitle(title);
        }
    }

    private void fillPages(SubjectReport response) {
        Collections.sort(response.getMonth_trend());
        String reportTime = getReportDatetime(response.getMonth_trend());
        homePage = ReportHomePage.newInstance(getActivity());
        homePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(1);
            }
        });
        pageList.add(homePage);
        ReportWorkPage workPage = ReportWorkPage.newInstance(getActivity(), response);
        pageList.add(workPage);
        pageList.add(ReportSubjectPage.newInstance(getActivity(), response.getMonth_trend()));
        pageList.add(ReportKnowledgePage.newInstance(getActivity(), response.getKnowledges_accuracy()));
        pageList.add(ReportCapacityPage.newInstance(getActivity(), response.getAbilities()));
        pageList.add(ReportScorePage.newInstance(getActivity(), response.getScore_analyses()));
        ReportAdapter adapter = new ReportAdapter(getActivity());
        adapter.setList(pageList);
        viewPager.setAdapter(adapter);
        homePage.setStudent(UserManager.getInstance().getStuName());
        homePage.setGrade(GradeUtils.getGradeName(response.getGrade_id()));
        String title = "麻辣老师学生的" + Subject.getSubjectName(subjectId) + "报告";
        homePage.setSubject(title);
        homePage.setReportTime(reportTime);
    }

    private String getReportDatetime(List<ExerciseMonthTrend> month_trend) {
        if (EmptyUtils.isEmpty(month_trend)) {
            return "我的报告";
        }
        for (ExerciseMonthTrend data : month_trend) {
            data.setTime(new Date(data.getYear() - 1900, data.getMonth() - 1, data.getDay()).getTime());
        }
        return DateUtils.formatFull(month_trend.get(0).getTime()) + "~" + DateUtils
                .formatFull(month_trend.get(month_trend.size() - 1).getTime());
    }

    private void requestData() {
        if (subjectId == -1) {
            //数学样本
            fillPages(MathReportTemplate.getMathTemplate());
            homePage.setStudent(MathReportTemplate.getStudent());
            homePage.setGrade(MathReportTemplate.getGrade());
            return;
        }
        beginLoadData();
    }


    @Override
    public String getStatName() {
        return "学生报告";
    }

    private void updatePageIndicator(int position) {
        if (position == 0) {
            dotViewContainer.setVisibility(View.INVISIBLE);
            return;
        } else {
            dotViewContainer.setVisibility(View.VISIBLE);
        }
        position--;//去掉首页
        int num = pageList.size() - 2;
        num = num <= 1 ? 1 : num;
        int stepWidth = (dotViewContainer.getWidth() - dotView.getWidth()) / num;
        FrameLayout.LayoutParams params;
        if (dotView.getLayoutParams() == null) {
            params = new FrameLayout.LayoutParams(dotView.getWidth(), dotView.getHeight());
        } else {
            params = (FrameLayout.LayoutParams) dotView.getLayoutParams();
        }
        params.setMargins(stepWidth * position, 0, 0, 0);
        dotView.setLayoutParams(params);
    }

    private void updateUI(int status) {
        switch (status) {
            case 0: //loading
                statusLayout.setVisibility(View.VISIBLE);
                statusImageView.setImageDrawable(animationDrawable);
                animationDrawable.start();
                statusButton.setText("正在获取学习报告数据");
                statusButton.setBackground(null);
                break;
            case 1: //finished
                statusLayout.setVisibility(View.GONE);
                animationDrawable.stop();
                statusButton.setText("学习报告加载失败，点击刷新");
                statusButton.setBackgroundResource(R.drawable.report__bk_round_gray);
                break;
            case -1: //failed
                statusLayout.setVisibility(View.VISIBLE);
                animationDrawable.stop();
                animationDrawable.selectDrawable(3);
                statusButton.setText("学习报告加载失败，点击刷新");
                statusButton.setBackgroundResource(R.drawable.report__bk_round_gray);
                break;
        }
    }

    private static final class FetchSubjectReport extends BaseApiContext<ReportFragment, SubjectReport> {

        private long subjectId;

        public FetchSubjectReport(ReportFragment reportFragment, long subjectId) {
            super(reportFragment);
            this.subjectId = subjectId;
        }

        @Override
        public SubjectReport request() throws Exception {
            return new SubjectReportApi().get(subjectId);
        }

        @Override
        public void onApiSuccess(@NonNull SubjectReport response) {
            get().updateUI(1);
            get().fillPages(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("当前无法访问题目数据，请稍后再试");
            get().updateUI(-1);
        }
    }
}
