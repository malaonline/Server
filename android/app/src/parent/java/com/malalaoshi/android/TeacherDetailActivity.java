package com.malalaoshi.android;


import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.malalaoshi.android.activitys.GalleryActivity;
import com.malalaoshi.android.adapter.HighScoreAdapter;
import com.malalaoshi.android.adapter.SchoolAdapter;
import com.malalaoshi.android.base.StatusBarActivity;
import com.malalaoshi.android.entity.Achievement;
import com.malalaoshi.android.entity.HighScore;
import com.malalaoshi.android.entity.MemberService;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.fragments.LoginFragment;
import com.malalaoshi.android.listener.NavigationFinishClickListener;
import com.malalaoshi.android.result.MemberServiceListResult;
import com.malalaoshi.android.result.SchoolListResult;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.JsonUtil;
import com.malalaoshi.android.util.LocationUtil;
import com.malalaoshi.android.util.LocManager;
import com.malalaoshi.android.util.ThemeUtils;
import com.malalaoshi.android.view.CircleImageView;
import com.malalaoshi.android.view.FlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by zl on 15/11/30.
 */

public class TeacherDetailActivity extends StatusBarActivity implements View.OnClickListener, AppBarLayout.OnOffsetChangedListener, LocManager.ReceiveLocationListener {

    private static final String TAG = "TeacherDetailActivity";

    private static final String EXTRA_TEACHER_ID = "teacherId";
    //教师id
    private Long mTeacherId;

    //接口地址
    private static final String TEACHING_ENVIRONMENT_PATH_V1 = "/api/v1/schools";
    private static final String MEMBERSERVICES_PATH_V1 = "/api/v1/memberservices";
    private static final String TEACHERS_PATH_V1 = "/api/v1/teachers";


    //会员服务请求结果
    private MemberServiceListResult mMemberServicesResult;

    //教学中心列表
    private List<School> mSchools = null;

    //除体验中心剩余教学中心列表
    private List<School> mOtherSchools = null;

    //教师信息请求结果
    private Teacher mTeacher;

    //图片缓存
    private ImageLoader mImageLoader;

    //网络请求消息队列
    private RequestQueue requestQueue;
    private String hostUrl;
    private List<String> requestQueueTags;


    @Bind(R.id.parent_teacher_detail_appbar)
    protected AppBarLayout mAppBarLayout;

    @Bind(R.id.parent_teacher_detail_toolbar)
    protected Toolbar toolbar;

    @Bind(R.id.nestedscrollview_content)
    protected NestedScrollView mNestedScrollViewContent;

    @Bind(R.id.rl_teacher_head_portrait)
    protected RelativeLayout mRlTeacherHeadPortrait;

    //头像
    @Bind(R.id.parent_teacher_detail_head_portrait)
    protected CircleImageView mHeadPortrait;

    //教师姓名
    @Bind(R.id.parent_teacher_detail_name_tv)
    protected TextView mTeacherName;

    //教师性别
    @Bind(R.id.parent_teacher_detail_gender_tv)
    protected TextView mTeacherGender;

    //价格区间
    @Bind(R.id.parent_teaching_price_tv)
    protected TextView mPriceRegion;
    
    //更多相册
    @Bind(R.id.parent_teacher_detail_grade_fl)
    protected FlowLayout mGrades;

    //标签
    @Bind(R.id.parent_teacher_detail_tag_ll)
    protected LinearLayout mTagLayout;

    //教师分格
    @Bind(R.id.parent_teacher_detail_tag_fl)
    protected FlowLayout mTeachingTags;

    //教师提分榜
    @Bind(R.id.parent_teacher_detail_highscore_listview)
    protected ListView mHighScoreList;

    //个人相册
    @Bind(R.id.parent_teacher_detail_gallery_more_iv)
    protected ImageView mMoreGallery;

    //更多相册
    @Bind(R.id.parent_teacher_detail_gallery_fl)
    protected FlowLayout mGallery;

    //特殊成就
    @Bind(R.id.parent_teacher_detail_achievement_fl)
    protected FlowLayout mAchievement;

