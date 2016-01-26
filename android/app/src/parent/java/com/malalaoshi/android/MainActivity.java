package com.malalaoshi.android;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.fragments.FilterDialogFragment;
import com.malalaoshi.android.fragments.LoginFragment;
import com.malalaoshi.android.fragments.ScheduleFragment;
import com.malalaoshi.android.fragments.SimpleAlertDialogFragment;
import com.malalaoshi.android.fragments.TeacherListFragment;
import com.malalaoshi.android.fragments.UserFragment;
import com.malalaoshi.android.receiver.NetworkStateReceiver;
import com.malalaoshi.android.util.FragmentUtil;
import com.malalaoshi.android.util.ImageCache;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {
    private  List<Teacher> teachersList = new ArrayList<Teacher>();

    private NetworkStateReceiver mNetworkStateReceiver;

    @Bind(R.id.index_home_btn)
    protected ImageView mImageViewHome;

    @Bind(R.id.index_personal_btn)
    protected ImageView mImageViewSchedule;

    private int selectIndex  = R.id.index_home_btn_view;

    private TeacherListFragment teacherListFragment = null;
    private UserFragment userFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

//        int statusBarHeight = ThemeUtils.getStatusBarHeight(this);
//
//        TextView mainBarLocation = (TextView)findViewById(R.id.main_bar_location);
//        Drawable[] drawable = mainBarLocation.getCompoundDrawables();
//        drawable[0].setBounds(0, 0, statusBarHeight, statusBarHeight);
//        mainBarLocation.setCompoundDrawables(drawable[0], drawable[1], drawable[2], drawable[3]);
//
//        TextView mainBarFilter = (TextView)findViewById(R.id.main_bar_filter);
//        Drawable[] drawableMBF = mainBarFilter.getCompoundDrawables();
//        drawableMBF[0].setBounds(0, 0, statusBarHeight, statusBarHeight);
//        mainBarFilter.setCompoundDrawables(drawableMBF[0], null, null, null);

        setSupportActionBar(toolbar);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
        ButterKnife.bind(this);
        teacherListFragment = new TeacherListFragment().setTeacherList(teachersList);
        FragmentUtil.opFragmentMainActivity(getSupportFragmentManager(), userFragment, teacherListFragment , TeacherListFragment.class.getName());
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
        d.show(getSupportFragmentManager(), SimpleAlertDialogFragment.class.getSimpleName());
    }

    @OnClick(R.id.main_bar_filter)
    protected void onClickBarBtnFilter() {
       // Dialog dialog = new Dialog(this,R.style.FilterDialog);
        //dialog.setContentView(R.layout.dialog_filter);
       // dialog.show();

        DialogFragment newFragment = FilterDialogFragment.newInstance();
        //newFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.FilterDialog);
        newFragment.show(getSupportFragmentManager(), FilterDialogFragment.class.getSimpleName());

    }

  /*  private void setDrawable(int viewId, int drawableId){
        ImageView iv = (ImageView)findViewById(viewId);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), drawableId, null);
        iv.setImageDrawable(drawable);
    }

     private void indexBtnEvent(MotionEvent event, int id, int drawableId, int pressedDrawableId){
        int action = event.getAction();
        switch(action){
            case 0:{
                setDrawable(id, pressedDrawableId);
                break;
            }
            case 1:{
                setDrawable(id, drawableId);
                break;
            }
            default:{
                break;
            }
        }
    }

   @OnTouch(R.id.index_home_btn_view)
    protected boolean onTouchIndexHomeBtn(MotionEvent event){
        indexBtnEvent(event, R.id.index_home_btn, R.drawable.index_home, R.drawable.index_home_press);
        if (teacherListFragment==null){
            teacherListFragment = new TeacherListFragment().setTeacherList(teachersList);
        }
        FragmentUtil.opFragmentMainActivity(getFragmentManager(), null, teacherListFragment, ScheduleFragment.class.getName());
        return true;
    }

    @OnTouch(R.id.index_personal_btn_view)
    protected boolean onTouchIndexPersonalBtn(MotionEvent event){
        indexBtnEvent(event, R.id.index_personal_btn, R.drawable.index_personal, R.drawable.index_personal_press);
        if (scheduleFragment==null){
            scheduleFragment = new ScheduleFragment();
        }

        FragmentUtil.opFragmentMainActivity(getFragmentManager(), null, scheduleFragment, ScheduleFragment.class.getName());
        return true;
    }*/

    @OnClick(R.id.index_home_btn_view)
    protected void onClickHome(View v){
        if (selectIndex!=R.id.index_home_btn_view){
            selectIndex = R.id.index_home_btn_view;
            mImageViewHome.setImageDrawable(getResources().getDrawable(R.drawable.index_home));
            mImageViewSchedule.setImageDrawable(getResources().getDrawable(R.drawable.index_personal_press));
            changeFragment(selectIndex);
        }
    }

    @OnClick(R.id.index_personal_btn_view)
    protected void onClickSchSchedule(View v){
        if (selectIndex!=R.id.index_personal_btn_view) {
            selectIndex = R.id.index_personal_btn_view;
            mImageViewHome.setImageDrawable(getResources().getDrawable(R.drawable.index_home_press));
            mImageViewSchedule.setImageDrawable(getResources().getDrawable(R.drawable.index_personal));
            changeFragment(selectIndex);
        }
    }

    private void changeFragment(int selectIndex) {
        switch (selectIndex){
            case R.id.index_home_btn_view:
                if (teacherListFragment==null){
                    teacherListFragment = new TeacherListFragment().setTeacherList(teachersList);
                }
                FragmentUtil.opFragmentMainActivity(getSupportFragmentManager(), userFragment, teacherListFragment, ScheduleFragment.class.getName());
                break;
            case R.id.index_personal_btn_view:
                if (userFragment==null){
                    userFragment = new UserFragment();
                }
                FragmentUtil.opFragmentMainActivity(getSupportFragmentManager(), teacherListFragment, userFragment, UserFragment.class.getName());
                break;
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!getFragmentManager().popBackStackImmediate()) {
                ActivityCompat.finishAfterTransition(this);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up signup_button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {
            MalaApplication.getInstance().logout();
            FragmentManager fragmentManager = getSupportFragmentManager();
            LoginFragment loginFragment = new LoginFragment();
            fragmentManager.beginTransaction().replace(R.id.content_layout, loginFragment).commit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
}