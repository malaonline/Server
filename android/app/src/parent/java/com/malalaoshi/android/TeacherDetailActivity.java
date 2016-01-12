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
import android.widget.ListAdapter;
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
import com.malalaoshi.android.adapter.CoursePriceAdapter;
import com.malalaoshi.android.adapter.HighScoreAdapter;
import com.malalaoshi.android.adapter.SchoolAdapter;
import com.malalaoshi.android.base.StatusBarActivity;
import com.malalaoshi.android.entity.CoursePrice;
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
import com.malalaoshi.android.util.StringUtil;
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

public class TeacherDetailActivity extends StatusBarActivity implements View.OnClickListener, CoursePriceAdapter.OnClickItem, AppBarLayout.OnOffsetChangedListener, LocManager.ReceiveLocationListener {

    private static final String TAG = "TeacherDetailActivity";

    private static final String EXTRA_TEACHER_ID = "teacherId";
    //教师id
    private Long mTeacherId;

    //接口地址
    private static final String TEACHING_ENVIRONMENT_PATH_V1 = "/api/v1/schools/";
    private static final String MEMBERSERVICES_PATH_V1 = "/api/v1/memberservices/";
    private static final String TEACHERS_PATH_V1 = "/api/v1/teachers/";


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

    @Bind(R.id.toolbar_title)
    protected TextView toolbarTitle;

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

    //教师性别
    @Bind(R.id.parent_teaching_age_tv)
    protected TextView mTeachingAge;

    //教师教授科目
    @Bind(R.id.parent_teacher_detail_subject_tv)
    protected TextView mTeacherSubject;

    //标签
    @Bind(R.id.parent_teacher_detail_tag_ll)
    protected LinearLayout mTagLayout;

    //教师分格
    @Bind(R.id.parent_teacher_detail_tag_tv)
    protected TextView mTeachingTags;

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
    @Bind(R.id.parent_teacher_detail_certificate_tv)
    protected TextView mCertificate;

    @Bind(R.id.parent_teacher_detail_memberservice_ll)
    protected LinearLayout mMemberServiceLayout;

    //会员服务
    @Bind(R.id.parent_teacher_detail_memberservice_tv)
    protected TextView mMemberServiceTv;

    //教龄级别
    @Bind(R.id.parent_teacher_detail_level_tv)
    protected TextView mTeacherLevel;

    //抵扣奖学金
    @Bind(R.id.parent_teacher_detail_scholarship_tv)
    protected TextView mScholarship;

    //价格表
    @Bind(R.id.parent_teacher_detail_pricelist_listview)
    protected ListView mCoursePriceList;

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

