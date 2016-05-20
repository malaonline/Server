package com.malalaoshi.android;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.malalaoshi.android.activitys.MemberActivity;
import com.malalaoshi.android.activitys.OrderListActivity;
import com.malalaoshi.android.adapter.FragmentGroupAdapter;
import com.malalaoshi.android.api.UnpayOrderCountApi;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.dialogs.PromptDialog;
import com.malalaoshi.android.entity.UnpayOrders;
import com.malalaoshi.android.events.EventType;
import com.malalaoshi.android.events.UnpayOrderEvent;
import com.malalaoshi.android.fragments.SimpleAlertDialogFragment;
import com.malalaoshi.android.fragments.TeacherListFragment;
import com.malalaoshi.android.fragments.UserFragment;
import com.malalaoshi.android.fragments.UserTimetableFragment;
import com.malalaoshi.android.receiver.NetworkStateReceiver;
import com.malalaoshi.android.util.DialogUtil;
import com.malalaoshi.android.util.ImageCache;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;


public class MainActivity extends BaseActivity implements FragmentGroupAdapter.IFragmentGroup, View.OnClickListener, ViewPager.OnPageChangeListener {

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

    protected ImageView ivUnpaidOrders;

    protected ViewPager vpHome;

    private NetworkStateReceiver mNetworkStateReceiver;


    private FragmentGroupAdapter mHomeFragmentAdapter;

    //具体数据内容页面
    private Map<Integer, Fragment> fragments = new HashMap<>();

    private long lastBackPressedTime;

