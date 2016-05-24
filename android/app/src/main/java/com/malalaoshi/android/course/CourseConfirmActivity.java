package com.malalaoshi.android.course;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.util.FragmentUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Course confirm activity
 * Created by tianwei on 3/5/16.
 */
public class CourseConfirmActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {
    public static final String EXTRA_SCHOOLS = "extra_schools";
    public static final String EXTRA_PRICES = "extra_prices";
    public static final String EXTRA_TEACHER_ID = "extra_teacher_id";
    public static final String EXTRA_SUBJECT = "extra_subject";
    public static final String EXTRA_TEACHER_AVATOR = "extra_teacher_avator";
    public static final String EXTRA_TEACHER_NAME = "extra_teacher_name";
    private static final String TAG = "CourseConfirmActivity";
    @Bind(R.id.title_view)
    protected TitleBarView titleBarView;

    private CourseConfirmFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_confirm);
        ButterKnife.bind(this);
        Log.i(TAG, "");
        Object[] schools = getIntent().getParcelableArrayExtra(EXTRA_SCHOOLS);
        Object[] prices = getIntent().getParcelableArrayExtra(EXTRA_PRICES);
        Object teacherId = getIntent().getLongExtra(EXTRA_TEACHER_ID, 0);
        Object subject = getIntent().getStringExtra(EXTRA_SUBJECT);
        String teacherAvator = getIntent().getStringExtra(EXTRA_TEACHER_AVATOR);
        String teacherName = getIntent().getStringExtra(EXTRA_TEACHER_NAME);
        fragment = CourseConfirmFragment.newInstance(schools, prices, teacherId, subject,teacherAvator,teacherName);
        FragmentUtil.openFragment(R.id.container, getSupportFragmentManager(), null
                , fragment, "couponfragment");
        titleBarView.setOnTitleBarClickListener(this);
    }

    @Override
    public void onTitleLeftClick() {
        this.finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected String getStatName() {
        return "课程确认";
    }
}
