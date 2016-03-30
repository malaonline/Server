package com.malalaoshi.android;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.malalaoshi.android.adapter.FragmentGroupAdapter;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.fragments.SimpleAlertDialogFragment;
import com.malalaoshi.android.fragments.TeacherListFragment;
import com.malalaoshi.android.fragments.UserFragment;
import com.malalaoshi.android.fragments.UserTimetableFragment;
import com.malalaoshi.android.receiver.NetworkStateReceiver;
import com.malalaoshi.android.util.ImageCache;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity implements FragmentGroupAdapter.IFragmentGroup , View.OnClickListener, ViewPager.OnPageChangeListener {

    public static String EXTRAS_PAGE_INDEX = "page index";
    public static final int PAGE_INDEX_TEACHERS = 0;
    public static final int PAGE_INDEX_COURSES = 1;
    public static final int PAGE_INDEX_USER = 2;
    private int pageIndex = PAGE_INDEX_TEACHERS;

    //标题栏
    protected RelativeLayout rlHomeTitle;
    //标题
    protected TextView tvTitle;
    //标题栏上的三个tab
    protected LinearLayout llTitleTabs;
    protected ImageView ivTitleTabTeacher;
    protected ImageView ivTitleTabTimeTable;
    protected ImageView ivTitleTabUser;

    protected TextView tvTitleLocation;
    protected TextView tvTitleTady;

    //tabs
    protected RelativeLayout rlTabBar;
    //tab
    protected RelativeLayout rlTabTeacher;
    protected RelativeLayout rlTabTimetable;
    protected RelativeLayout rlTabUser;
    //tab textview
    protected TextView tvTabTeacher;
    protected TextView tvTabTimetable;
    protected TextView tvTabUserCenter;

    //tab indicator
    protected View tabTeacherIndicator;
    protected View tabTimetableIndicator;
    protected View tabUserCenterIndicator;

    private boolean initOnce = true;

    protected ViewPager vpHome;

    private NetworkStateReceiver mNetworkStateReceiver;


    private FragmentGroupAdapter mHomeFragmentAdapter;

    //具体数据内容页面
    private Map<Integer, Fragment> fragments = new HashMap<>();

    private long lastBackPressedTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        init();
        initData();
        initViews();
        setEvent();
    }

    private void init() {
        mNetworkStateReceiver = new NetworkStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);
        //获取待显示页索引
        pageIndex = getIntent().getIntExtra(EXTRAS_PAGE_INDEX,0);

        //标题栏
        rlHomeTitle = (RelativeLayout)findViewById(R.id.home_title);

        tvTitle = (TextView) findViewById(R.id.tv_title_text);

        llTitleTabs = (LinearLayout)findViewById(R.id.ll_home_title_tab);

        ivTitleTabTeacher = (ImageView)findViewById(R.id.iv_title_tab_teacher);
        ivTitleTabTimeTable = (ImageView)findViewById(R.id.iv_title_tab_timetable);
        ivTitleTabUser = (ImageView)findViewById(R.id.iv_title_tab_user);

        tvTitleLocation = (TextView) findViewById(R.id.tv_title_location);
        tvTitleTady = (TextView) findViewById(R.id.tv_title_tady);

        //tabs
        rlTabBar = (RelativeLayout)findViewById(R.id.rl_home_tabs);
        //tab
        rlTabTeacher = (RelativeLayout)findViewById(R.id.rl_tab_findteacher);
        rlTabTimetable = (RelativeLayout)findViewById(R.id.rl_tab_timetable);
        rlTabUser = (RelativeLayout)findViewById(R.id.rl_tab_usercenter);
        //tab textView
        tvTabTeacher = (TextView)findViewById(R.id.tv_tab_findteacher);
        tvTabTimetable = (TextView)findViewById(R.id.tv_tab_timetable);
        tvTabUserCenter = (TextView)findViewById(R.id.tv_tab_usercenter);
        //tab Indicator
        tabTeacherIndicator = (View)findViewById(R.id.view_tab_indicator_findteacher);
        tabTimetableIndicator = (View)findViewById(R.id.view_tab_indicator_timetable);
        tabUserCenterIndicator = (View)findViewById(R.id.view_tab_indicator_usercenter);

        vpHome = (ViewPager) findViewById(R.id.viewpage);
    }

    private void setEvent() {
        rlTabTeacher.setOnClickListener(this);
        rlTabTimetable.setOnClickListener(this);
        rlTabUser.setOnClickListener(this);
        ivTitleTabTeacher.setOnClickListener(this);
        ivTitleTabTimeTable.setOnClickListener(this);
        ivTitleTabUser.setOnClickListener(this);
        vpHome.setOnPageChangeListener(this);
        tvTitleLocation.setOnClickListener(this);
        tvTitleTady.setOnClickListener(this);
    }

    private void initViews() {
        setCurrentTab(pageIndex);
    }

    private void initData() {
        mHomeFragmentAdapter = new FragmentGroupAdapter(this,getSupportFragmentManager(), this);
        vpHome.setAdapter(mHomeFragmentAdapter);
        vpHome.setOffscreenPageLimit(2);//缓存页面
        vpHome.setCurrentItem(pageIndex);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //获取待显示页索引
        pageIndex = intent.getIntExtra(EXTRAS_PAGE_INDEX, pageIndex);
        setCurrentTab(pageIndex);
        vpHome.setCurrentItem(pageIndex);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_title_tab_teacher:
            case R.id.rl_tab_findteacher:
                setCurrentTab(PAGE_INDEX_TEACHERS);
                vpHome.setCurrentItem(PAGE_INDEX_TEACHERS);
                break;
            case R.id.iv_title_tab_timetable:
            case R.id.rl_tab_timetable:
                setCurrentTab(PAGE_INDEX_COURSES);
                vpHome.setCurrentItem(PAGE_INDEX_COURSES);
                break;
            case R.id.iv_title_tab_user:
            case R.id.rl_tab_usercenter:
                setCurrentTab(PAGE_INDEX_USER);
                vpHome.setCurrentItem(PAGE_INDEX_USER);
                break;
            case R.id.tv_title_location:
                onClickBarBtnLocation();
                break;
            case R.id.tv_title_tady:
                scrollToTady();
                break;
        }
    }

    //移动到今天
    private void scrollToTady() {
        UserTimetableFragment userTimetableFragment = (UserTimetableFragment) mHomeFragmentAdapter.getItem(1);
        if (userTimetableFragment!=null&&userTimetableFragment.isResumed()){
            userTimetableFragment.scrollToTady();
        }
    }

    private void loadCourses(){
        UserTimetableFragment userTimetableFragment = (UserTimetableFragment) mHomeFragmentAdapter.getItem(1);
        if (userTimetableFragment!=null&&userTimetableFragment.isResumed()){
            userTimetableFragment.loadDatas();
        }
    }

    private void setCurrentTab(int i) {
        switch (i){
            case PAGE_INDEX_TEACHERS:
                ivTitleTabTeacher.setSelected(true);
                ivTitleTabTimeTable.setSelected(false);
                ivTitleTabUser.setSelected(false);

                tabTeacherIndicator.setSelected(true);
                tabTimetableIndicator.setSelected(false);
                tabUserCenterIndicator.setSelected(false);

                tvTabTeacher.setTextColor(getResources().getColor(R.color.tab_text_press_color));
                tvTabTimetable.setTextColor(getResources().getColor(R.color.tab_text_normal_color));
                tvTabUserCenter.setTextColor(getResources().getColor(R.color.tab_text_normal_color));

                tvTitleLocation.setVisibility(View.VISIBLE);
                tvTitleTady.setVisibility(View.GONE);
                break;
            case PAGE_INDEX_COURSES:
                ivTitleTabTeacher.setSelected(false);
                ivTitleTabTimeTable.setSelected(true);
                ivTitleTabUser.setSelected(false);

                tabTeacherIndicator.setSelected(false);
                tabTimetableIndicator.setSelected(true);
                tabUserCenterIndicator.setSelected(false);

                tvTabTeacher.setTextColor(getResources().getColor(R.color.tab_text_normal_color));
                tvTabTimetable.setTextColor(getResources().getColor(R.color.tab_text_press_color));
                tvTabUserCenter.setTextColor(getResources().getColor(R.color.tab_text_normal_color));

                tvTitleLocation.setVisibility(View.GONE);
                tvTitleTady.setVisibility(View.VISIBLE);
                //下载数据
                loadCourses();
                break;
            case PAGE_INDEX_USER:
                ivTitleTabTeacher.setSelected(false);
                ivTitleTabTimeTable.setSelected(false);
                ivTitleTabUser.setSelected(true);

                tabTeacherIndicator.setSelected(false);
                tabTimetableIndicator.setSelected(false);
                tabUserCenterIndicator.setSelected(true);

                tvTabTeacher.setTextColor(getResources().getColor(R.color.tab_text_normal_color));
                tvTabTimetable.setTextColor(getResources().getColor(R.color.tab_text_normal_color));
                tvTabUserCenter.setTextColor(getResources().getColor(R.color.tab_text_press_color));

                tvTitleLocation.setVisibility(View.GONE);
                tvTitleTady.setVisibility(View.GONE);
                break;

        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    protected void onClickBarBtnLocation() {
//        Toast.makeText(this,"TODO: 提示目前只支持洛阳市，换成Dialog", Toast.LENGTH_SHORT).show();
        SimpleAlertDialogFragment d = SimpleAlertDialogFragment.newInstance("目前只支持洛阳市，其他地区正在拓展中", "我知道了",R.drawable.ic_location);
        d.show(getSupportFragmentManager(), SimpleAlertDialogFragment.class.getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        ImageCache.getInstance(this).flush();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetworkStateReceiver != null) {
            unregisterReceiver(mNetworkStateReceiver);
        }
        ImageCache.getInstance(this).close();
    }

    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = fragments.get(position);
        if (fragment == null) {
            switch (position) {
                case 0:
                    fragment = new TeacherListFragment();
                    break;
                case 1:
                    fragment = new UserTimetableFragment();
                    break;
                case 2:
                    fragment = new UserFragment();
                    break;
            }
        }
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public int getFragmentCount() {
        return 3;
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastBackPressedTime < 1000) {
            finish();
        } else {
            lastBackPressedTime = System.currentTimeMillis();
            Toast.makeText(this,"再按一次退出",Toast.LENGTH_SHORT).show();
        }
    }

}
