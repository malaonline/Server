package com.malalaoshi.android.activitys;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.fragments.MemberRightFragment;
import com.malalaoshi.android.view.Indicator.RubberIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MemberActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {

    public static String EXTRA_CURRETN_POSITION = "current pager";
    private List<Fragment> fragments;

    private MyPagerAdapter adapter;


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
        fragments.add(MemberRightFragment.newInstance(R.drawable.ic_member_selfstudy, "自习陪读", "享受专业老师免费陪读服务，随时解决学习问题"));
        fragments.add(MemberRightFragment.newInstance(R.drawable.ic_member_report, "学习报告", "全面记录学生学习数据，方便家长，随时查看，充分了解学员知识点掌握情况"));
        fragments.add(MemberRightFragment.newInstance(R.drawable.ic_member_counseling, "心理辅导", "免费获得专业心理咨询师1对1心理辅导，促进学员身心健康成长"));
        fragments.add(MemberRightFragment.newInstance(R.drawable.ic_member_unique, "特色讲座", "特邀各领域专家进行多种特色讲座，营养健康、家庭教育、高效学习应有尽有"));
        fragments.add(MemberRightFragment.newInstance(R.drawable.ic_member_lecture, "考前串讲", "专业解读考试趋势，剖析考试难点分享高分经验。还有命题专家进行中高考押题"));
        fragments.add(MemberRightFragment.newInstance(R.drawable.ic_member_book, "错题本", "针对每个学员记录并生成错题本，方便查找知识漏洞，并生成针对性练习"));
        fragments.add(MemberRightFragment.newInstance(R.drawable.ic_member_spps, "SPPS测评", "定期进行SPPS评测，充分了解学员学习情况"));
        fragments.add(MemberRightFragment.newInstance(R.drawable.ic_member_expect, "敬请期待...", ""));
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

    public void getPagerPosition() {
        Intent intent = getIntent();
        currentPagerposition = intent.getIntExtra(EXTRA_CURRETN_POSITION,0);
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
