package com.malalaoshi.android;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.malalaoshi.android.activitys.OrderListActivity;
import com.malalaoshi.android.adapter.FragmentGroupAdapter;
import com.malalaoshi.android.api.NoticeMessageApi;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.dialogs.PromptDialog;
import com.malalaoshi.android.entity.NoticeMessage;
import com.malalaoshi.android.events.EventType;
import com.malalaoshi.android.events.NoticeEvent;
import com.malalaoshi.android.fragments.ScheduleFragment;
import com.malalaoshi.android.fragments.MemberServiceFragment;
import com.malalaoshi.android.fragments.SimpleAlertDialogFragment;
import com.malalaoshi.android.fragments.MainFragment;
import com.malalaoshi.android.fragments.UserFragment;
import com.malalaoshi.android.receiver.NetworkStateReceiver;
import com.malalaoshi.android.util.DialogUtil;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.LocManager;
import com.malalaoshi.android.util.PermissionUtil;
import com.malalaoshi.android.view.tabindicator.ViewPagerIndicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;


public class MainActivity extends BaseActivity implements FragmentGroupAdapter.IFragmentGroup, View.OnClickListener, ViewPagerIndicator.OnPageChangeListener, ScheduleFragment.OnClickEmptyCourse {

    public static String EXTRAS_PAGE_INDEX = "page index";
    public static final int PAGE_INDEX_TEACHERS = 0;
    public static final int PAGE_INDEX_COURSES = 1;
    public static final int PAGE_INDEX_MEMBER_SERVICE = 2;
    public static final int PAGE_INDEX_USER = 3;

    //位置相关权限
    private static final int PERMISSIONS_REQUEST_LOCATION = 0x07;

    private int pageIndex = PAGE_INDEX_TEACHERS;

    protected TextView tvTitleLocation;
    protected TextView tvTitleTady;

    private ViewPagerIndicator indicatorTabs;

    protected ViewPager vpHome;

    private NetworkStateReceiver mNetworkStateReceiver;

    //具体数据内容页面
    private Map<Integer, Fragment> fragments = new HashMap<>();

    //定位相关对象
    private LocManager locManager;

    private long lastBackPressedTime;

    private boolean isResume = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        init();
        //初始化定位
        initLocation();
        initData();
        initViews();
        setEvent();
        if (savedInstanceState!=null){
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (int i=0;fragments!=null&&i<fragments.size();i++){
                Fragment fragment = fragments.get(i);
                if (fragment instanceof ScheduleFragment){
                    ((ScheduleFragment)fragment).setOnClickEmptyCourse(this);
                    break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNoticeMessage();
        isResume = true;
    }

    private void init() {
        mNetworkStateReceiver = new NetworkStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);
        //获取待显示页索引
        pageIndex = getIntent().getIntExtra(EXTRAS_PAGE_INDEX, 0);

        tvTitleLocation = (TextView) findViewById(R.id.tv_title_location);
        tvTitleTady = (TextView) findViewById(R.id.tv_title_tady);

        indicatorTabs = (ViewPagerIndicator) findViewById(R.id.indicator_tabs);
        vpHome = (ViewPager) findViewById(R.id.viewpage);

        //得到LocationManager
        locManager = LocManager.getInstance();
    }

    private void setEvent() {
        tvTitleLocation.setOnClickListener(this);
        indicatorTabs.setViewPager(vpHome);
        indicatorTabs.setPageChangeListener(this);
        EventBus.getDefault().register(this);
    }

    private void initViews() {
        setCurrentPager(pageIndex);
    }

    //初始化定位
    private void initLocation() {

        //注册定位结果回调,定位接口直接放置于locManager对象中
        //locManager.registerLocationListener(this);
        //检测获取位置权限
        List<String> permissions = PermissionUtil.checkPermission(MainActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS});
        if (permissions == null) {
            Toast.makeText(this,"获取定位权限失败!",Toast.LENGTH_SHORT).show();
            return;
        }
        if (permissions.size() == 0) {
            initLocManager();
        } else {
            PermissionUtil.requestPermissions(MainActivity.this, permissions, PERMISSIONS_REQUEST_LOCATION);
        }
    }

    private void initLocManager() {
        loadLocation();
    }

