package com.malalaoshi.android.report;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report__fragmet_folder, container, false);
        initViewPager(view);
        return view;
    }

    private void initViewPager(View view) {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        ReportAdapter adapter = new ReportAdapter(getActivity());
        viewPager.setAdapter(adapter);
        List<View> pageList = new ArrayList<>();
        pageList.add(ReportCapacityPage.newInstance(getActivity()));
        pageList.add(ReportKnowledgePage.newInstance(getActivity()));
        pageList.add(ReportScorePage.newInstance(getActivity()));
        pageList.add(ReportSubjectPage.newInstance(getActivity()));
        pageList.add(ReportHomePage.newInstance(getActivity()));
        pageList.add(ReportWorkPage.newInstance(getActivity()));
        adapter.setList(pageList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public String getStatName() {
        return "学生报告";
    }
}
