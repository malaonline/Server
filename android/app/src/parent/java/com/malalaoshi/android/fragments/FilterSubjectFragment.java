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
import com.malalaoshi.android.dialog.MultiSelectFilterDialog;
import com.malalaoshi.android.entity.Subject;
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
public class FilterSubjectFragment extends Fragment {
    //参数
    public static String ARGMENTS_GRADE_ID = "grade id";
    private Long extraGradeId;
    //参数
    public static String ARGMENTS_SUBJECT_ID = "subject id";
    private Long extraSubjectId;

    private List<Map<String, Object>> mSubjects = new ArrayList<>();

    @Bind(R.id.filter_subjects_list)
    protected ExpandedHeightGridView mGridViewSubject;
    private FilterAdapter nSubjectFilterAdapter;

    private Map<String, Object> selectedObj;

    private OnSubjectClickListener subjectClickListener;

    public FilterSubjectFragment(){
    }

    public static FilterSubjectFragment newInstance() {
        FilterSubjectFragment filterSubjectFragment = new FilterSubjectFragment();
        return filterSubjectFragment;
    }

    public static FilterSubjectFragment newInstance(Long gradeId,Long subjectId) {
        if (gradeId==null||subjectId==null){
            return null;
        }
        FilterSubjectFragment filterSubjectFragment = new FilterSubjectFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(ARGMENTS_GRADE_ID,gradeId);
        bundle.putLong(ARGMENTS_SUBJECT_ID,subjectId);
        filterSubjectFragment.setArguments(bundle);
        return filterSubjectFragment;
    }

    public void setOnSubjectClickListener(OnSubjectClickListener subjectClickListener){
        this.subjectClickListener = subjectClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_subject_filter, container, false);
        ButterKnife.bind(this, view);
        Bundle bundle = getArguments();
        if (bundle!=null){
            extraGradeId = getArguments().getLong(ARGMENTS_GRADE_ID);
            extraSubjectId = getArguments().getLong(ARGMENTS_SUBJECT_ID);
        }
        initDatas();
        initViews();
        return view;
    }

    private void initViews() {
        nSubjectFilterAdapter = new FilterAdapter(getActivity(),
                mSubjects, R.layout.filter_grade_item);
        mGridViewSubject.setAdapter(nSubjectFilterAdapter);
    }

    private void initDatas() {
        loadDatas();
    }

    private void loadDatas() {
        mSubjects.clear();
        long[] subjects1 = new long[]{1,2,3};
        long[] subjects2 = new long[]{1,2,3,4,5,6,7,8,9};
        long[] grade1 = new long[]{1,2,3,4,5,6,7};
        boolean b = false;
        for (int i=0;extraGradeId!=null&&i<grade1.length;i++){
            if (extraGradeId==grade1[i]){
                b = true;
                break;
            }
        }
        long[] currentGrade = null;
        if (b){
            currentGrade = subjects1;
        }else{
            currentGrade = subjects2;
        }

        for (int i = 0; i < Subject.subjectList.size(); i++) {
            Subject subject = Subject.subjectList.get(i);
            for (int j=0;j<currentGrade.length;j++){
                if (currentGrade[j]==subject.getId()){
                        Map<String, Object> item = new HashMap<String, Object>();
                        item.put("id", subject.getId());
                        item.put("name", subject.getName());
                        if (extraSubjectId!=null&&extraSubjectId==subject.getId()){
                            item.put("selected",true);
                            selectedObj = item;
                        }else{
                            item.put("selected",false);
                        }
                        mSubjects.add(item);
                    break;
                }

            }
        }
    }

    @OnItemClick(R.id.filter_subjects_list)
    public void onSubjectItemClick(AdapterView<?> parent, View view, int position, long id) {
        Map<String, Object> obj = null;
        obj = mSubjects.get(position);
        if (selectedObj!=null&&selectedObj != obj){
            selectedObj.put("selected",false);
            selectedObj = obj;
            obj.put("selected",true);
            nSubjectFilterAdapter.notifyDataSetChanged();
        }
        if (subjectClickListener!=null){
            Subject subject = new Subject();
            subject.setId((Long) obj.get("id"));
            subject.setName((String) obj.get("name"));
            subjectClickListener.onSubjectClick(subject);
        }
    }




    public interface OnSubjectClickListener{
        void onSubjectClick(Subject subject);
    }
}
