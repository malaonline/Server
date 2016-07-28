package com.malalaoshi.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.dialog.RadioFilterDialog;
import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.entity.Tag;
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
public class TeacherFilterActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener,
        FilterSubjectFragment.OnSubjectClickListener
        ,FilterGradeFragment.OnGradeClickListener
        ,FilterTagFragment.OnTagClickListener{

    public static final String EXTRA_GRADE = "grade";
    public static final String EXTRA_SUBJECT = "subject";
    public static final String EXTRA_TAGS = "tags";

    @Bind(R.id.title_view)
    protected TitleBarView titleBarView;

    @Bind(R.id.tv_filter_grade)
    protected TextView tvFilterGrade;

    @Bind(R.id.tv_filter_subject)
    protected TextView tvFilterSubject;

    @Bind(R.id.tv_filter_tag)
    protected TextView tvFilterTag;

    //筛选条件
    private Grade grade;
    private Subject subject;
    private ArrayList<Tag> tags;

    private DialogFragment dialogFragment;

    public static void open(Context context, Grade grade, Subject subject, ArrayList<Tag> tags) {
        if (grade==null||subject==null){
            return;
        }
        if (tags==null){
            tags = new ArrayList<>();
        }

        Intent intent = new Intent(context, TeacherFilterActivity.class);
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
        initViews(savedInstanceState);
        setEvent();
    }


    private void setEvent() {
        titleBarView.setOnTitleBarClickListener(this);
    }

    private void initDatas() {
        Intent intent = getIntent();
        grade = intent.getParcelableExtra(EXTRA_GRADE);
        subject = intent.getParcelableExtra(EXTRA_SUBJECT);
        tags = intent.getParcelableArrayListExtra(EXTRA_TAGS);
    }

    private void initViews(Bundle savedInstanceState) {
        Long gradeId = updateFilterCondition(grade);
        Long subjectId = updateFilterCondition(subject);
        long[] tagIds = updateFilterCondition(tags);
        if (savedInstanceState==null){
            TeacherListFragment filterFragment = TeacherListFragment.newInstance(gradeId, subjectId, tagIds);
            FragmentUtil.openFragment(R.id.id_content, getSupportFragmentManager(), null, filterFragment, TeacherListFragment.class.getName());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        TeacherListFragment teacherListFragment = (TeacherListFragment) getSupportFragmentManager().findFragmentByTag(TeacherListFragment.class.getName());
        if (teacherListFragment!=null){
            teacherListFragment.setEmptyViewText("请重新设定筛选条件!");
        }
    }

    @OnClick(R.id.tv_filter_grade)
    public void onClickGradeFilter(View v){
        closeFilterDialog();
        dialogFragment = RadioFilterDialog.newInstance(grade, this);
        dialogFragment.show(getSupportFragmentManager(), RadioFilterDialog.class.getName());
        StatReporter.switchFilterGrade();
    }

    @OnClick(R.id.tv_filter_subject)
    public void onClickSubjectFilter(View v){
        closeFilterDialog();
        dialogFragment = RadioFilterDialog.newInstance(grade, subject,this);
        dialogFragment.show(getSupportFragmentManager(), RadioFilterDialog.class.getName());

        StatReporter.switchFilterSubject();
    }

    @OnClick(R.id.tv_filter_tag)
    public void onClickTagFilter(View v){
        closeFilterDialog();
        dialogFragment = RadioFilterDialog.newInstance(tags, this);
        ((RadioFilterDialog)dialogFragment).setOnRightClickListener(new RadioFilterDialog.OnRightClickListener() {
            @Override
            public void OnRightClick(View v) {
                TeacherListFragment filterFragment = (TeacherListFragment) TeacherFilterActivity.this.getSupportFragmentManager().findFragmentByTag(TeacherListFragment.class.getName());
                long[] tagIds = updateFilterCondition(tags);
                filterFragment.setTagIds(tagIds);
                filterFragment.refresh();
                closeFilterDialog();
            }
        });
        dialogFragment.show(getSupportFragmentManager(), RadioFilterDialog.class.getName());
        StatReporter.switchFilterFeature();
    }

    private Long updateFilterCondition(Subject subject) {
        Long subjectId = null;
        if (subject!=null&&subject.getName()!=null){
            tvFilterSubject.setText(subject.getName());
            subjectId = subject.getId();
        }else{
            tvFilterSubject.setText("不限");
        }
        return subjectId;
    }

    private Long updateFilterCondition(Grade grade) {
        Long gradeId = null;
        if (grade!=null&&grade.getName()!=null){
            tvFilterGrade.setText(grade.getName());
            gradeId = grade.getId();
        }
        else{
            tvFilterGrade.setText("不限");
        }
        return gradeId;
    }

    private long[] updateFilterCondition(List<Tag> tags) {
        long[] tagIds = null;
        if (tags!=null&&tags.size()>0){
            String str = StringUtil.joinEntityName(tags,"·");
            tvFilterTag.setText(str);
            tagIds = new long[tags.size()];
            for (int i=0;i<tags.size();i++){
                tagIds[i] = tags.get(i).getId();
            }
        }else{
            tvFilterTag.setText("不限");
        }
        return tagIds;
    }

    @Override
    public void onTitleLeftClick() {
        this.finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    @Override
    protected String getStatName() {
        return "老师过滤";
    }

    @Override
    public void onSubjectClick(Subject subject) {
        TeacherListFragment filterFragment = (TeacherListFragment) TeacherFilterActivity.this.getSupportFragmentManager().findFragmentByTag(TeacherListFragment.class.getName());
        this.subject = subject;
        Long subjectId = updateFilterCondition(subject);
        filterFragment.setSubjectId(subjectId);
        filterFragment.refresh();
        closeFilterDialog();
    }

    @Override
    public void onGradeClick(Grade grade) {
        TeacherListFragment filterFragment = (TeacherListFragment) TeacherFilterActivity.this.getSupportFragmentManager().findFragmentByTag(TeacherListFragment.class.getName());
        this.grade = grade;
        Long gradeId = updateFilterCondition(grade);
        Log.e("TeacherFilterActivity","TeacherFilterActivity:"+gradeId+" "+this.grade+" "+filterFragment);
        filterFragment.setGradeId(gradeId);
        filterFragment.refresh();
        closeFilterDialog();
    }


    private void closeFilterDialog() {
        if (dialogFragment!=null){
            dialogFragment.dismiss();
            dialogFragment = null;
        }
    }

    @Override
    public void onTagClick(ArrayList<Tag> tags) {
        //开始筛选
        this.tags = tags;
    }
}