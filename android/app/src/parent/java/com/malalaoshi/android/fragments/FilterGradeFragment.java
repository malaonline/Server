package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.FilterAdapter;
import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.view.ExpandedHeightGridView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by kang on 16/1/21.
 */
public class FilterGradeFragment extends Fragment {
    //参数
    public static String ARGMENTS_GRADE_ID = "grade id";
    private Long extraGradeId;

    private List<Map<String, Object>> mPrimaryGrages = new ArrayList<>();   // 小学adapter data
    private List<Map<String, Object>> mMiddleGrages = new ArrayList<>();    // 初中adapter data
    private List<Map<String, Object>> mSeniorGrages = new ArrayList<>();    // 高中adapter data

    @Bind(R.id.filter_grages_list1)
    protected ExpandedHeightGridView mGridViewPrimary;

    @Bind(R.id.filter_grages_list2)
    protected ExpandedHeightGridView mGridViewMiddle;

    @Bind(R.id.filter_grages_list3)
    protected ExpandedHeightGridView mGridViewSenior;

    private FilterAdapter mPrimaryFilterAdapter;
    private FilterAdapter mMiddleFilterAdapter;
    private FilterAdapter mSeniorFilterAdapter;

    private Map<String, Object> selectedObj;

    private OnGradeClickListener gradeClickListener;

    public FilterGradeFragment(){

    }

    public void setOnGradeClickListener(OnGradeClickListener gradeClickListener){
        this.gradeClickListener = gradeClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grade_filter, container, false);
        ButterKnife.bind(this, view);
        extraGradeId = getArguments().getLong(ARGMENTS_GRADE_ID);
        initDatas();
        initViews();
        return view;
    }

    private void initViews() {
        mPrimaryFilterAdapter = new FilterAdapter(getActivity(), mPrimaryGrages, R.layout.filter_grade_item);
        mGridViewPrimary.setAdapter(mPrimaryFilterAdapter);
        mMiddleFilterAdapter = new FilterAdapter(getActivity(),mMiddleGrages, R.layout.filter_grade_item);
        mGridViewMiddle.setAdapter(mMiddleFilterAdapter);
        mSeniorFilterAdapter = new FilterAdapter(getActivity(), mSeniorGrages, R.layout.filter_grade_item);
        mGridViewSenior.setAdapter(mSeniorFilterAdapter);
    }

    private void initDatas() {
        loadDatas();
    }

    private void loadDatas() {

        // 小学
        Grade primary = Grade.getGradeById(Grade.PRIMARY_ID);
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("id", primary.getId());
        item.put("name", primary.getName());
        if (extraGradeId==primary.getId()){
            item.put("selected",true);
            selectedObj = item;
        }else{
            item.put("selected", false);
        }
        mPrimaryGrages.add(item);
        // 初中
        Grade middle = Grade.getGradeById(Grade.MIDDLE_ID);
        item = new HashMap<String, Object>();
        item.put("id", middle.getId());
        item.put("name", middle.getName());
        if (extraGradeId==middle.getId()){
            item.put("selected",true);
            selectedObj = item;
        }else{
            item.put("selected",false);
        }
        mMiddleGrages.add(item);
        // 高中
        Grade senior = Grade.getGradeById(Grade.SENIOR_ID);
        item = new HashMap<String, Object>();
        item.put("id", senior.getId());
        item.put("name", senior.getName());
        if (extraGradeId==senior.getId()){
            selectedObj = item;
            item.put("selected",true);
        }else{
            item.put("selected",false);
        }
        mSeniorGrages.add(item);

        // collect all grade
        for (Grade g: Grade.gradeList) {
            if (g.getSupersetId() == null) {
                continue;
            }
            if (g.getSupersetId() == Grade.PRIMARY_ID) {
                item = new HashMap<String, Object>();
                item.put("id", g.getId());
                item.put("name", primary.getName() + g.getName());
                if (extraGradeId==g.getId()){
                    selectedObj = item;
                    item.put("selected",true);
                }else{
                    item.put("selected",false);
                }
                mPrimaryGrages.add(item);
            }
            if (g.getSupersetId() == Grade.MIDDLE_ID) {
                item = new HashMap<String, Object>();
                item.put("id", g.getId());
                item.put("name", middle.getName() + g.getName());
                if (extraGradeId==g.getId()){
                    selectedObj = item;
                    item.put("selected",true);
                }else{
                    item.put("selected",false);
                }
                mMiddleGrages.add(item);
            }
            if (g.getSupersetId() == Grade.SENIOR_ID) {
                item = new HashMap<String, Object>();
                item.put("id", g.getId());
                item.put("name", senior.getName() + g.getName());
                if (extraGradeId==g.getId()){
                    selectedObj = item;
                    item.put("selected",true);
                }else{
                    item.put("selected",false);
                }
                mSeniorGrages.add(item);
            }
        }
    }


    @OnItemClick(R.id.filter_grages_list1)
    public void onPrimaryItemClick(AdapterView<?> parent, View view, int position, long id) {
        //parent.get
        dealItemClick(parent, 1, view, position);
    }

    @OnItemClick(R.id.filter_grages_list2)
    public void onMideleItemClick(AdapterView<?> parent, View view, int position, long id) {
        dealItemClick(parent, 2,view,position);
    }

    @OnItemClick(R.id.filter_grages_list3)
    public void onSeniorItemClick(AdapterView<?> parent, View view, int position, long id) {
        dealItemClick(parent, 3,view,position);
    }

    private void dealItemClick(AdapterView<?> parent,int type, View view, int position) {
        Map<String, Object> obj = null;
        switch (type){
            case 1:
                obj = mPrimaryGrages.get(position);
                break;
            case 2:
                obj = mMiddleGrages.get(position);
                break;
            case 3:
                obj = mSeniorGrages.get(position);
                break;
        }
        if (selectedObj != obj){
            selectedObj.put("selected",false);
            selectedObj = obj;
            obj.put("selected", true);
            mPrimaryFilterAdapter.notifyDataSetChanged();
            mMiddleFilterAdapter.notifyDataSetChanged();
            mSeniorFilterAdapter.notifyDataSetChanged();
        }
        if (gradeClickListener!=null){
            Grade grade = new Grade();
            grade.setId((Long) obj.get("id"));
            grade.setName((String) obj.get("name"));
            gradeClickListener.onGradeClick(grade);
        }
    }

    public interface OnGradeClickListener{
        void onGradeClick(Grade grade);
    }
}
