package com.malalaoshi.android;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.fragments.FilterDialogFragment;
import com.malalaoshi.android.fragments.LoginFragment;
import com.malalaoshi.android.fragments.SimpleAlertDialogFragment;
import com.malalaoshi.android.fragments.TeacherListFragment;
import com.malalaoshi.android.receiver.NetworkStateReceiver;
import com.malalaoshi.android.util.FragmentUtil;
import com.malalaoshi.android.util.ThemeUtils;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NetworkStateReceiver mNetworkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        int statusBarHeight = ThemeUtils.getStatusBarHeight(this);

        TextView mainBarLocation = (TextView)findViewById(R.id.main_bar_location);
        Drawable[] drawable = mainBarLocation.getCompoundDrawables();
        drawable[0].setBounds(0, 0, statusBarHeight, statusBarHeight);
        mainBarLocation.setCompoundDrawables(drawable[0], drawable[1], drawable[2], drawable[3]);

        TextView mainBarFilter = (TextView)findViewById(R.id.main_bar_filter);
        Drawable[] drawableMBF = mainBarFilter.getCompoundDrawables();
        drawableMBF[0].setBounds(0, 0, statusBarHeight, statusBarHeight);
        mainBarFilter.setCompoundDrawables(drawableMBF[0], null, null, null);

        setSupportActionBar(toolbar);

//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();

//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);

        FragmentUtil.opFragmentMainActivity(getFragmentManager(), null, new TeacherListFragment(), TeacherListFragment.class.getName());
        ButterKnife.bind(this);
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

    private void setDrawable(int viewId, int drawableId){
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

    @OnTouch(R.id.index_home_btn)
    protected boolean onTouchIndexHomeBtn(MotionEvent event){
        indexBtnEvent(event, R.id.index_home_btn, R.drawable.index_home, R.drawable.index_home_press);
        return true;
    }

    @OnTouch(R.id.index_personal_btn)
    protected boolean onTouchIndexPersonalBtn(MotionEvent event){
        indexBtnEvent(event, R.id.index_personal_btn, R.drawable.index_personal, R.drawable.index_personal_press);
        return true;
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
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_logout) {
            MalaApplication.getInstance().logout();
            FragmentManager fragmentManager = getFragmentManager();
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
    protected void onDestroy() {
        super.onDestroy();
        if (mNetworkStateReceiver != null) {
            unregisterReceiver(mNetworkStateReceiver);
        }
    }

}