    private boolean isResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        init();
        initData();
        initViews();
        setEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUnpayOrders();
        isResume = true;
    }

    private void init() {
        mNetworkStateReceiver = new NetworkStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);
        //获取待显示页索引
        pageIndex = getIntent().getIntExtra(EXTRAS_PAGE_INDEX, 0);

        //标题栏
        rlHomeTitle = (RelativeLayout) findViewById(R.id.home_title);

        tvTitle = (TextView) findViewById(R.id.tv_title_text);

        llTitleTabs = (LinearLayout) findViewById(R.id.ll_home_title_tab);

        ivTitleTabTeacher = (ImageView) findViewById(R.id.iv_title_tab_teacher);
        ivTitleTabTimeTable = (ImageView) findViewById(R.id.iv_title_tab_timetable);
        ivTitleTabUser = (ImageView) findViewById(R.id.iv_title_tab_user);

        tvTitleLocation = (TextView) findViewById(R.id.tv_title_location);
        tvTitleTady = (TextView) findViewById(R.id.tv_title_tady);

        //tabs
        rlTabBar = (RelativeLayout) findViewById(R.id.rl_home_tabs);
        //tab
        rlTabTeacher = (RelativeLayout) findViewById(R.id.rl_tab_findteacher);
        rlTabTimetable = (RelativeLayout) findViewById(R.id.rl_tab_timetable);
        rlTabUser = (RelativeLayout) findViewById(R.id.rl_tab_usercenter);
        //tab textView
        tvTabTeacher = (TextView) findViewById(R.id.tv_tab_findteacher);
        tvTabTimetable = (TextView) findViewById(R.id.tv_tab_timetable);
        tvTabUserCenter = (TextView) findViewById(R.id.tv_tab_usercenter);
        //tab Indicator
        tabTeacherIndicator = findViewById(R.id.view_tab_indicator_findteacher);
        tabTimetableIndicator = findViewById(R.id.view_tab_indicator_timetable);
        tabUserCenterIndicator = findViewById(R.id.view_tab_indicator_usercenter);

        ivUnpaidOrders = (ImageView) findViewById(R.id.iv_unpaid_orders);

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
        EventBus.getDefault().register(this);
    }

    private void initViews() {
        setCurrentTab(pageIndex);
    }

    private void initData() {
        mHomeFragmentAdapter = new FragmentGroupAdapter(this, getSupportFragmentManager(), this);
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
        switch (v.getId()) {
            case R.id.iv_title_tab_teacher:
            case R.id.rl_tab_findteacher:
                setCurrentTab(PAGE_INDEX_TEACHERS);
                vpHome.setCurrentItem(PAGE_INDEX_TEACHERS);
                StatReporter.teacherListPage();
                break;
            case R.id.iv_title_tab_timetable:
            case R.id.rl_tab_timetable:
                setCurrentTab(PAGE_INDEX_COURSES);
                vpHome.setCurrentItem(PAGE_INDEX_COURSES);
                StatReporter.coursePage();
                break;
            case R.id.iv_title_tab_user:
            case R.id.rl_tab_usercenter:
                setCurrentTab(PAGE_INDEX_USER);
                vpHome.setCurrentItem(PAGE_INDEX_USER);
                StatReporter.myPage();
                break;
            case R.id.tv_title_location:
                onClickBarBtnLocation();
                StatReporter.ClickCityLocation();
                break;
            case R.id.tv_title_tady:
                scrollToToady();
                StatReporter.ClickToday();
                break;
        }
    }

    //移动到今天
    private void scrollToToady() {
        UserTimetableFragment userTimetableFragment = (UserTimetableFragment) mHomeFragmentAdapter.getItem(1);
        if (userTimetableFragment != null && userTimetableFragment.isResumed()) {
            userTimetableFragment.scrollToToday();
        }
    }

    private void loadCourses() {
        EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_TIMETABLE_DATA));
    }

    private void setCurrentTab(int i) {
        switch (i) {
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
        Toast.makeText(this, "TODO: 提示目前只支持洛阳市，换成Dialog", Toast.LENGTH_SHORT).show();
        SimpleAlertDialogFragment d = SimpleAlertDialogFragment.newInstance("目前只支持洛阳市，其他地区正在拓展中", "我知道了", R.drawable.ic_location);
        d.show(getSupportFragmentManager(), SimpleAlertDialogFragment.class.getSimpleName());
    }


    public void onEventMainThread(UnpayOrderEvent event) {
        switch (event.getEventType()) {
            case EventType.BUS_EVENT_UNPAY_ORDER_COUNT:
                if (event.getUnpayCount() > 0) {
                    ivUnpaidOrders.setVisibility(View.VISIBLE);
                    if (MalaApplication.getInstance().isFirstStartApp && isResume) {
                        showUnpaidOrderTipDialog();
                        MalaApplication.getInstance().isFirstStartApp = false;
                    }
                } else {
                    MalaApplication.getInstance().isFirstStartApp = false;
                    ivUnpaidOrders.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    private void showUnpaidOrderTipDialog() {
        //支付成功
        DialogUtil.showPromptDialog(getSupportFragmentManager(), R.drawable.ic_pay_success
                , "您有订单尚未支付!", "查看订单",
                new PromptDialog.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        Intent intent = new Intent(MainActivity.this, OrderListActivity.class);
                        startActivity(intent);
                    }
                }, true, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ImageCache.getInstance(this).flush();
        isResume = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetworkStateReceiver != null) {
            unregisterReceiver(mNetworkStateReceiver);
        }
        ImageCache.getInstance(this).close();
        EventBus.getDefault().unregister(this);
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


    public void loadUnpayOrders() {
        if (UserManager.getInstance().isLogin()) {
            ApiExecutor.exec(new LoadUnpayOrdersRequest(this));
        }
    }

    private static final class LoadUnpayOrdersRequest extends BaseApiContext<MainActivity, UnpayOrders> {

        public LoadUnpayOrdersRequest(MainActivity mainActivity) {
            super(mainActivity);
        }

        @Override
        public UnpayOrders request() throws Exception {
            return new UnpayOrderCountApi().get();
        }

        @Override
        public void onApiSuccess(@NonNull UnpayOrders unpayOrders) {
            if (unpayOrders != null && unpayOrders.getCount() != null) {
                UnpayOrderEvent unpayOrderEvent = new UnpayOrderEvent(EventType.BUS_EVENT_UNPAY_ORDER_COUNT);
                unpayOrderEvent.setUnpayCount(unpayOrders.getCount());
                EventBus.getDefault().post(unpayOrderEvent);
            }

        }

        @Override
        public void onApiFailure(Exception exception) {
        }
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastBackPressedTime < 1000) {
            finish();
        } else {
            lastBackPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected String getStatName() {
        return "家长主界面";
    }
}
