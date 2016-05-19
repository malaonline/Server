package com.malalaoshi.android.activitys;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.malalaoshi.android.MainActivity;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.fragments.MemberBookFragment;
import com.malalaoshi.android.fragments.MemberCounselingFragment;
import com.malalaoshi.android.fragments.MemberExpectFragment;
import com.malalaoshi.android.fragments.MemberLectureFragment;
import com.malalaoshi.android.fragments.MemberReportFragment;
import com.malalaoshi.android.fragments.MemberSelfstudyFragment;
import com.malalaoshi.android.fragments.MemberSppsFragment;
import com.malalaoshi.android.fragments.MemberUniqueFragment;
import com.malalaoshi.android.view.Indicator.RubberIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemberActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {

    private List<Fragment> fragments;

    private MyPagerAdapter adapter;


    //TODO  页面当前位置
    private int currentPagerposition = 0;


    @Bind(R.id.vp_member)
    protected ViewPager memberViewPager;
    @Bind(R.id.ri_member_rubber)
    protected RubberIndicator riMember;
    @Bind(R.id.tbv_member_title)
    protected TitleBarView titleBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);
        ButterKnife.bind(this);
        //获得点击的哪个页面
        getPagerPosition();
        titleBarView.setOnTitleBarClickListener(this);
        riMember.setCount(8, currentPagerposition);
        setAdapter();
        memberViewPager.setCurrentItem(currentPagerposition);
        setListener();
    }


    private void setListener() {

        memberViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        moveRubberIndicator(0);
                        break;
                    case 1:
                        moveRubberIndicator(1);
                        break;
                    case 2:
                        moveRubberIndicator(2);
                        break;
                    case 3:
                        moveRubberIndicator(3);
                        break;
                    case 4:
                        moveRubberIndicator(4);
                        break;
                    case 5:
                        moveRubberIndicator(5);
                        break;
                    case 6:
                        moveRubberIndicator(6);
                        break;
                    case 7:
                        moveRubberIndicator(7);
                        break;
                }


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void moveRubberIndicator(int i) {
        if (currentPagerposition < i) {
            riMember.moveToRight();
        } else if (currentPagerposition > i) {
            riMember.moveToLeft();
        }
        currentPagerposition = i;

    }


    private void setAdapter() {
        fragments = new ArrayList<>();
        fragments.add(new MemberSelfstudyFragment());
        fragments.add(new MemberReportFragment());
        fragments.add(new MemberCounselingFragment());
        fragments.add(new MemberUniqueFragment());
        fragments.add(new MemberLectureFragment());
        fragments.add(new MemberBookFragment());
        fragments.add(new MemberSppsFragment());
        fragments.add(new MemberExpectFragment());
        FragmentManager fm = getSupportFragmentManager();
        adapter = new MyPagerAdapter(fm);
        memberViewPager.setAdapter(adapter);
    }


    @Override
    protected String getStatName() {
        return "会员特权展示";
    }

    //TitleBar监听器
    @Override
    public void onTitleLeftClick() {
        finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    //TODO
    public void getPagerPosition() {
        Intent intent = getIntent();
        //  int position = intent.getIntExtra("PagerPosition",0);
        int position = 3;
        currentPagerposition = position;
    }


    class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return fragments.get(arg0);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

    }
}
