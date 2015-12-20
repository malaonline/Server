package com.malalaoshi.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.fragments.TeacherListFragment;
import com.malalaoshi.android.listener.NavigationFinishClickListener;
import com.malalaoshi.android.util.FragmentUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zl on 15/12/17.
 */
public class TeacherListFilterActivity  extends AppCompatActivity{
    private List<Teacher> teachersList = new ArrayList<Teacher>();

    @Bind(R.id.teacher_list_filter_toolbar)
    protected Toolbar toolbar;

    private Long gradeId = null;
    private Long subjectId = null;
    private Long [] tagIds = null;

    public static void open(Context context, Long gradeId, Long subjectId, long [] tagIds) {
        Intent intent = new Intent(context, TeacherListFilterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("gradeId", gradeId);
        intent.putExtra("subjectId", subjectId);
        intent.putExtra("tagIds", tagIds);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_list_filter);
        ButterKnife.bind(this);

        getExtraValues();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new NavigationFinishClickListener(this));
        Subject sub = Subject.getSubjectFromListById(subjectId, Subject.subjectList);
        Grade grade = Grade.getById(gradeId);
        String title = "";
        title += grade != null ? grade.getName() : "";
        title += sub != null ? sub.getName() : "";
        toolbar.setTitle(title);

        FragmentUtil.opFragmentMainActivity(getFragmentManager(), null, new TeacherListFragment().setTeacherList(teachersList).setSearchCondition(gradeId, subjectId, tagIds), TeacherListFragment.class.getName());
    }

    @Override
    public void onBackPressed(){
        if(!getFragmentManager().popBackStackImmediate()){
            ActivityCompat.finishAfterTransition(this);
            this.finish();
        }
    }

    private void getExtraValues(){
        Intent intent = getIntent();
        gradeId = intent.getLongExtra("gradeId", -1);
        subjectId = intent.getLongExtra("subjectId", -1);
        long [] tagIdsTmp = intent.getLongArrayExtra("tagIds");
        if(tagIdsTmp != null){
            tagIds = new Long[tagIdsTmp.length];
            for(int i=0; i<tagIdsTmp.length; i++){
                tagIds[i] = tagIdsTmp[i];
            }
        }
    }
}