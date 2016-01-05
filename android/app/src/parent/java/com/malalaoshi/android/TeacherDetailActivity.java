package com.malalaoshi.android;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
import com.malalaoshi.android.base.StatusBarActivity;
import com.malalaoshi.android.entity.CoursePrice;
import com.malalaoshi.android.entity.HighScore;
import com.malalaoshi.android.entity.MemberService;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.fragments.LoginFragment;
import com.malalaoshi.android.listener.NavigationFinishClickListener;
import com.malalaoshi.android.result.MemberServiceListResult;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.JsonUtil;
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
public class TeacherDetailActivity extends StatusBarActivity implements View.OnClickListener, CoursePriceAdapter.OnClickItem, AppBarLayout.OnOffsetChangedListener {
    private static final String TAG = "TeacherDetailActivity";

    private static final String EXTRA_TEACHER_ID = "teacherId";
    //教师id
    private Long mTeacherId;

    //接口地址
    private static final String TEACHING_ENVIRONMENT_PATH_V1 = "/api/v1/teahingenvironment/";
    private static final String MEMBERSERVICES_PATH_V1 = "/api/v1/memberservices/";
    private static final String TEACHERS_PATH_V1 = "/api/v1/teachers/";


    //会员服务请求结果
    private MemberServiceListResult mMemberServicesResult;

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

    //头像
    @Bind(R.id.parent_teacher_detail_head_portrait)
    protected CircleImageView mHeadPortrait;

    //教师姓名
    @Bind(R.id.parent_teacher_detail_name_tv)
    protected TextView mTeacherName;

    //教师性别
    @Bind(R.id.parent_teacher_detail_gender_iv)
    protected ImageView mTeacherGender;

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
    @Bind(R.id.parent_teacher_detail_gallery_more_tv)
    protected TextView mMoreGallery;

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
    @Bind(R.id.ll_teaching_environment)
    protected LinearLayout llTeachingEnviroment;

    //教学环境照片
    @Bind(R.id.iv_teaching_environment)
    protected ImageView ivTeachingEnvironment;

    //体验中心名称
    @Bind(R.id.tv_experience_center)
    protected TextView tvExperienceCenterName;

    //体验中心地址
    @Bind(R.id.tv_experience_center_address)
    protected TextView tvExperienceCenterAddress;

    //距离体验中心的距离
    @Bind(R.id.tv_experience_center_distance)
    protected TextView tvExperienceCenterDistance;

    //其它学习中心
    @Bind(R.id.ll_training_center)
    protected LinearLayout llTrainingCenter;

    @Bind(R.id.tv_training_center)
    protected TextView tvTrainingCenter;

    //其它学习中列表
    @Bind(R.id.lv_training_center)
    protected ListView trainingCenterList;

