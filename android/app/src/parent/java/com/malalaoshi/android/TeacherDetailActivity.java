package com.malalaoshi.android;

import android.Manifest;
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

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.malalaoshi.android.activitys.GalleryActivity;
import com.malalaoshi.android.activitys.GalleryPreviewActivity;
import com.malalaoshi.android.adapter.HighScoreAdapter;
import com.malalaoshi.android.adapter.SchoolAdapter;
import com.malalaoshi.android.base.StatusBarActivity;
import com.malalaoshi.android.course.CourseConfirmActivity;
import com.malalaoshi.android.entity.Achievement;
import com.malalaoshi.android.entity.CoursePrice;
import com.malalaoshi.android.entity.HighScore;
import com.malalaoshi.android.entity.MemberService;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.fragments.LoginFragment;
import com.malalaoshi.android.listener.NavigationFinishClickListener;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.result.MemberServiceListResult;
import com.malalaoshi.android.result.SchoolListResult;
import com.malalaoshi.android.usercenter.SmsAuthActivity;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.JsonUtil;
import com.malalaoshi.android.util.LocManager;
import com.malalaoshi.android.util.LocationUtil;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.util.PermissionUtil;
import com.malalaoshi.android.util.ThemeUtils;
import com.malalaoshi.android.util.UserManager;
import com.malalaoshi.android.view.CircleImageView;
import com.malalaoshi.android.view.FlowLayout;
import com.malalaoshi.android.util.Number;

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
    protected void onDestroy() {
        super.onDestroy();
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

        //注册定位结果回调
        locManager.registerLocationListener(this);
        //检测获取位置权限
        List<String> permissions = PermissionUtil.checkPermission(TeacherDetailActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS});
        if (permissions==null){
            return ;
        }
        if (permissions.size()==0){
            initLocManager();
        }else{
            PermissionUtil.requestPermissions(TeacherDetailActivity.this,permissions, PERMISSIONS_REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); switch (requestCode) {
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

    private void initLocManager(){
        locManager.initLocation();
        loadLocation();
    }

    //
    private void initData() {
        Intent intent = getIntent();
        mTeacherId = intent.getLongExtra(EXTRA_TEACHER_ID, 0);
        mImageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
        mAllSchools = new ArrayList<School>();
        mFirstSchool = new ArrayList<>();
        mSchoolAdapter = new SchoolAdapter(this, mFirstSchool);
        listviewSchool.setAdapter(mSchoolAdapter);
        mNestedScrollViewContent.fullScroll(ScrollView.FOCUS_UP);
        mNestedScrollViewContent.smoothScrollTo(0, 0);
        loadTeacherInfo();
        loadMemeberServices();
        loadSchools();
    }

    //请求教学环境信息
    void loadSchools() {
        NetworkSender.getSchoolList(new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                if (json == null) {
                    return;
                }
                SchoolListResult schoolListResult = JsonUtil.parseStringData(json.toString(), SchoolListResult.class);
                if (schoolListResult == null || schoolListResult.getResults() == null) {
                    Log.e(LoginFragment.class.getName(), "school list request failed!");
                    return;
                }
                //获取体验中心
                mAllSchools.addAll(schoolListResult.getResults());
                if (mAllSchools.size() > 0) {
                    School school = null;
                    for (int i = 0; i < mAllSchools.size(); i++) {
                        if (true == mAllSchools.get(i).isCenter()) {
                            if (i == 0) {
                                break;
                            }
                            school = mAllSchools.get(i);
                            mAllSchools.set(i, mAllSchools.get(0));
                            mAllSchools.set(0, school);
                            //mOtherSchools.remove(i);
                            break;
                        }
                    }
                    mFirstSchool.add(mAllSchools.get(0));
                    dealSchools();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
            }
        });
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
            Double dis;
            mFirstSchool.clear();
            mFirstSchool.add(mAllSchools.get(0));
            tvSchoolMore.setText(String.format("离您最近的社区中心 (%s))",LocationUtil.formatDistance(mAllSchools.get(0).getDistance())));
        } else {
            tvSchoolMore.setText("其他社区中心");
        }
        updateUISchools();
    }

    private void loadTeacherInfo() {
        NetworkSender.getTeacherInfo(String.format("%d", mTeacherId), new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                if (json != null && !json.toString().isEmpty()) {
                    mTeacher = JsonUtil.parseStringData(json.toString(), Teacher.class);
                    //mTeacher = JsonUtil.parseData(R.raw.teacher, Teacher.class, TeacherDetailActivity.this);
                    if (mTeacher != null) {
                        updateUI(mTeacher);
                        mSignUp.setEnabled(true);
                        return;
                    }
                }
                dealRequestError("");
            }

            @Override
            public void onFailed(VolleyError error) {
                dealRequestError(error.getMessage());
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
            }
        });
    }

    //启动定位
    void loadLocation() {
        locManager.start();
    }

    private void loadMemeberServices() {
        NetworkSender.getMemberService(new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                if (json == null) {
                    return;
                }
                MemberServiceListResult memberServiceListResult = JsonUtil.parseStringData(json.toString(), MemberServiceListResult.class);
                //mMemberServicesResult = JsonUtil.parseData(R.raw.memberservice, MemberServiceListResult.class, TeacherDetailActivity.this);
                if (memberServiceListResult != null && memberServiceListResult.getResults() != null && memberServiceListResult.getResults().size() > 0) {
                    updateUIServices(memberServiceListResult.getResults());
                } else {
                    Log.e(LoginFragment.class.getName(), "member services request failed!");
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
            }
        });
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
            for (int i = 0; i < mMemberServices.size(); i++) {
                datas[i] = mMemberServices.get(i).getName();
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
            }
            //头像
            string = teacher.getAvatar();
            mImageLoader.get(string != null ? string : "", ImageLoader.getImageListener(mHeadPortrait, R.drawable.ic_default_teacher_avatar, R.drawable.ic_default_teacher_avatar));

            //性别
            String grender = teacher.getGender();
            if (grender != null && grender.equals("m")) {
                mTeacherGender.setText("男");
            } else if (grender != null && grender.equals("f")) {
                mTeacherGender.setText("女");
            } else {
                mTeacherGender.setText("保密");
            }

            Double minPrice = teacher.getMin_price();
            Double maxPrice = teacher.getMax_price();
            String region = null;
            if (minPrice != null && maxPrice != null) {

                region = Number.subZeroAndDot(minPrice * 0.01d) + "-" + Number.subZeroAndDot(maxPrice * 0.01d) + "元/小时";
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
        for (int i = 0; datas != null && i < datas.size(); i++) {
            TextView textView = new TextView(this);
            ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            int padding = getResources().getDimensionPixelSize(R.dimen.item_text_padding);
            layoutParams.setMargins(padding, padding, padding, padding);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(padding, padding, padding, padding);
            textView.setText(datas.get(i).getTitle());
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
                    Intent intent = new Intent(TeacherDetailActivity.this, GalleryActivity.class);
                    intent.putExtra(GalleryActivity.GALLERY_URLS, imgUrl);
                    intent.putExtra(GalleryActivity.GALLERY_DES, imgDes);
                    intent.putExtra(GalleryActivity.GALLERY_CURRENT_INDEX, finalI);
                    startActivity(intent);

                }
            });
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
        int margin = getResources().getDimensionPixelSize(R.dimen.item_gallery_padding);
        int width = (mGallery.getWidth()-4*margin) / 3;

        for (int i = 0; gallery != null && i < 3 && i < gallery.length; i++) {
            ImageView imageView = new ImageView(this);

            imageView.setLayoutParams(new ViewGroup.MarginLayoutParams(
                    width, ViewGroup.MarginLayoutParams.MATCH_PARENT));
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
                    Intent intent = new Intent(TeacherDetailActivity.this, GalleryActivity.class);
                    intent.putExtra(GalleryActivity.GALLERY_URLS, mTeacher.getPhoto_set());
                    intent.putExtra(GalleryActivity.GALLERY_CURRENT_INDEX, finalI);
                    startActivity(intent);
                }
            });

            mImageLoader.get(gallery[i], ImageLoader.getImageListener(imageView, R.drawable.ic_default_img, R.drawable.ic_default_img));
            mGallery.addView(imageView, i);
        }
    }

    private void dealRequestError(String errorCode) {
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
                break;
        }
    }

    private void changeSchoolsShow() {

        if (!isShowAllSchools){
            ivSchoolMore.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_up));
            tvSchoolMore.setText("收起");
            mSchoolAdapter.setSchools(mAllSchools);
            mSchoolAdapter.notifyDataSetChanged();
        }else{
            Double dis = mAllSchools.get(0).getDistance();
            if (dis!=null&&dis>=0) {
                tvSchoolMore.setText(String.format("离您最近的社区中心 (%s))",LocationUtil.formatDistance(dis)));
            } else {
                tvSchoolMore.setText("其他社区中心");
            }
            ivSchoolMore.setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_down));
            mSchoolAdapter.setSchools(mFirstSchool);
            mSchoolAdapter.notifyDataSetChanged();
        }
        isShowAllSchools = !isShowAllSchools;
    }

    private void signUp() {
        //判断是否登录
        if (UserManager.getInstance().isLogin()){
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
            if (resultCode == SmsAuthActivity.RESULT_CODE_LOGIN_SUCCESSED) {
                //跳转到课程购买页
                startCourseConfirmActivity();
            }
        }
    }

    //启动登录页
    private void startSmsActivityRes() {
        Intent intent = new Intent();
        intent.setClass(this, SmsAuthActivity.class);
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

    //设置上滑头像消失
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //设置头像上滑缩小消失
        int toolbarHeight = toolbar.getHeight();
        int headPortraitHeight = mRlTeacherHeadPortrait.getHeight();
        //最大上滑距离
        int maxOffset = mAppBarLayout.getHeight() - toolbarHeight;
        //头像彻底消失临界点
        int criticalPoint = headPortraitHeight * 35 / 75;
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
