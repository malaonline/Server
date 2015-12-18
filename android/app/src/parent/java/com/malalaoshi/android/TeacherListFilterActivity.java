package com.malalaoshi.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;


import com.malalaoshi.android.fragments.TeacherListFragment;
import com.malalaoshi.android.listener.NavigationFinishClickListener;
import com.malalaoshi.android.util.FragmentUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zl on 15/12/17.
 */
public class TeacherListFilterActivity  extends AppCompatActivity{
    @Bind(R.id.teacher_list_filter_toolbar)
    protected Toolbar toolbar;

    public static void open(Context context, String teacherId) {
        Intent intent = new Intent(context, TeacherListFilterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_list_filter);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new NavigationFinishClickListener(this));

        FragmentUtil.opFragmentMainActivity(getFragmentManager(), null, new TeacherListFragment(), TeacherListFragment.class.getName());
    }

    @Override
    public void onBackPressed(){
        if(!getFragmentManager().popBackStackImmediate()){
            ActivityCompat.finishAfterTransition(this);
            this.finish();
        }
    }
}