    //
    private Drawable mUpIcon;
    private Drawable mDownIcon;

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
    }

    private void setEvent() {
        mMoreGallery.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
        llTrainingCenter.setOnClickListener(this);
    }

    //初始化定位
    private void initLocation() {
        //定位
    }

    //定位后请求教学环境信息
    void loadTeachingEnvironment(){
        String url = hostUrl + TEACHING_ENVIRONMENT_PATH_V1;
        StringRequest jstringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Type listType = new TypeToken<ArrayList<MemberService>>(){}.getType();
                //mMemberServicesResult = JsonUtil.parseData(R.raw.membersiver, MemberServiceListResult.class, TeacherDetailActivity.this);
                //mMemberServicesResult = JsonUtil.parseStringData(response, MemberServiceListResult.class);
                updateUITeachingEnvironment();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
            }
        });
        addRequestQueue(jstringRequest, TEACHING_ENVIRONMENT_PATH_V1);
    }

    private void initData() {
        Intent intent = getIntent();
        mTeacherId = intent.getLongExtra(EXTRA_TEACHER_ID, 0);
        requestQueueTags = new ArrayList<String>();
        requestQueue = MalaApplication.getHttpRequestQueue();
        hostUrl = MalaApplication.getInstance().getMalaHost();
        mImageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
        mUpIcon = getResources().getDrawable(R.drawable.ic_close);
        mUpIcon.setBounds(0, 0, mUpIcon.getMinimumWidth(), mUpIcon.getMinimumHeight());
        mDownIcon = getResources().getDrawable(R.drawable.back);
        mDownIcon.setBounds(0, 0, mDownIcon.getMinimumWidth(), mDownIcon.getMinimumHeight());

        loadTeacherInfo();
        loadMemeberServices();
    }


    private void loadTeacherInfo() {
        String url = hostUrl + TEACHERS_PATH_V1 + mTeacherId + "/";
        StringRequest jstringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mTeacher = JsonUtil.parseStringData(response, Teacher.class);
                updateUI(mTeacher);
                //停止进度条
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dealRequestError(error.getMessage());
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
                //停止进度条
            }
        });
        addRequestQueue(jstringRequest,TEACHERS_PATH_V1);
    }

    //向请求队列添加请求
    public void addRequestQueue(StringRequest stringRequest, String requestTag){
        requestQueueTags.add(requestTag);
        stringRequest.setTag(requestTag);
        requestQueue.add(stringRequest);
    }

    //取消说有网络请求
    public void cancelAllRequestQueue(){
        for (int i=0; requestQueue!=null&&i< requestQueueTags.size();i++){
            requestQueue.cancelAll(requestQueueTags.get(i));
        }
    }


    private void loadMemeberServices() {
        //String url = hostUrl +MEMBERSERVICES_PATH_V1;
        String url = hostUrl + MEMBERSERVICES_PATH_V1;
        StringRequest jstringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Type listType = new TypeToken<ArrayList<MemberService>>(){}.getType();
                //mMemberServicesResult = JsonUtil.parseData(R.raw.membersiver, MemberServiceListResult.class, TeacherDetailActivity.this);
                mMemberServicesResult = JsonUtil.parseStringData(response, MemberServiceListResult.class);
                updateUIServices(mMemberServicesResult.getResults());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
            }
        });

        addRequestQueue(jstringRequest,MEMBERSERVICES_PATH_V1);
    }

    //更新教学环境UI
    private void updateUITeachingEnvironment() {
        //
        //教学环境
        llTeachingEnviroment.setVisibility(View.VISIBLE);
        //教学环境照片
        String string = "";
        mImageLoader.get(string != null ? string : "", ImageLoader.getImageListener(ivTeachingEnvironment, R.drawable.user_detail_header_bg, R.drawable.user_detail_header_bg));
        //体验中心名称
        tvExperienceCenterName.setText(string);
        //体验中心地址
        tvExperienceCenterAddress.setText(string);
        //距离体验中心的距离
        tvExperienceCenterDistance.setText(string);

        //其它学习中心
        llTrainingCenter.setVisibility(View.VISIBLE);
        tvTrainingCenter.setText(string);
        //其它学习中列表
        //trainingCenterList.setAdapter("");
        //trainingCenterList.setVisibility(View.GONE);
    }

    //跟新会员服务接口
    private void updateUIServices(List<MemberService> mMemberServices) {
        if (mMemberServices != null && mMemberServices.size() > 0) {
            mMemberServiceLayout.setVisibility(View.VISIBLE);
            mMemberServiceTv.setText(StringUtil.joinEntityName(mMemberServices));
        }
    }

    //跟新教师详情
    private void updateUI(Teacher teacher) {
        if (teacher != null) {
            String string;
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
                mTeacherGender.setImageDrawable(getResources().getDrawable(R.drawable.user_detail_header_bg));
            } else if (ch != null && ch == 'w') {
                mTeacherGender.setImageDrawable(getResources().getDrawable(R.drawable.user_detail_header_bg));
            } else {
                mTeacherGender.setVisibility(View.GONE);
            }

            //教学科目
            String spot = " | ";
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
            setListViewHeightBasedOnChildren(mHighScoreList);
            //个人相册
            loadGallery(mTeacher.getPhoto_set());
            //特殊成就
            String[] strCers = teacher.getCertificate_set();
            mCertificate.setText(StringUtil.join(strCers, spot));

            //教龄级别
            Integer age = teacher.getTeaching_age();
            String level = teacher.getLevel();
            if (age != null) {
                if (level != null && level != null) {
                    mTeacherLevel.setText(age + spot + level);
                } else {
                    mTeacherLevel.setText(age + "");
                }

            } else if (age == null && level != null && level != null) {
                if (level != null && level != null) {
                    mTeacherLevel.setText(level);
                } else {
                    mTeacherLevel.setText("");
                }
            }

            //价格表
            List<CoursePrice> coursePrices = teacher.getPrices();
            CoursePriceAdapter coursePriceAdapter = new CoursePriceAdapter(this, coursePrices != null ? coursePrices : (new ArrayList<CoursePrice>()));
            //添加按钮监听事件
            coursePriceAdapter.setOnClickItem(this);
            mCoursePriceList.setAdapter(coursePriceAdapter);
            setListViewHeightBasedOnChildren(mCoursePriceList);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parent_teacher_signup_btn:
                //将界面移动到最底端
                mAppBarLayout.setExpanded(false);
                mNestedScrollViewContent.fullScroll(ScrollView.FOCUS_DOWN);
                break;
            case R.id.parent_teacher_detail_gallery_more_tv:
                //查看更多照片
                break;
            case R.id.ll_training_center:
                //显示/隐藏其它学习中心列表
                int visible = trainingCenterList.getVisibility();
                if (visible==View.GONE){
                    tvTrainingCenter.setCompoundDrawables(null, null, mDownIcon, null);
                    trainingCenterList.setVisibility(View.VISIBLE);

                }else {
                    tvTrainingCenter.setCompoundDrawables(null, null, mUpIcon, null);
                    trainingCenterList.setVisibility(View.GONE);
                }
                break;
        }
    }

    //设置listview的高度
    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    //报名
    @Override
    public void onClickItem(int position, Long gradeId) {


    }

    //设置上滑头像消失
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        //设置上滑头像消失
        int toolbarHeight = toolbar.getHeight();
        int headPortraitHeight = mHeadPortrait.getHeight();
        //最大上滑距离
        int maxOffset = mAppBarLayout.getHeight()-toolbarHeight;
        //头像彻底消失临界点
        int criticalPoint = toolbarHeight>(headPortraitHeight/2)?toolbarHeight:headPortraitHeight/2-50;
        int len = (maxOffset+verticalOffset)-criticalPoint;
        if (len<=0){
            mHeadPortrait.setVisibility(View.GONE);
        }else if (len>0&&len<50){
            float ratio = (float) (len) / (float) 50;
            mHeadPortrait.setVisibility(View.VISIBLE);
            mHeadPortrait.setAlpha(ratio);

        }else{
            mHeadPortrait.setVisibility(View.VISIBLE);
            mHeadPortrait.setAlpha(1.0f);
        }

    }
}
