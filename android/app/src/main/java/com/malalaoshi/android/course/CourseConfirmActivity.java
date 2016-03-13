package com.malalaoshi.android.course;

import android.os.Bundle;
import android.util.Log;

import com.malalaoshi.android.R;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.util.FragmentUtil;
import com.malalaoshi.android.view.TitleBarView;

import java.util.Objects;

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
    private static final String TAG = "CourseConfirmActivity";
    @Bind(R.id.title_view)
    protected TitleBarView titleBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_confirm);
        ButterKnife.bind(this);
        Log.i(TAG, "");
        Object[] schools = getIntent().getParcelableArrayExtra(EXTRA_SCHOOLS);
        Object[] prices = getIntent().getParcelableArrayExtra(EXTRA_PRICES);
        Object teacherId = getIntent().getLongExtra(EXTRA_TEACHER_ID, 0);
        CourseConfirmFragment fragment = CourseConfirmFragment.newInstance(schools, prices, teacherId);
        FragmentUtil.openFragment(R.id.container, getSupportFragmentManager(), null
                , fragment, "couponfragment");
        titleBarView.setOnTitleBarClickListener(this);
    }

    @Override
    public void onTitleLeftClick() {

    }

    @Override
    public void onTitleRightClick() {

    }
}
