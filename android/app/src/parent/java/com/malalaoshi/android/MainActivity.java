package com.malalaoshi.android;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
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
import com.malalaoshi.android.view.HomeScrollView;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends BaseActivity implements FragmentGroupAdapter.IFragmentGroup , HomeScrollView.ScrollViewListener, View.OnClickListener, ViewPager.OnPageChangeListener {

    public static String EXTRAS_PAGE_INDEX = "page index";
    public static final int PAGE_INDEX_TEACHERS = 0;
    public static final int PAGE_INDEX_COURSES = 1;
    public static final int PAGE_INDEX_USER = 2;
    private int pageIndex = PAGE_INDEX_TEACHERS;

    protected HomeScrollView homeScrollView;

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
    private float tabSY;
    protected RelativeLayout rlTabBar;
    //tab
    protected RelativeLayout rlTabTeacher;
    private float tabTeacherX;
    private int tabTeacherWidth;
    private float tabTeacherTextSize;
    protected RelativeLayout rlTabTimetable;
    private float tabTimetableX;
    private int tabTimetableWidth;
    private float tabTimetableTextSize;
    protected RelativeLayout rlTabUser;
    private float tabUserCenterX;
    private int tabUserCenterWidth;
    private float tabUserCenterTextSize;
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

        homeScrollView = (HomeScrollView)findViewById(R.id.home_pager);
        vpHome = (ViewPager) findViewById(R.id.viewpage);
    }

    private void setEvent() {
        //homeScrollView.setOnScrollViewListener(this);
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
    public void onScrollChanged(HomeScrollView scrollView, int x, int y, int oldx, int oldy) {
        if (initOnce==true){
            initOnce = false;
            tabTeacherX = rlTabTeacher.getX();
            tabTeacherWidth = rlTabTeacher.getWidth();
            tabTeacherTextSize = tvTabTeacher.getTextSize();
            tabTimetableX = rlTabTimetable.getX();
            tabTimetableWidth = rlTabTimetable.getWidth();
            tabTimetableTextSize = tvTabTimetable.getTextSize();
            tabUserCenterX= rlTabUser.getX();
            tabUserCenterWidth= rlTabUser.getWidth();
            tabUserCenterTextSize = tvTabUserCenter.getTextSize();
            tabSY = rlTabBar.getY();
        }
        //titleTabs显隐
        transformTitleTabAlpha(y);
        //title text显隐
        transformTitleTextAlpha(y);
        //tab text显隐
        transformTabTextAlpha(y);
        //tab text缩放
        transformTabTextScale(y);
        //tabBar位移
        transformTabbarPos(y);
        //tab位移
        transformTabPos(y);
        //指示器显隐
        transformTabIndicatorAlpha(y);
    }

    void transformTitleTabAlpha(int y) {
        int topBoundary = getResources().getDimensionPixelSize(R.dimen.title_tab_appear_boundary_top);
        int bottomBoundary = getResources().getDimensionPixelSize(R.dimen.title_tab_disappear_boundary_bottom);
        //完全显示
        if (y>=topBoundary){
            llTitleTabs.setVisibility(View.VISIBLE);
            llTitleTabs.setAlpha(1);
        }else if (y<topBoundary&&y>bottomBoundary){
            int dis = topBoundary - bottomBoundary;
            float ratio = (float)(y-bottomBoundary)/(float)dis;
            llTitleTabs.setVisibility(View.VISIBLE);
            llTitleTabs.setAlpha(ratio);
        }else{
            llTitleTabs.setVisibility(View.GONE);
            llTitleTabs.setAlpha(0);
        }
    }



    void transformTitleTextAlpha(int y){
        int topBoundary = getResources().getDimensionPixelSize(R.dimen.title_text_disappear_boundary_top);
        int bottomBoundary = getResources().getDimensionPixelSize(R.dimen.title_text_appear_boundary_bottom);
        //彻底消失
        if (y>=topBoundary){
            tvTitle.setAlpha(0);
        }else if (y<topBoundary&&y>bottomBoundary){
            int dis = topBoundary - bottomBoundary;
            float ratio = (float)(y-bottomBoundary)/(float)dis;
            tvTitle.setAlpha(1 - ratio);
        }else{
            tvTitle.setAlpha(1);
        }
    }

    void transformTabTextAlpha(int y){
        int topBoundary = getResources().getDimensionPixelSize(R.dimen.tab_bar_disappear_boundary_top);
        int bottomBoundary = getResources().getDimensionPixelSize(R.dimen.tab_bar_appear_boundary_bottom);
        //彻底消失
        if (y>=topBoundary) {
            rlTabTeacher.setAlpha(0);
            rlTabTimetable.setAlpha(0);
            rlTabUser.setAlpha(0);
        }else if (y<topBoundary&&y>bottomBoundary){
            int dis = topBoundary - bottomBoundary;
            float ratio = (float)(y-bottomBoundary)/(float)dis;
            rlTabTeacher.setAlpha(1 - ratio);
            rlTabTimetable.setAlpha(1 - ratio);
            rlTabUser.setAlpha(1 - ratio);
        }else {
            rlTabTeacher.setAlpha(1);
            rlTabTimetable.setAlpha(1);
            rlTabUser.setAlpha(1);
        }
    }

    void transformTabTextScale(int y){
        int topBoundary = getResources().getDimensionPixelSize(R.dimen.tab_bar_scale_boundary_top);
        int bottomBoundary = getResources().getDimensionPixelSize(R.dimen.tab_bar_scale_boundary_bottom);
        //最小

        if (y>=topBoundary){
            //重置tab文字大小
            tvTabTeacher.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (tabTeacherTextSize * 0.8));
            tvTabTimetable.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (tabTimetableTextSize * 0.8));
            tvTabUserCenter.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (tabUserCenterTextSize * 0.8));
        }else if (y<topBoundary&&y>bottomBoundary){
            int dis = topBoundary - bottomBoundary;
            float ratio = (float)(y-bottomBoundary)/(float)dis;
            tvTabTeacher.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (tabTeacherTextSize * (0.8 + 0.2 * (1 - ratio))));
            tvTabTimetable.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (tabTimetableTextSize * (0.8 + 0.2 * (1 - ratio))));
            tvTabUserCenter.setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (tabUserCenterTextSize * (0.8 + 0.2 * (1 - ratio))));
        }else{
            //重置tab文字大小
            tvTabTeacher.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTeacherTextSize);
            tvTabTimetable.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTimetableTextSize);
            tvTabUserCenter.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabUserCenterTextSize);
        }
    }

    void transformTabbarPos(int y) {
        int topBoundary = getResources().getDimensionPixelSize(R.dimen.tab_bar_move_boundary_top);
        int bottomBoundary = getResources().getDimensionPixelSize(R.dimen.tab_bar_move_boundary_bottom);
        //完全显示
        if (y>=topBoundary) {
            rlTabBar.setVisibility(View.GONE);
            rlTabBar.setY(tabSY - (topBoundary - bottomBoundary));
        }else if (y<topBoundary&&y>bottomBoundary){
            int dis = topBoundary - bottomBoundary;
            float ratio = (float)(y-bottomBoundary)/(float)dis;
            float dis1 = y - bottomBoundary;
            rlTabBar.setVisibility(View.VISIBLE);
            rlTabBar.setY(tabSY - dis1);

        }else {
            rlTabBar.setVisibility(View.VISIBLE);
            rlTabBar.setY(tabSY);
        }
    }

    void transformTabPos(int y) {
        int topBoundary = getResources().getDimensionPixelSize(R.dimen.tab_move_left_boundary_top);
        int bottomBoundary = getResources().getDimensionPixelSize(R.dimen.tab_move_left_boundary_bottom);
        int tabDivider = getResources().getDimensionPixelSize(R.dimen.tab_divider);
        int tabPadding = getResources().getDimensionPixelSize(R.dimen.tab_text_padding);
        if (y>=topBoundary){
            //重置tab位置
            rlTabTeacher.setX(tabTeacherX);
            rlTabTimetable.setX(tabTeacherX + tvTabTeacher.getWidth() + tabDivider - 2 * tabPadding);
            rlTabUser.setX(tabTeacherX + tvTabTeacher.getWidth() + tvTabTimetable.getWidth() + 2 * tabDivider - 4 * tabPadding);
        }else if (y < topBoundary&&y>bottomBoundary){
            int dis = topBoundary - bottomBoundary;
            float ratio = (float) (y-bottomBoundary)/(float)dis;
            float dis1 = tabTimetableX-(tabTeacherX + tvTabTeacher.getWidth()+tabDivider - 2*tabPadding);
            float dis2 = tabUserCenterX - (tabTeacherX + tvTabTeacher.getWidth()+tvTabTimetable.getWidth()+2*tabDivider - 4*tabPadding);
            //重置tab位置
            rlTabTeacher.setX(tabTeacherX);
            rlTabTimetable.setX(tabTeacherX + tvTabTeacher.getWidth() + tabDivider - 2 * tabPadding + dis1 * (1 - ratio));
            rlTabUser.setX(tabTeacherX + tvTabTeacher.getWidth() + 2 * tabDivider - 4 * tabPadding + tvTabTimetable.getWidth() + dis2 * (1 - ratio));
        }else{
            //重置tab位置
            rlTabTeacher.setX(tabTeacherX);
            rlTabTimetable.setX(tabTimetableX);
            rlTabUser.setX(tabUserCenterX);
        }
    }

    void transformTabIndicatorAlpha(int y) {
        int topBoundary = getResources().getDimensionPixelSize(R.dimen.indicator_move_boundary_top);
        int bottomBoundary = getResources().getDimensionPixelSize(R.dimen.indicator_move_boundary_bottom);
        if (y>=topBoundary){
            tabTeacherIndicator.setAlpha(0);
            tabTimetableIndicator.setAlpha(0);
            tabUserCenterIndicator.setAlpha(0);
        }else if (y < topBoundary&&y>bottomBoundary){
            int dis = topBoundary - bottomBoundary;
            float ratio = (float) (y-bottomBoundary)/(float)dis;
            //重置tab位置
            tabTeacherIndicator.setAlpha(1 - ratio);
            tabTimetableIndicator.setAlpha(1 - ratio);
            tabUserCenterIndicator.setAlpha(1 - ratio);
        }else{
            tabTeacherIndicator.setAlpha(1);
            tabTimetableIndicator.setAlpha(1);
            tabUserCenterIndicator.setAlpha(1);
        }
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