    @Bind(R.id.parent_teacher_detail_memberservice_ll)
    protected LinearLayout mMemberServiceLayout;

    //会员服务
    @Bind(R.id.parent_teacher_detail_memberservice_fl)
    protected FlowLayout mMemberServiceFl;

    //教龄级别
    @Bind(R.id.parent_teacher_detail_level_tv)
    protected TextView mTeacherLevel;

    //马上报名
    @Bind(R.id.parent_teacher_signup_btn)
    protected Button mSignUp;

    //教学环境
    @Bind(R.id.ll_school_environment)
    protected LinearLayout llSchoolEnviroment;

    //学习中心列表
    @Bind(R.id.listview_school)
    protected ListView listviewSchool;

    //更多学习中心
    @Bind(R.id.ll_school_more)
    protected LinearLayout llSchoolMore;

    @Bind(R.id.tv_school_more)
    protected TextView tvSchoolMore;

    private SchoolAdapter mSchoolAdapter;

    //定位相关对象
    private LocManager locManager;

    //当前经纬度
    private double longitude = 0.0f;
    private double latitude = 0.0f;

    public static void open(Context context, Long teacherId) {
        if (teacherId != null) {
            Intent intent = new Intent(context, TeacherDetailActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EXTRA_TEACHER_ID, teacherId);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_teacher_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ThemeUtils.setMargins(toolbar, 0, ThemeUtils.getStatusBarHeight(this), 0, 0);
        } else {
            ThemeUtils.setMargins(toolbar, 0, ThemeUtils.getStatusBarHeight(this) / 2, 0, 5);
        }

        toolbar.setNavigationOnClickListener(new NavigationFinishClickListener(this));
        //得到LocationManager
        locManager = LocManager.getInstance(this);

        //初始化定位
        initLocation();
        //初始化数据
        initData();
        setEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAppBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //volley联动,取消请求
        cancelAllRequestQueue();
        //停止定位sdk
        locManager.unregisterLocationListener(this);
    }

    private void setEvent() {
        mMoreGallery.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        llSchoolMore.setOnClickListener(this);
    }

    //初始化定位
    private void initLocation() {
        locManager.initLocation();
        //注册定位结果回调
        locManager.registerLocationListener(this);
        loadLocation();
    }

    //
    private void initData() {
        Intent intent = getIntent();
        mTeacherId = intent.getLongExtra(EXTRA_TEACHER_ID, 0);
        requestQueueTags = new ArrayList<String>();
        requestQueue = MalaApplication.getHttpRequestQueue();
        hostUrl = MalaApplication.getInstance().getMalaHost();
        mImageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
        mSchools = new ArrayList<School>();
        mOtherSchools = new ArrayList<>();
        mSchoolAdapter = new SchoolAdapter(this, mSchools);
        listviewSchool.setAdapter(mSchoolAdapter);
        mNestedScrollViewContent.fullScroll(ScrollView.FOCUS_UP);
        mNestedScrollViewContent.smoothScrollTo(0,0);
        loadTeacherInfo();
        loadMemeberServices();
        loadSchools();
    }