        setContentView(R.layout.teacher_detail);
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
            //没有体验中心,取最近的教学中心展示
            if (mSchools.size() <= 0) {
                mSchools.add(mOtherSchools.get(0));
                mOtherSchools.remove(0);
            }
        } else {
            if (mSchools.size() <= 0) {
                mSchools.add(mOtherSchools.get(0));
                mOtherSchools.remove(0);
            }
        }
        updateUISchools();
    }

    private void loadTeacherInfo() {
        String url = hostUrl + TEACHERS_PATH_V1 + mTeacherId + "/";
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
        mSchoolAdapter.notifyDataSetChanged();
    }

    //跟新会员服务接口
    private void updateUIServices(List<MemberService> mMemberServices) {
        if (mMemberServices != null && mMemberServices.size() > 0) {
            mMemberServiceLayout.setVisibility(View.VISIBLE);
            mMemberServiceTv.setText(StringUtil.joinEntityName(mMemberServices, "  "));
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
            Integer age = teacher.getTeaching_age();
            if (age != null) {
                mTeachingAge.setText(age.toString());
            }

            //教学科目
            StringBuilder strSubject = new StringBuilder();
            string = mTeacher.getSubject();
            if (string == null) {
                string = "";
            }

            //年级
            String[] grades = teacher.getGrades();
            if (grades != null && grades.length > 0) {
                for (int i = 0; i < grades.length; i++) {
                    strSubject.append(grades[i] + string + spot);
                }
                strSubject.setLength(strSubject.length() - spot.length());
            }
            mTeacherSubject.setText(strSubject.toString());

            //分格标签
            String[] tags = mTeacher.getTags();
            String tagsStr = StringUtil.join(tags, spot);
            if (tagsStr != null && tagsStr.length() > 0) {
                mTagLayout.setVisibility(View.VISIBLE);
                mTeachingTags.setText(tagsStr);
            }

            //提分榜
            List<HighScore> highScores = new ArrayList<HighScore>();
            //第一个为空,listview第一行为标题
            highScores.add(new HighScore());
            highScores.addAll(mTeacher.getHighscore_set());
            HighScoreAdapter highScoreAdapter = new HighScoreAdapter(this, highScores);
            mHighScoreList.setAdapter(highScoreAdapter);
            //个人相册
            loadGallery(mTeacher.getPhoto_set());
            //特殊成就
            String[] strCers = teacher.getCertificate_set();
            mCertificate.setText(StringUtil.join(strCers, spot));

            //教龄级别

            String level = teacher.getLevel();
            if (level != null) {
                mTeacherLevel.setText(level);
            }

            //价格表
            List<CoursePrice> coursePrices = teacher.getPrices();
            CoursePriceAdapter coursePriceAdapter = new CoursePriceAdapter(this, coursePrices != null ? coursePrices : (new ArrayList<CoursePrice>()));
            //添加按钮监听事件
            coursePriceAdapter.setOnClickItem(this);
            mCoursePriceList.setAdapter(coursePriceAdapter);
        }
    }


    void loadGallery(String[] gallery) {

        mGallery.removeAllViews();
        int width = mGallery.getWidth() / 3;
        for (int i = 0; gallery != null && i < gallery.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new ViewGroup.MarginLayoutParams(
                    width, width));
            imageView.setPadding(5, 5, 5, 5);
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
                //将界面移动到最底端
                mAppBarLayout.setExpanded(false);
                mNestedScrollViewContent.fullScroll(ScrollView.FOCUS_DOWN);
                break;
            case R.id.parent_teacher_detail_gallery_more_iv:
                //查看更多照片
                //Intent intent = new Intent(this, GalleryActivity.class);
                //startActivity(intent);
                break;
            case R.id.ll_school_more:
                //显示更多教学中心
                llSchoolMore.setVisibility(View.GONE);
                mSchools.addAll(mOtherSchools);
                mSchoolAdapter.notifyDataSetChanged();
                break;
        }
    }

    //报名
    @Override
    public void onClickItem(int position, Long gradeId) {


    }

    //设置上滑头像消失
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        Log.e(TAG, "toolbar_height:" + toolbar.getHeight() + " appBarLayout_height:" + mAppBarLayout.getHeight() + " Offset:" + verticalOffset);
        //设置头像上滑缩小消失
        int toolbarHeight = toolbar.getHeight();
        int headPortraitHeight = mRlTeacherHeadPortrait.getHeight();
        //最大上滑距离
        int maxOffset = mAppBarLayout.getHeight() - toolbarHeight;
        //头像彻底消失临界点
        int criticalPoint = headPortraitHeight / 2;
        int len = (maxOffset + verticalOffset) - criticalPoint;
        if (len <= 0) {
            mRlTeacherHeadPortrait.setVisibility(View.GONE);
            toolbar.setNavigationIcon(R.drawable.ic_black_back);
            toolbarTitle.setTextColor(getResources().getColor(R.color.text_color_darkgray));
        } else if (len > 0 && len < 50) {
            float ratio = (float) (len) / (float) 50;
            mRlTeacherHeadPortrait.setVisibility(View.VISIBLE);
            mRlTeacherHeadPortrait.setAlpha(ratio);
            toolbar.setNavigationIcon(R.drawable.ic_black_back);
            toolbarTitle.setTextColor(getResources().getColor(R.color.colorWhite));
        } else {
            mRlTeacherHeadPortrait.setVisibility(View.VISIBLE);
            mRlTeacherHeadPortrait.setAlpha(1.0f);
            toolbar.setNavigationIcon(R.drawable.ic_white_back);
            toolbarTitle.setTextColor(getResources().getColor(R.color.colorWhite));
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
