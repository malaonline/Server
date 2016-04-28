package com.malalaoshi.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.malalaoshi.android.activitys.GalleryActivity;
import com.malalaoshi.android.activitys.GalleryPreviewActivity;
import com.malalaoshi.android.adapter.HighScoreAdapter;
import com.malalaoshi.android.adapter.SchoolAdapter;
import com.malalaoshi.android.api.MemberServiceApi;
import com.malalaoshi.android.api.SchoolListApi;
import com.malalaoshi.android.api.TeacherInfoApi;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.LoginActivity;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.course.CourseConfirmActivity;
import com.malalaoshi.android.entity.Achievement;
import com.malalaoshi.android.entity.CoursePrice;
import com.malalaoshi.android.entity.HighScore;
import com.malalaoshi.android.entity.MemberService;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.fragments.LoginFragment;
import com.malalaoshi.android.listener.BounceTouchListener;
import com.malalaoshi.android.result.MemberServiceListResult;
import com.malalaoshi.android.result.SchoolListResult;
import com.malalaoshi.android.util.DialogUtil;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.LocManager;
import com.malalaoshi.android.util.LocationUtil;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.util.Number;
import com.malalaoshi.android.util.PermissionUtil;
import com.malalaoshi.android.view.CircleImageView;
import com.malalaoshi.android.view.FlowLayout;
import com.malalaoshi.android.view.ObservableScrollView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/3/29.
 */
public class TeacherInfoActivity extends BaseActivity implements View.OnClickListener, LocManager.ReceiveLocationListener, ObservableScrollView.ScrollViewListener, TitleBarView.OnTitleBarClickListener {

    private static final String EXTRA_TEACHER_ID = "teacherId";
    private static int REQUEST_CODE_LOGIN = 1000;

    //位置相关权限
    public static final int PERMISSIONS_REQUEST_LOCATION = 0x07;

    //教师id
    private Long mTeacherId;

    //所有教学中心
    private List<School> mAllSchools = null;

    //第一个显示的教学中心
    private List<School> mFirstSchool = null;

    //教师信息请求结果
    private Teacher mTeacher;

    //图片缓存
    private ImageLoader mImageLoader;

    //标题栏
    @Bind(R.id.titleBar)
    protected TitleBarView titleBarView;

    @Bind(R.id.view_line)
    protected View viewLine;

    //
    @Bind(R.id.scroll_view)
    protected ObservableScrollView scrollView;

    //背景图
    @Bind(R.id.header_image_view)
    protected View headerImage;

    //头像
    @Bind(R.id.parent_teacher_detail_head_portrait)
    protected CircleImageView mHeadPortrait;

    //教师姓名
    @Bind(R.id.parent_teacher_detail_name_tv)
    protected TextView mTeacherName;

    //教师性别
    @Bind(R.id.parent_teacher_detail_gender_iv)
    protected ImageView mTeacherGender;

    //教授科目
    @Bind(R.id.parent_teacher_detail_subject_tv)
    protected TextView mTeacherSubject;

    //价格区间
    @Bind(R.id.parent_teaching_price_tv)
    protected TextView mPriceRegion;

    //授课年级
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
    @Bind(R.id.parent_teacher_detail_gallery_ll)
    protected LinearLayout mLlGallery;

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

    @Bind(R.id.iv_school_more)
    protected ImageView ivSchoolMore;

    @Bind(R.id.tv_school_more)
    protected TextView tvSchoolMore;

    private SchoolAdapter mSchoolAdapter;

    private boolean isShowAllSchools = false;

    //定位相关对象
    private LocManager locManager;

    //当前经纬度
    private double longitude = 0.0f;
    private double latitude = 0.0f;


    private boolean teacherInfoFlag = false;
    private boolean schoolFlag = false;
    private boolean memberFlag = false;

    private boolean loadFinish = false;

