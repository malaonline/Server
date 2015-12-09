package com.malalaoshi.android;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.malalaoshi.android.base.StatusBarActivity;

import butterknife.Bind;
import butterknife.ButterKnife;

import com.malalaoshi.android.listener.NavigationFinishClickListener;
import com.malalaoshi.android.util.ThemeUtils;

/**
 * Created by zl on 15/11/30.
 */
public class TeacherDetailActivity extends StatusBarActivity{
    private static final String EXTRA_TEACHER_ID = "teacherId";

    @Bind(R.id.parent_teacher_detail_toolbar)
    protected Toolbar toolbar;

    public static void open(Context context, String teacherId) {
        Intent intent = new Intent(context, TeacherDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(EXTRA_TEACHER_ID, teacherId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ThemeUtils.setMargins(toolbar, 0, ThemeUtils.getStatusBarHeight(this), 0, 0);
        }else{
            ThemeUtils.setMargins(toolbar, 0, ThemeUtils.getStatusBarHeight(this)/2, 0, 5);
        }

        toolbar.setNavigationOnClickListener(new NavigationFinishClickListener(this));
    }

}