    //请求教学环境信息
    void loadSchools() {
        String url = hostUrl + "/" + TEACHING_ENVIRONMENT_PATH_V1;
        StringRequest jstringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Type listType = new TypeToken<ArrayList<MemberService>>(){}.getType();
                SchoolListResult schoolListResult = JsonUtil.parseStringData(response, SchoolListResult.class);
                if (schoolListResult == null || schoolListResult.getResults() == null) {
                    Log.e(LoginFragment.class.getName(), "school list request failed!");
                    return;
                }
                //获取体验中心
                School school = null;
                mOtherSchools.addAll(schoolListResult.getResults());
                if (mOtherSchools.size() > 0) {
                    for (int i = 0; i < mOtherSchools.size(); i++) {
                        if (true == mOtherSchools.get(i).isCenter()) {
                            school = mOtherSchools.get(i);
                            mOtherSchools.remove(i);
                            break;
                        }
                    }
                    if (school != null) {
                        mSchools.add(school);
                    }
                    dealSchools();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
            }
        });
        addRequestQueue(jstringRequest, TEACHING_ENVIRONMENT_PATH_V1);
    }

    private void dealSchools() {

        //无数据
        if (mSchools.size() <= 0 && mOtherSchools.size() <= 0) {
            return;
        }

        //定位成功
        if (locManager.getLocationStatus() == LocManager.OK_LOCATION) {
            //排序
            LocationUtil.sortByRegion(mOtherSchools, latitude, longitude);
            Double dis;
            if (mSchools.size()<=0){
                dis = mOtherSchools.get(0).getRegion();
            }else{
                if (mOtherSchools.size()<=0){
                    dis = mOtherSchools.get(0).getRegion();
                }else{
                    School school = mSchools.get(0);
                    dis = mOtherSchools.get(0).getRegion()-mSchools.get(0).getRegion()>0?mSchools.get(0).getRegion():mOtherSchools.get(0).getRegion();
                }
            }

            tvSchoolMore.setText("离您最近的社区中心 ("+dis+"m)");
            //没有体验中心,取最近的教学中心展示
            if (mSchools.size() <= 0) {
                mSchools.add(mOtherSchools.get(0));
                mOtherSchools.remove(0);
            }
        } else {
            tvSchoolMore.setText("其他社区中心");
            if (mSchools.size() <= 0) {
                mSchools.add(mOtherSchools.get(0));
                mOtherSchools.remove(0);
            }
        }
        updateUISchools();
    }

    private void loadTeacherInfo() {
        String url = hostUrl + TEACHERS_PATH_V1 + "/"+mTeacherId;
        StringRequest jstringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mTeacher = JsonUtil.parseStringData(response, Teacher.class);
                //mTeacher = JsonUtil.parseData(R.raw.teacher, Teacher.class, TeacherDetailActivity.this);
                if (mTeacher != null) {
                    updateUI(mTeacher);
                } else {
                    //数据请求失败

                }
                //停止进度条
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dealRequestError(error.getMessage());
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
                //停止进度条,数据请求失败

            }
        });
        addRequestQueue(jstringRequest, TEACHERS_PATH_V1);
    }

    //启动定位
    void loadLocation() {
        locManager.start();
    }

    private void loadMemeberServices() {
        //String url = hostUrl +MEMBERSERVICES_PATH_V1;
        String url = hostUrl + MEMBERSERVICES_PATH_V1;
        StringRequest jstringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mMemberServicesResult = JsonUtil.parseStringData(response, MemberServiceListResult.class);
                //mMemberServicesResult = JsonUtil.parseData(R.raw.memberservice, MemberServiceListResult.class, TeacherDetailActivity.this);
                if (mMemberServicesResult != null && mMemberServicesResult.getResults() != null && mMemberServicesResult.getResults().size() > 0) {
                    updateUIServices(mMemberServicesResult.getResults());
                } else {
                    Log.e(LoginFragment.class.getName(), "member services request failed!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
            }
        });

        addRequestQueue(jstringRequest, MEMBERSERVICES_PATH_V1);
    }

    //更新教学环境UI
    private void updateUISchools() {
        //教学环境
        llSchoolEnviroment.setVisibility(View.VISIBLE);
        llSchoolEnviroment.setFocusable(false);
        mSchoolAdapter.notifyDataSetChanged();

    }

    //跟新会员服务接口
    private void updateUIServices(List<MemberService> mMemberServices) {
        if (mMemberServices != null && mMemberServices.size() > 0) {
            mMemberServiceLayout.setVisibility(View.VISIBLE);
            String[] datas = new String[mMemberServices.size()];
            for (int i =0;i<mMemberServices.size();i++){
                datas[i] = mMemberServices.get(i).getName();
            }
            setFlowDatas(mMemberServiceFl,datas);
        }
    }

    //跟新教师详情
    private void updateUI(Teacher teacher) {
        if (teacher != null) {
            String string;
            String spot = "  ";
            //姓名
            string = teacher.getName();
            if (string != null) {
                mTeacherName.setText(string);
            }
            //头像
            string = teacher.getAvatar();
            mImageLoader.get(string != null ? string : "", ImageLoader.getImageListener(mHeadPortrait, R.drawable.user_detail_header_bg, R.drawable.user_detail_header_bg));

            //性别
            Character ch = teacher.getGender();
            if (ch != null && ch == 'm') {
                mTeacherGender.setText("男");
            } else if (ch != null && ch == 'w') {
                mTeacherGender.setText("女");
            } else {
                mTeacherGender.setText("保密");
            }

            Double minPrice = teacher.getMin_price();
            Double maxPrice = teacher.getMax_price();
            String region = null;
            if (minPrice!=null&&maxPrice!=null){
                region = minPrice+"/"+maxPrice+"元每小时";
            }
            else if (minPrice!=null){
                region = minPrice+"元每小时";
            }
            else if (maxPrice!=null){
                region = maxPrice+"元每小时";
            }
            if (region != null) {
                mPriceRegion.setText(region);
            }

            //教授年级
            String[] grades = mTeacher.getGrades();
            if (grades!=null&&grades.length>0){
                setFlowDatas(mGrades,grades);
            }

            //分格标签
            String[] tags = mTeacher.getTags();
            if (tags != null && tags.length > 0) {
                mTagLayout.setVisibility(View.VISIBLE);
                setFlowDatas(mTeachingTags, tags);
            }

            //提分榜
            List<HighScore> highScores = new ArrayList<HighScore>();
            //第一个为空,listview第一行为标题
            highScores.add(new HighScore());
            highScores.addAll(mTeacher.getHighscore_set());
            HighScoreAdapter highScoreAdapter = new HighScoreAdapter(this, highScores);
            mHighScoreList.setFocusable(true);
            mHighScoreList.setAdapter(highScoreAdapter);

            //个人相册
            loadGallery(mTeacher.getPhoto_set());
            //特殊成就
            List<Achievement> achievements = mTeacher.getAchievement_set();
            if (achievements!=null&&achievements.size()>0){
                String[] achievementArr = new String[achievements.size()];
                for (int i=0;i<achievements.size();i++){
                    achievementArr[i] = achievements.get(i).getTitle();
                }
                setFlowDatas(mAchievement,achievementArr, R.drawable.item_text_bg);
            }

            //教龄级别
            String level = teacher.getLevel();
            Integer teachAge = teacher.getTeaching_age();
            if (level != null&&teachAge!=null) {
                mTeacherLevel.setText(level+" "+teachAge.toString());
            }else if (level != null){
                mTeacherLevel.setText(level);
            }else if (teachAge!=null){
                mTeacherLevel.setText(teachAge.toString());
            }
        }
    }

    private void setFlowDatas(FlowLayout flowlayout, String[] datas, int drawable) {
        flowlayout.setFocusable(false);
        flowlayout.removeAllViews();
        for (int i = 0; datas != null && i < datas.length; i++) {
            TextView textView = new TextView(this);
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int padding = getResources().getDimensionPixelSize(R.dimen.item_text_padding);
            layoutParams.setMargins(padding,padding,padding,padding);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(padding,padding,padding,padding);
            textView.setText(datas[i]);
            textView.setBackground(getResources().getDrawable(drawable));
            flowlayout.addView(textView, i);
        }
    }

    private void setFlowDatas(FlowLayout flowlayout, String[] datas) {
        flowlayout.setFocusable(false);
        flowlayout.removeAllViews();
        for (int i = 0; datas != null && i < datas.length; i++) {
            TextView textView = new TextView(this);
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            int padding = getResources().getDimensionPixelSize(R.dimen.item_text_padding);
            textView.setPadding(padding, padding, padding, padding);
            textView.setText(datas[i]);
            flowlayout.addView(textView, i);
        }
    }


    void loadGallery(String[] gallery) {
        mGallery.setFocusable(false);
        mGallery.removeAllViews();
        int width = mGallery.getWidth() / 3;

        for (int i = 0; gallery != null && i < 3 && i < gallery.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new ViewGroup.MarginLayoutParams(
                    width, width));
            int margin = getResources().getDimensionPixelSize(R.dimen.item_gallery_padding);

            if (i==0){
                imageView.setPadding(0, 0, margin, 0);
            }else if(i==1){
                imageView.setPadding(margin, 0, margin, 0);
            }else{
                imageView.setPadding(margin, 0, 0, 0);
            }

            final int finalI = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //查看更多照片
                    Intent intent = new Intent(TeacherDetailActivity.this, GalleryActivity.class);
                    intent.putExtra(GalleryActivity.GALLERY_URLS, mTeacher.getPhoto_set());
                    intent.putExtra(GalleryActivity.GALLERY_CURRENT_INDEX, finalI);
                    startActivity(intent);
                }
            });
            mImageLoader.get(gallery[i], ImageLoader.getImageListener(imageView, R.drawable.user_detail_header_bg, R.drawable.user_detail_header_bg));
            mGallery.addView(imageView, i);
        }
    }

    private void dealRequestError(String errorCode) {
        Toast.makeText(this, "网络请求失败!", Toast.LENGTH_SHORT).show();
    }

    //向请求队列添加请求
    public void addRequestQueue(StringRequest stringRequest, String requestTag) {
        requestQueueTags.add(requestTag);
        stringRequest.setTag(requestTag);
        requestQueue.add(stringRequest);
    }

    //取消说有网络请求
    public void cancelAllRequestQueue() {
        for (int i = 0; requestQueue != null && i < requestQueueTags.size(); i++) {
            requestQueue.cancelAll(requestQueueTags.get(i));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parent_teacher_signup_btn:

                break;
            case R.id.parent_teacher_detail_gallery_more_iv:
                //查看更多照片
                Intent intent = new Intent(this, GalleryActivity.class);
                intent.putExtra(GalleryActivity.GALLERY_URLS, mTeacher.getPhoto_set());
                //intent.getIntExtra(GalleryActivity.GALLERY_CURRENT_INDEX,)
                startActivity(intent);
                break;
            case R.id.ll_school_more:
                //显示更多教学中心
                llSchoolMore.setVisibility(View.GONE);
                mSchools.addAll(mOtherSchools);
                mSchoolAdapter.notifyDataSetChanged();
                break;
        }
    }

    //设置上滑头像消失
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
       //设置头像上滑缩小消失
        int toolbarHeight = toolbar.getHeight();
        int headPortraitHeight = mRlTeacherHeadPortrait.getHeight();
        //最大上滑距离
        int maxOffset = mAppBarLayout.getHeight() - toolbarHeight;
        //头像彻底消失临界点
        int criticalPoint = headPortraitHeight *35 / 75;
        Log.e(TAG, "toolbar_height:" + toolbarHeight + " appBarLayout_height:" + mAppBarLayout.getHeight() + " Offset:" + verticalOffset);
        int len = (maxOffset + verticalOffset);
        if (len <= 0) {
            mRlTeacherHeadPortrait.setVisibility(View.GONE);
            toolbar.setNavigationIcon(R.drawable.ic_black_back);
            //toolbarTitle.setTextColor(getResources().getColor(R.color.text_color_darkgray));
        } else if (len > 0 && len < toolbarHeight) {
            float ratio = (float) (len) / (float) toolbarHeight;
            mRlTeacherHeadPortrait.setVisibility(View.VISIBLE);
            mRlTeacherHeadPortrait.setAlpha(ratio);
            toolbar.setNavigationIcon(R.drawable.ic_black_back);
            //toolbarTitle.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            mRlTeacherHeadPortrait.setVisibility(View.VISIBLE);
            mRlTeacherHeadPortrait.setAlpha(1.0f);
            toolbar.setNavigationIcon(R.drawable.ic_white_back);
            //toolbarTitle.setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }


    @Override
    public void onReceiveLocation(Location location) {
        if (location == null) {
            return;
        } else {

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.i(TAG, "positioning success," + ",\nlatitude:" + latitude + ",\nlongitude:" + longitude);
            //定位成功后更新school列表,定位失败则不做处理
            dealSchools();
        }
    }
}
