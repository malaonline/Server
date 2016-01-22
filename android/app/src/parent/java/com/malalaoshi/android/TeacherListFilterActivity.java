package com.malalaoshi.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


import com.malalaoshi.android.dialog.FilterDialog;
import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.entity.Tag;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.fragments.FilterGradeFragment;
import com.malalaoshi.android.fragments.FilterSubjectFragment;
import com.malalaoshi.android.fragments.FilterTagFragment;
import com.malalaoshi.android.fragments.TeacherListFragment;
import com.malalaoshi.android.util.FragmentUtil;
import com.malalaoshi.android.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zl on 15/12/17.
 */
public class TeacherListFilterActivity  extends AppCompatActivity{

    public static final String EXTRA_GRADE = "grade";
    public static final String EXTRA_SUBJECT = "subject";
    public static final String EXTRA_TAGS = "tags";


    private List<Teacher> teachersList = new ArrayList<Teacher>();

    @Bind(R.id.tv_title)
    protected TextView tvTitle;

    @Bind(R.id.tv_filter_grade)
    protected TextView tvFilterGrade;

    @Bind(R.id.tv_filter_subject)
    protected TextView tvFilterSubject;

    @Bind(R.id.tv_filter_tag)
    protected TextView tvFilterTag;

    //筛选结果列表
    private TeacherListFragment filterFragment;

    //筛选条件
    private Grade grade;
    private Subject subject;
    private List<Tag> tags;

    public static void open(Context context, Grade grade, Subject subject, ArrayList<Tag> tags) {
        Intent intent = new Intent(context, TeacherListFilterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_GRADE, grade);
        intent.putExtra(EXTRA_SUBJECT, subject);
        intent.putParcelableArrayListExtra(EXTRA_TAGS, tags);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_list_filter);
        ButterKnife.bind(this);