    public static void open(Context context, Long teacherId) {
        if (teacherId != null) {
            Intent intent = new Intent(context, TeacherInfoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EXTRA_TEACHER_ID, teacherId);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_info);
        ButterKnife.bind(this);

        initViews();
        //得到LocationManager
        locManager = LocManager.getInstance(this);

        //初始化定位
        initLocation();
        //初始化数据
        initData();
        setEvent();

        //DialogUtil.startCircularProcessDialog(this, "正在加载数据", true, false);
        BounceTouchListener bounceTouchListener = new BounceTouchListener(scrollView, R.id.layout_teacher_info_body);
        bounceTouchListener.setOnTranslateListener(new BounceTouchListener.OnTranslateListener() {
            @Override
            public void onTranslate(float translation) {
                if (translation > 0) {
                    float scale = ((2 * translation) / headerImage.getMeasuredHeight()) + 1;
                    headerImage.setScaleX(scale);
                    headerImage.setScaleY(scale);
                } else {
                    headerImage.setScaleX(1);
                    headerImage.setScaleY(1);
                }
            }
        });

        scrollView.setOnTouchListener(bounceTouchListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!loadFinish) {
            DialogUtil.startCircularProcessDialog(this, "正在加载数据", true, false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!loadFinish) {
            DialogUtil.stopProcessDialog();
        }
    }

    private void stopProcess() {
        if (teacherInfoFlag && schoolFlag && memberFlag) {
            loadFinish = true;
            DialogUtil.stopProcessDialog();
        }
    }

    private void initViews() {
        mHighScoreList.setFocusable(false);
        listviewSchool.setFocusable(false);
    }

    private void setEvent() {
        mMoreGallery.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        llSchoolMore.setOnClickListener(this);
        scrollView.setScrollViewListener(this);
        titleBarView.setOnTitleBarClickListener(this);
    }

    //初始化定位
    private void initLocation() {

        //注册定位结果回调
        locManager.registerLocationListener(this);
        //检测获取位置权限
        List<String> permissions = PermissionUtil.checkPermission(TeacherInfoActivity.this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS});
        if (permissions == null) {
            return;
        }
        if (permissions.size() == 0) {
            initLocManager();
        } else {
            PermissionUtil.requestPermissions(TeacherInfoActivity.this, permissions, PERMISSIONS_REQUEST_LOCATION);
        }
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
            //Toast.makeText(this,"缺少定位相关权限",Toast.LENGTH_SHORT).show();
        }
    }

    private void initLocManager() {
        locManager.initLocation();
        loadLocation();
    }

    private void initData() {
        Intent intent = getIntent();
        mTeacherId = intent.getLongExtra(EXTRA_TEACHER_ID, 0);
        mImageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
        mAllSchools = new ArrayList<>();
        mFirstSchool = new ArrayList<>();
        mSchoolAdapter = new SchoolAdapter(this, mFirstSchool);
        listviewSchool.setAdapter(mSchoolAdapter);

        //老师
        loadTeacherInfo();
        //Member services
        ApiExecutor.exec(new LoadMemberServicesRequest(this));
        //请求教学环境信息
        ApiExecutor.exec(new LoadSchoolListRequest(this));
    }

    private void onLoadSchoolListSuccess(SchoolListResult result) {
        //获取体验中心
        mAllSchools.addAll(result.getResults());
        if (mAllSchools.size() > 0) {
            School school;
            for (int i = 0; i < mAllSchools.size(); i++) {
                if (mAllSchools.get(i).isCenter()) {
                    if (i == 0) {
                        break;
                    }
                    school = mAllSchools.get(i);
                    mAllSchools.set(i, mAllSchools.get(0));
                    mAllSchools.set(0, school);
                    break;
                }
            }
            mFirstSchool.add(mAllSchools.get(0));
            dealSchools();
        }
    }

    private void dealSchools() {
        //无数据
        if (mAllSchools.size() <= 0 && mFirstSchool.size() <= 0) {
            return;
        }

        //定位成功
        if (locManager.getLocationStatus() == LocManager.OK_LOCATION) {
            //排序
            LocationUtil.sortByDistance(mAllSchools, latitude, longitude);
            mFirstSchool.clear();
            mFirstSchool.add(mAllSchools.get(0));
            tvSchoolMore.setText(String.format("离您最近的社区中心 (%s)", LocationUtil.formatDistance(mAllSchools.get(0).getDistance())));
        } else {
            tvSchoolMore.setText("其他社区中心");
        }
        updateUISchools();
    }

    private void onLoadTeacherInfoSuccess(@NonNull Teacher teacher) {
        mTeacher = teacher;
        updateUI(mTeacher);
        mSignUp.setEnabled(true);
    }

    private void loadTeacherInfo() {
        if (mTeacherId == null) {
            return;
        }
        ApiExecutor.exec(new LoadTeacherInfoRequest(this, mTeacherId));
    }

    //启动定位
    void loadLocation() {
        locManager.start();
    }

    private void onLoadMemberServiceSuccess(MemberServiceListResult result) {
        if (result != null && result.getResults() != null && result.getResults().size() > 0) {
            updateUIServices(result.getResults());
        } else {
            Log.e(LoginFragment.class.getName(), "member services request failed!");
        }
    }

    //更新教学环境UI
    private void updateUISchools() {
        //教学环境
        llSchoolEnviroment.setVisibility(View.VISIBLE);
        llSchoolEnviroment.setFocusable(false);
        mSchoolAdapter.notifyDataSetChanged();

    }

    //跟新会员服务接口
    private void updateUIServices(List<MemberService> memberServices) {
        if (memberServices != null && memberServices.size() > 0) {
            mMemberServiceLayout.setVisibility(View.VISIBLE);
            String[] datas = new String[memberServices.size()];
            for (int i = 0; i < memberServices.size(); i++) {
                datas[i] = memberServices.get(i).getName();
            }
            setFlowDatas(mMemberServiceFl, datas);
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
                //titleBarView.setTitle(string);
            }
            //头像
            string = teacher.getAvatar();
            mImageLoader.get(string != null ? string : "", ImageLoader.getImageListener(mHeadPortrait, R.drawable.ic_default_teacher_avatar, R.drawable.ic_default_teacher_avatar));

            //性别
            String grender = teacher.getGender();
            if (grender != null && grender.equals("m")) {
                mTeacherGender.setImageResource(R.drawable.ic_male_gender);
            } else if (grender != null && grender.equals("f")) {
                mTeacherGender.setImageResource(R.drawable.ic_female_gender);
            }

            //教授科目
            if (!teacher.getSubject().isEmpty()) {
                mTeacherSubject.setText(teacher.getSubject());
            }

            Double minPrice = teacher.getMin_price();
            Double maxPrice = teacher.getMax_price();
            String region = null;
            if (minPrice != null && maxPrice != null) {

                region = com.malalaoshi.android.util.Number.subZeroAndDot(minPrice * 0.01d) + "-" + Number.subZeroAndDot(maxPrice * 0.01d) + "元/小时";
            } else if (minPrice != null) {
                region = Number.subZeroAndDot(minPrice * 0.01d) + "元/小时";
            } else if (maxPrice != null) {
                region = Number.subZeroAndDot(maxPrice * 0.01d) + "元/小时";
            }
            if (region != null) {
                mPriceRegion.setText(region);
            }

            //教授年级
            String[] grades = mTeacher.getGrades();
            if (grades != null && grades.length > 0) {
                setFlowDatas(mGrades, grades);
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
            if (achievements != null && achievements.size() > 0) {
                setFlowCertDatas(mAchievement, achievements, R.drawable.item_text_bg);
            }

            //教龄级别
            String level = teacher.getLevel();
            Integer teachAge = teacher.getTeaching_age();
            if (level != null && teachAge != null) {
                mTeacherLevel.setText(teachAge.toString() + "年" + "  " + level);
            } else if (level != null) {
                mTeacherLevel.setText(level);
            } else if (teachAge != null) {
                mTeacherLevel.setText(teachAge.toString() + "年");
            }
        }
    }

    private void setFlowCertDatas(FlowLayout flowlayout, final List<Achievement> datas, int drawable) {
        flowlayout.setFocusable(false);
        flowlayout.removeAllViews();
        int topButtomPadding = getResources().getDimensionPixelSize(R.dimen.item_text_top_bottom_padding);
        int leftRightPadding = getResources().getDimensionPixelSize(R.dimen.item_text_padding);
        for (int i = 0; datas != null && i < datas.size(); i++) {
            TextView textView = new TextView(this);
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //int padding = getResources().getDimensionPixelSize(R.dimen.item_text_padding);
            layoutParams.setMargins(leftRightPadding, topButtomPadding, leftRightPadding, topButtomPadding);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(leftRightPadding, leftRightPadding, leftRightPadding, leftRightPadding);
            textView.setText(datas.get(i).getTitle());
            textView.setTextColor(getResources().getColor(R.color.prices_name_text_color));
            textView.setBackground(getResources().getDrawable(drawable));
            final int finalI = i;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] imgUrl = new String[datas.size()];
                    String[] imgDes = new String[datas.size()];

                    for (int i = 0; i < datas.size(); i++) {
                        imgUrl[i] = datas.get(i).getImg();
                        imgDes[i] = datas.get(i).getTitle();
                    }
                    Intent intent = new Intent(TeacherInfoActivity.this, GalleryActivity.class);
                    intent.putExtra(GalleryActivity.GALLERY_URLS, imgUrl);
                    intent.putExtra(GalleryActivity.GALLERY_DES, imgDes);
                    intent.putExtra(GalleryActivity.GALLERY_CURRENT_INDEX, finalI);
                    startActivity(intent);
                    StatReporter.specialCertPage();

                }
            });
            flowlayout.addView(textView, i);
        }
    }

    private void setFlowDatas(FlowLayout flowlayout, String[] datas) {
        flowlayout.setFocusable(false);
        flowlayout.removeAllViews();

        int topButtomPadding = getResources().getDimensionPixelSize(R.dimen.item_text_top_bottom_padding);
        int leftRightPadding = getResources().getDimensionPixelSize(R.dimen.item_text_padding);
        for (int i = 0; datas != null && i < datas.length; i++) {
            TextView textView = new TextView(this);
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(leftRightPadding, topButtomPadding, leftRightPadding, topButtomPadding);
            textView.setText(datas[i]);
            textView.setTextColor(getResources().getColor(R.color.item_text_color_normal));
            flowlayout.addView(textView, i);
        }
    }


    void loadGallery(String[] gallery) {
        if (gallery == null || gallery.length <= 0) {
            mLlGallery.setVisibility(View.GONE);
            return;
        }
        mGallery.setFocusable(false);
        mGallery.removeAllViews();
        int margin = getResources().getDimensionPixelSize(R.dimen.item_gallery_padding);
        int height = getResources().getDimensionPixelSize(R.dimen.item_gallery_height);
        int width = (mGallery.getWidth() - 4 * margin) / 3;

        for (int i = 0; gallery != null && i < 3 && i < gallery.length; i++) {
            ImageView imageView = new ImageView(this);

            imageView.setLayoutParams(new ViewGroup.MarginLayoutParams(
                    width, height));

            if (i == 0) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
                layoutParams.setMargins(0, 0, margin, 0);
                imageView.setLayoutParams(layoutParams);
            } else if (i == 1) {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
                layoutParams.setMargins(margin, 0, margin, 0);
                imageView.setLayoutParams(layoutParams);
            } else {
                ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) imageView.getLayoutParams();
                layoutParams.setMargins(margin, 0, 0, 0);
                imageView.setLayoutParams(layoutParams);
            }

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            final int finalI = i;
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //查看更多照片
                    Intent intent = new Intent(TeacherInfoActivity.this, GalleryActivity.class);
                    intent.putExtra(GalleryActivity.GALLERY_URLS, mTeacher.getPhoto_set());
                    intent.putExtra(GalleryActivity.GALLERY_CURRENT_INDEX, finalI);
                    startActivity(intent);
                }
            });

            mImageLoader.get(gallery[i], ImageLoader.getImageListener(imageView, R.drawable.ic_default_img, R.drawable.ic_default_img));
            mGallery.addView(imageView, i);
        }
    }

    private void dealRequestError() {
        Toast.makeText(this, "网络请求失败!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parent_teacher_signup_btn:
                //
                signUp();
                break;
            case R.id.parent_teacher_detail_gallery_more_iv:
                //查看更多照片
                Intent intent = new Intent(this, GalleryPreviewActivity.class);
                intent.putExtra(GalleryPreviewActivity.GALLERY_URLS, mTeacher.getPhoto_set());
                startActivity(intent);
                break;
            case R.id.ll_school_more:
                //显示更多教学中心
                changeSchoolsShow();
                StatReporter.moreSchool();
                break;
        }
    }

    private void changeSchoolsShow() {

        if (!isShowAllSchools) {
            ivSchoolMore.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_up));
            tvSchoolMore.setText("收起");
            mSchoolAdapter.setSchools(mAllSchools);
            mSchoolAdapter.notifyDataSetChanged();
        } else {
            if (mAllSchools==null||mAllSchools.size()<=0){
                return;
            }
            Double dis = mAllSchools.get(0).getDistance();
            if (dis != null && dis >= 0) {
                tvSchoolMore.setText(String.format("离您最近的社区中心 (%s)", LocationUtil.formatDistance(dis)));
            } else {
                tvSchoolMore.setText("其他社区中心");
            }
            ivSchoolMore.setImageDrawable(getResources().getDrawable(R.drawable.ic_list_down));
            mSchoolAdapter.setSchools(mFirstSchool);
            mSchoolAdapter.notifyDataSetChanged();
        }
        isShowAllSchools = !isShowAllSchools;
    }

    private void signUp() {
        StatReporter.soonRoll();
        //判断是否登录
        if (UserManager.getInstance().isLogin()) {
            //跳转至报名页
            startCourseConfirmActivity();
        } else {
            //跳转登录页
            startSmsActivityRes();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOGIN) {
            if (resultCode == LoginActivity.RESULT_CODE_LOGIN_SUCCESS) {
                //跳转到课程购买页
                startCourseConfirmActivity();
            }
        }
    }

    //启动登录页
    private void startSmsActivityRes() {
        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivityForResult(intent, REQUEST_CODE_LOGIN);
    }

    //启动购买课程页
    private void startCourseConfirmActivity() {
        Intent signIntent = new Intent(this, CourseConfirmActivity.class);
        List<School> schools = new ArrayList<>();
        if (MiscUtil.isNotEmpty(mAllSchools)) {
            schools.addAll(mAllSchools);
        }
        /*if (MiscUtil.isNotEmpty(mOtherSchools)) {
            schools.addAll(mOtherSchools);
        }*/
        signIntent.putExtra(CourseConfirmActivity.EXTRA_SCHOOLS,
                schools.toArray(new School[schools.size()]));
        if (mTeacher != null && mTeacher.getPrices() != null) {
            signIntent.putExtra(CourseConfirmActivity.EXTRA_PRICES,
                    mTeacher.getPrices().toArray(new CoursePrice[mTeacher.getPrices().size()]));
            signIntent.putExtra(CourseConfirmActivity.EXTRA_TEACHER_ID, mTeacher.getId());
            signIntent.putExtra(CourseConfirmActivity.EXTRA_SUBJECT, mTeacher.getSubject());
        }
        startActivity(signIntent);
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

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        //最大上滑距离
        int maxOffset = headerImage.getMeasuredHeight() - titleBarView.getMeasuredHeight();
        //开始变色位置
        int startOffset = maxOffset / 2;
        if (y > startOffset && y < maxOffset - 10) {  //开始变色
            int ratio = (int) (255 * ((float) (y - startOffset) / (float) (maxOffset - startOffset + 10)));
            titleBarView.setLeftImageDrawable(getResources().getDrawable(R.drawable.core__back_btn));
            titleBarView.setBackgroundColor(Color.argb(ratio, 255, 255, 255));
            viewLine.setAlpha(0);
            titleBarView.setTitle("");
        } else if (y >= maxOffset - 10) {        //白色背景
            titleBarView.setLeftImageDrawable(getResources().getDrawable(R.drawable.core__back_btn));
            titleBarView.setBackgroundColor(Color.argb(255, 255, 255, 255));
            viewLine.setAlpha(1);
            if (mTeacher != null) {
                titleBarView.setTitle(mTeacher.getName());
            }
        } else {                            //无背景
            titleBarView.setLeftImageDrawable(getResources().getDrawable(R.drawable.core__white_btn));
            titleBarView.setBackgroundColor(Color.argb(0, 255, 255, 255));
            viewLine.setAlpha(0);
            titleBarView.setTitle("");
        }
    }

    @Override
    public void onTitleLeftClick() {
        this.finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    @Override
    protected String getStatName() {
        return "老师详情页";
    }

    private static final class LoadTeacherInfoRequest extends BaseApiContext<TeacherInfoActivity, Teacher> {

        private long teacherId;

        public LoadTeacherInfoRequest(TeacherInfoActivity teacherInfoActivity, long teacherId) {
            super(teacherInfoActivity);
            this.teacherId = teacherId;
        }

        @Override
        public Teacher request() throws Exception {
            return new TeacherInfoApi().get(teacherId);
        }

        @Override
        public void onApiSuccess(@NonNull Teacher response) {
            get().onLoadTeacherInfoSuccess(response);
        }

        @Override
        public void onApiFinished() {
            get().teacherInfoFlag = true;
            get().stopProcess();
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().dealRequestError();
        }
    }

    private static final class LoadMemberServicesRequest extends BaseApiContext<TeacherInfoActivity, MemberServiceListResult> {
        public LoadMemberServicesRequest(TeacherInfoActivity teacherInfoActivity) {
            super(teacherInfoActivity);
        }

        @Override
        public MemberServiceListResult request() throws Exception {
            return new MemberServiceApi().get();
        }

        @Override
        public void onApiSuccess(@NonNull MemberServiceListResult response) {
            get().onLoadMemberServiceSuccess(response);
        }

        @Override
        public void onApiFinished() {
            get().memberFlag = true;
            get().stopProcess();
        }
    }

    private static final class LoadSchoolListRequest extends BaseApiContext<TeacherInfoActivity, SchoolListResult> {

        public LoadSchoolListRequest(TeacherInfoActivity teacherInfoActivity) {
            super(teacherInfoActivity);
        }

        @Override
        public SchoolListResult request() throws Exception {
            return new SchoolListApi().get();
        }

        @Override
        public void onApiSuccess(@NonNull SchoolListResult response) {
            if (response.getResults() != null) {
                get().onLoadSchoolListSuccess(response);
            }
        }

        @Override
        public void onApiFinished() {
            get().schoolFlag = true;
            get().stopProcess();
        }

    }

}