    //启动定位
    void loadLocation() {
        locManager.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                permissionsResultLocation(grantResults);
                break;
            }
        }
    }

    private void permissionsResultLocation(int[] grantResults) {
        //如果请求被取消，那么 result 数组将为空
        boolean res = PermissionUtil.permissionsResult(grantResults);
        if (res) {
            // 已经获取对应权限
            initLocManager();
        } else {
            // 未获取到授权，取消需要该权限的方法
            Toast.makeText(this,"获取定位权限失败!",Toast.LENGTH_SHORT).show();
        }
    }

    private void initData() {
        indicatorTabs.setTitles(new String[]{"找老师","课表","会员专享","我的"});

        FragmentGroupAdapter homeFragmentAdapter = new FragmentGroupAdapter(this, getSupportFragmentManager(), this);
        vpHome.setAdapter(homeFragmentAdapter);
        vpHome.setOffscreenPageLimit(3);//缓存页面
        vpHome.setCurrentItem(pageIndex);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //获取待显示页索引
        pageIndex = intent.getIntExtra(EXTRAS_PAGE_INDEX, pageIndex);
        setCurrentPager(pageIndex);
        vpHome.setCurrentItem(pageIndex);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_title_location:
                onClickBarBtnLocation();
                StatReporter.ClickCityLocation();
                break;
        }
    }

    private void setCurrentPager(int i) {
        switch (i) {
            case PAGE_INDEX_TEACHERS:
                tvTitleLocation.setVisibility(View.VISIBLE);
                StatReporter.teacherListPage();
                break;
            case PAGE_INDEX_COURSES:
                EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_BACKGROUND_LOAD_TIMETABLE_DATA));
                tvTitleLocation.setVisibility(View.GONE);
                StatReporter.coursePage();
                break;
            case PAGE_INDEX_MEMBER_SERVICE:
                EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_BACKGROUND_LOAD_REPORT_DATA));
                tvTitleLocation.setVisibility(View.GONE);
                StatReporter.memberServicePage();
                break;
            case PAGE_INDEX_USER:
                EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_BACKGROUND_LOAD_USERCENTER_DATA));
                tvTitleLocation.setVisibility(View.GONE);
                StatReporter.myPage();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        setCurrentPager(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    protected void onClickBarBtnLocation() {
//        Toast.makeText(this,"TODO: 提示目前只支持郑州市，换成Dialog", Toast.LENGTH_SHORT).show();
        SimpleAlertDialogFragment d = SimpleAlertDialogFragment.newInstance("目前只支持郑州市，其他地区正在拓展中", "我知道了", R.drawable.ic_location);
        d.show(getSupportFragmentManager(), SimpleAlertDialogFragment.class.getName());
    }


    public void onEventMainThread(NoticeEvent event) {
        switch (event.getEventType()) {
            case EventType.BUS_EVENT_NOTICE_MESSAGE:
                if (event.getUnpayCount()>0){
                    indicatorTabs.setTabIndicatorVisibility(3,View.VISIBLE);
                    if (MalaApplication.getInstance().isFirstStartApp&&isResume){
                        showUnpaidOrderTipDialog();
                        MalaApplication.getInstance().isFirstStartApp = false;
                    }
                } else {
                    MalaApplication.getInstance().isFirstStartApp = false;
                    indicatorTabs.setTabIndicatorVisibility(3,View.INVISIBLE);
                }
                break;
        }
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.getEventType()) {
            case BusEvent.BUS_EVENT_LOGOUT_SUCCESS:
            case BusEvent.BUS_EVENT_LOGIN_SUCCESS:
                loadNoticeMessage();
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
                    fragment = new MainFragment();
                    break;
                case 1:
                    fragment = new ScheduleFragment();
                    ((ScheduleFragment)fragment).setOnClickEmptyCourse(this);
                    break;
                case 2:
                    fragment = new MemberServiceFragment();
                    break;
                case 3:
                    fragment = new UserFragment();
                    break;
            }
        }
        return fragment;
    }

    @Override
    public int getFragmentCount() {
        return 4;
    }


    public void loadNoticeMessage() {
        if (UserManager.getInstance().isLogin()) {
            ApiExecutor.exec(new LoadNoticeRequest(this));
        }else{
            NoticeEvent noticeEvent = new NoticeEvent(EventType.BUS_EVENT_NOTICE_MESSAGE);
            noticeEvent.setUnpayCount(0L);
            noticeEvent.setUncommentCount(0L);
            EventBus.getDefault().post(noticeEvent);
        }
    }

    @Override
    public void onClickEmptyCourse(View v) {
        setCurrentPager(PAGE_INDEX_TEACHERS);
        vpHome.setCurrentItem(PAGE_INDEX_TEACHERS);
    }

    private static final class LoadNoticeRequest extends BaseApiContext<MainActivity, NoticeMessage> {

        public LoadNoticeRequest(MainActivity mainActivity) {
            super(mainActivity);
        }

        @Override
        public NoticeMessage request() throws Exception {
            return new NoticeMessageApi().get();
        }

        @Override
        public void onApiSuccess(@NonNull NoticeMessage noticeMessage) {
            if (noticeMessage != null && noticeMessage.getUnpaid_num() != null&&noticeMessage.getTocomment_num()!=null) {
                NoticeEvent noticeEvent = new NoticeEvent(EventType.BUS_EVENT_NOTICE_MESSAGE);
                noticeEvent.setUnpayCount(noticeMessage.getUnpaid_num());
                noticeEvent.setUncommentCount(noticeMessage.getTocomment_num());
                EventBus.getDefault().post(noticeEvent);
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
