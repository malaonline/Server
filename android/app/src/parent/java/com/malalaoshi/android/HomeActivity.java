package com.malalaoshi.android;

import android.app.DialogFragment;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.TableLayout;


import com.malalaoshi.android.adapter.FragmentGroupAdapter;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.fragments.FilterDialogFragment;
import com.malalaoshi.android.fragments.ScheduleFragment;
import com.malalaoshi.android.fragments.SimpleAlertDialogFragment;
import com.malalaoshi.android.fragments.TeacherListFragment;
import com.malalaoshi.android.receiver.NetworkStateReceiver;
import com.malalaoshi.android.util.ImageCache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kang on 16/1/15.
 */
public class HomeActivity extends BaseActivity implements FragmentGroupAdapter.IFragmentGroup{
    private List<Teacher> teachersList = new ArrayList<Teacher>();

    private NetworkStateReceiver mNetworkStateReceiver;
    private ViewPager vpHome;
    private TabLayout tabHome;

    private FragmentGroupAdapter mHomeFragmentAdapter;

    //具体数据内容页面
    private Map<Integer, Fragment> fragments = new HashMap<>();
    private List<String> tabTiles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        initData();
        initViews();
        setEvent();
    }

    private void setEvent() {
    }

    private void initViews() {

    }

    private void initData() {

        vpHome = (ViewPager)findViewById(R.id.home_viewpager);
        mHomeFragmentAdapter = new FragmentGroupAdapter(this,getSupportFragmentManager(),this);
        vpHome.setAdapter(mHomeFragmentAdapter);
        vpHome.setOffscreenPageLimit(2);//缓存页面
        vpHome.setCurrentItem(0);
        tabTiles.add("找老师");
        tabTiles.add("课表");
        tabTiles.add("我的");
        tabHome = (TabLayout) findViewById(R.id.home_tablayout);
        tabHome.setupWithViewPager(vpHome);
        tabHome.setTabsFromPagerAdapter(mHomeFragmentAdapter);
    }

    private void init() {
        mNetworkStateReceiver = new NetworkStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetworkStateReceiver, filter);
    }

    @OnClick(R.id.main_bar_location)
    protected void onClickBarBtnLocation() {
//        Toast.makeText(this,"TODO: 提示目前只支持洛阳市，换成Dialog", Toast.LENGTH_SHORT).show();
        SimpleAlertDialogFragment d = SimpleAlertDialogFragment.newInstance("目前只支持洛阳市，其他地区正在拓展中", "知道了");
        d.show(getFragmentManager(), SimpleAlertDialogFragment.class.getSimpleName());
    }

    @OnClick(R.id.main_bar_filter)
    protected void onClickBarBtnFilter() {
        DialogFragment newFragment = FilterDialogFragment.newInstance();
        newFragment.show(getFragmentManager(), FilterDialogFragment.class.getSimpleName());
    }

    @Override
    public void onBackPressed() {
        if (!getFragmentManager().popBackStackImmediate()) {
            ActivityCompat.finishAfterTransition(this);
        }
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
                    fragment = new TeacherListFragment().setTeacherList(teachersList);
                    break;
                case 1:
                    fragment = new ScheduleFragment();
                    break;
                case 2:
                    fragment = new ScheduleFragment();
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
    public CharSequence getPageTitle(int position) {
        return tabTiles.get(position);
    }
}