        initDatas();
        initViews();
        setEvent();
    }


    private void setEvent() {
    }

    private void initViews() {
        updateCtrls();
        filterFragment = new TeacherListFragment().setTeacherList(teachersList);
        FragmentUtil.openFragment(R.id.teacher_list_fragment, getFragmentManager(), null, filterFragment, TeacherListFragment.class.getName());
    }

    private void initDatas() {
        tvTitle.setText("筛选结果");
        Intent intent = getIntent();
        grade = intent.getParcelableExtra(EXTRA_GRADE);
        subject = intent.getParcelableExtra(EXTRA_SUBJECT);
        tags = intent.getParcelableArrayListExtra(EXTRA_TAGS);
    }

    private void updateCtrls() {
        if (grade!=null&&grade.getName()!=null){
            tvFilterGrade.setText(grade.getName());
        }
        else{
            tvFilterGrade.setText("不限");
        }
        if (subject!=null&&subject.getName()!=null){
            tvFilterSubject.setText(subject.getName());
        }else{
            tvFilterSubject.setText("不限");
        }
        if (tags!=null&&tags.size()>0){
            String str = StringUtil.joinEntityName(tags,"·");
            tvFilterTag.setText(str);
        }else{
            tvFilterTag.setText("不限");
        }
    }

    @Override
    public void onBackPressed(){
        if(!getFragmentManager().popBackStackImmediate()){
            ActivityCompat.finishAfterTransition(this);
            this.finish();
        }
    }

    @OnClick(R.id.tv_filter_grade)
    public void onClickGradeFilter(View v){//int width, int height, List<Fragment> fragments, int pageIndex,FragmentManager fragmentManager
        List<Fragment> fragments = new ArrayList<>();
        FilterGradeFragment gradeFragment = new FilterGradeFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(FilterGradeFragment.ARGMENTS_GRADE_ID, grade.getId());
        gradeFragment.setArguments(bundle);
        fragments.add(gradeFragment);
        //fragments.add(new FilterSubjectFragment());
        int width = getResources().getDimensionPixelSize(R.dimen.filter_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.filter_dialog_height);
        final FilterDialog filterdialog = new FilterDialog();
        Bundle filterBundle = new Bundle();
        filterBundle.putInt(FilterDialog.ARGMENTS_DIALOG_WIDTH, width);
        filterBundle.putInt(FilterDialog.ARGMENTS_DIALOG_HEIGHT, height);
        filterBundle.putInt(FilterDialog.ARGMENTS_DIALOG_PAGEINDEX, 0);
        filterdialog.setArguments(filterBundle);
        filterdialog.setFragments(fragments);
        filterdialog.setRightBtnVisable(View.GONE);
        filterdialog.setLeftBtnVisable(View.GONE);
        filterdialog.setTileIconImageDrawable(getResources().getDrawable(R.drawable.ic_grade_dialog));
        filterdialog.setTitleText("筛选年级");
        gradeFragment.setOnGradeClickListener(new FilterGradeFragment.OnGradeClickListener() {
            @Override
            public void onGradeClick(Grade grade) {
                TeacherListFilterActivity.this.grade = grade;
                filterdialog.dismiss();
                updateCtrls();
                //筛选结果
                //filterFragment
            }
        });
        filterdialog.show(getSupportFragmentManager(),"dialog");

    }

    @OnClick(R.id.tv_filter_subject)
    public void onClickSubjectFilter(View v){
        List<Fragment> fragments = new ArrayList<>();
        FilterSubjectFragment subjectFragment = new FilterSubjectFragment();
        Bundle bundle = new Bundle();
        bundle.putLong(FilterSubjectFragment.ARGMENTS_GRADE_ID, grade.getId());
        bundle.putLong(FilterSubjectFragment.ARGMENTS_SUBJECT_ID, subject.getId());
        subjectFragment.setArguments(bundle);
        fragments.add(subjectFragment);
        int width = getResources().getDimensionPixelSize(R.dimen.filter_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.filter_dialog_height);
        final FilterDialog filterdialog = new FilterDialog();
        Bundle filterBundle = new Bundle();
        filterBundle.putInt(FilterDialog.ARGMENTS_DIALOG_WIDTH, width);
        filterBundle.putInt(FilterDialog.ARGMENTS_DIALOG_HEIGHT, height);
        filterBundle.putInt(FilterDialog.ARGMENTS_DIALOG_PAGEINDEX, 0);
        filterdialog.setArguments(filterBundle);
        filterdialog.setFragments(fragments);
        filterdialog.setRightBtnVisable(View.GONE);
        filterdialog.setLeftBtnVisable(View.GONE);
        filterdialog.setTileIconImageDrawable(getResources().getDrawable(R.drawable.ic_subject_dialog));
        filterdialog.setTitleText("筛选课程");
        subjectFragment.setOnSubjectClickListener(new FilterSubjectFragment.OnSubjectClickListener() {
            @Override
            public void onSubjectClick(Subject subject) {
                TeacherListFilterActivity.this.subject = subject;
                filterdialog.dismiss();
                updateCtrls();
                //筛选结果
                //filterFragment
            }
        });
        filterdialog.show(getSupportFragmentManager(), "dialog");

    }

    @OnClick(R.id.tv_filter_tag)
    public void onClickTagFilter(View v){
        List<Fragment> fragments = new ArrayList<>();
        long[] tagsId = new long[tags==null?0:tags.size()];
        for (int i=0;tags!=null&&i<tags.size();i++){
            tagsId[i] = tags.get(i).getId();
        }

        FilterTagFragment tagFragment = new FilterTagFragment();
        Bundle bundle = new Bundle();
        bundle.putLongArray(FilterTagFragment.ARGMENTS_TAGS_ID, tagsId);
        tagFragment.setArguments(bundle);

        tagFragment.setOnTagClickListener(new FilterTagFragment.OnTagClickListener() {
            @Override
            public void onTagClick(ArrayList<Tag> tags) {
                TeacherListFilterActivity.this.tags = tags;
            }
        });
        fragments.add(tagFragment);
        int width = getResources().getDimensionPixelSize(R.dimen.filter_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.filter_dialog_height);
        final FilterDialog filterdialog = new FilterDialog();
        Bundle filterBundle = new Bundle();
        filterBundle.putInt(FilterDialog.ARGMENTS_DIALOG_WIDTH, width);
        filterBundle.putInt(FilterDialog.ARGMENTS_DIALOG_HEIGHT, height);
        filterBundle.putInt(FilterDialog.ARGMENTS_DIALOG_PAGEINDEX, 0);
        filterdialog.setArguments(filterBundle);
        filterdialog.setFragments(fragments);
        filterdialog.setRightBtnVisable(View.VISIBLE);
        filterdialog.setLeftBtnVisable(View.GONE);
        filterdialog.setTileIconImageDrawable(getResources().getDrawable(R.drawable.ic_tag_dialog));
        filterdialog.setTitleText("筛选标签");
        filterdialog.setOnRightClickListener(new FilterDialog.OnRightClickListener() {
            @Override
            public void OnRightClick(View v) {
                //开始筛选
                filterdialog.dismiss();
                updateCtrls();
                //筛选结果
                //filterFragment
            }
        });
        filterdialog.show(getSupportFragmentManager(),"dialog");

    }

    @OnClick(R.id.iv_titlebar_left)
    public void onClickLeft(View v){
        this.finish();
    }
}