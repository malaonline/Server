package com.malalaoshi.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malalaoshi.android.R;
import com.malalaoshi.android.TeacherFilterActivity;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.dialog.MultiSelectFilterDialog;
import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.entity.Tag;
import com.malalaoshi.android.util.FragmentUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainFragment extends BaseFragment implements MultiSelectFilterDialog.OnRightClickListener {

    public static MainFragment newInstance() {
        MainFragment f = new MainFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_list, container, false);
        ButterKnife.bind(this, view);
        initViews(savedInstanceState);
        initData();
        return view;
    }

    private void initData() {

    }

    private void initViews(Bundle savedInstanceState) {
        if (savedInstanceState==null){
            TeacherListFragment fragment = TeacherListFragment.newInstance();
            FragmentUtil.openFragment(R.id.id_content, getChildFragmentManager(), null, fragment, TeacherListFragment.class.getName());
        }
    }

    //筛选
    @OnClick(R.id.teacher_filter_btn)
    public void onClickTeacherFilter() {
        StatReporter.ClickTeacherFilter();
        MultiSelectFilterDialog newFragment = MultiSelectFilterDialog.newInstance();
        newFragment.setOnRightClickListener(this);
        newFragment.show(getFragmentManager(), MultiSelectFilterDialog.class.getName());
    }

    @Override
    public String getStatName() {
        return "老师列表页";
    }

    @Override
    public void OnRightClick(View v, Grade grade, Subject subject, ArrayList<Tag> tags) {
        TeacherFilterActivity.open(getContext(), grade, subject, tags);
    }

}