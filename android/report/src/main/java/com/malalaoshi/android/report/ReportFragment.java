package com.malalaoshi.android.report;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.report.adapter.ReportAdapter;
import com.malalaoshi.android.report.page.ReportCapacityPage;
import com.malalaoshi.android.report.page.ReportHomePage;
import com.malalaoshi.android.report.page.ReportKnowledgePage;
import com.malalaoshi.android.report.page.ReportScorePage;
import com.malalaoshi.android.report.page.ReportSubjectPage;
import com.malalaoshi.android.report.page.ReportWorkPage;

import java.util.ArrayList;
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
    //封面
    private ReportHomePage homePage;

    private List<View> pageList;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report__fragmet_folder, container, false);
        dotView = view.findViewById(R.id.dot_view);
        dotViewContainer = view.findViewById(R.id.dot_view_container);
        homePage = (ReportHomePage) view.findViewById(R.id.view_home_page);
        homePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homePage.setVisibility(View.GONE);
                dotViewContainer.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
            }
        });
        initViewPager(view);
        return view;
    }

    private void initViewPager(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        ReportAdapter adapter = new ReportAdapter(getActivity());
        viewPager.setAdapter(adapter);
        pageList = new ArrayList<>();
        pageList.add(ReportWorkPage.newInstance(getActivity()));
        pageList.add(ReportSubjectPage.newInstance(getActivity()));
        pageList.add(ReportKnowledgePage.newInstance(getActivity()));
        pageList.add(ReportCapacityPage.newInstance(getActivity()));
        pageList.add(ReportScorePage.newInstance(getActivity()));
        adapter.setList(pageList);
        adapter.notifyDataSetChanged();
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
    }

    @Override
    public String getStatName() {
        return "学生报告";
    }

    private void updatePageIndicator(int position) {
        int num = pageList.size() - 1;
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
}
