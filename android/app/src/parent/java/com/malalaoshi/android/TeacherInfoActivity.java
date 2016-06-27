package com.malalaoshi.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.malalaoshi.android.activitys.GalleryActivity;
import com.malalaoshi.android.activitys.GalleryPreviewActivity;
import com.malalaoshi.android.adapter.GralleryAdapter;
import com.malalaoshi.android.adapter.HighScoreAdapter;
import com.malalaoshi.android.adapter.SchoolAdapter;
import com.malalaoshi.android.api.SchoolListApi;
import com.malalaoshi.android.api.TeacherInfoApi;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.LoginActivity;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.course.CourseConfirmActivity;
import com.malalaoshi.android.entity.Achievement;
import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.entity.HighScore;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.listener.BounceTouchListener;
import com.malalaoshi.android.result.SchoolListResult;
import com.malalaoshi.android.util.LocManager;
import com.malalaoshi.android.util.LocationUtil;
import com.malalaoshi.android.util.Number;
import com.malalaoshi.android.view.FlowLayout;
import com.malalaoshi.android.view.ObservableScrollView;
import com.malalaoshi.android.view.RingProgressbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/3/29.
 */
public class TeacherInfoActivity extends BaseActivity implements View.OnClickListener, ObservableScrollView.ScrollViewListener, TitleBarView.OnTitleBarClickListener, AdapterView.OnItemClickListener {

    private static final String EXTRA_TEACHER_ID = "teacherId";
    private static int REQUEST_CODE_LOGIN = 1000;

    //教师id
    private Long mTeacherId;

    //所有教学中心
    private List<School> mAllSchools = null;

    //第一个显示的教学中心
    private List<School> mFirstSchool = null;

    //教师信息请求结果
    private Teacher mTeacher;

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
    protected SimpleDraweeView mHeadPortrait;

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

    //教龄
    @Bind(R.id.view_teacher_level)
    protected RingProgressbar viewTeacherLevel;

    @Bind(R.id.tv_teacher_level)
    protected TextView tvTeacherLevel;

    //级别
    @Bind(R.id.view_teacher_seniority)
    protected RingProgressbar viewTeacherSeniority;

    @Bind(R.id.tv_teacher_seniority)
    protected TextView tvTeacherSeniority;

    //教授年级
    //小学
    @Bind(R.id.rl_teach_primary)
    protected RelativeLayout rlTeachPrimary;

    @Bind(R.id.fl_teach_primary)
    protected FlowLayout flTeachPrimary;

    @Bind(R.id.view_primary_line)
    protected View viewPrimaryLine;

    //初中
    @Bind(R.id.rl_teach_junior)
    protected RelativeLayout rlTeachJunior;

    @Bind(R.id.fl_teach_junior)
    protected FlowLayout flTeachJunior;

    @Bind(R.id.view_junior_line)
    protected View viewJuniorLine;

    //高中
    @Bind(R.id.rl_teach_senior)
    protected RelativeLayout rlTeachSenior;

    @Bind(R.id.fl_teach_senior)
    protected FlowLayout flTeachSenior;

    //标签
    @Bind(R.id.parent_teacher_detail_tag_fl)
    protected FlowLayout flTags;

    //教师提分榜
    @Bind(R.id.parent_teacher_detail_highscore_listview)
    protected ListView mHighScoreList;

    @Bind(R.id.hs_grallery)
    protected HorizontalScrollView hsGrallery;

    //个人相册
    @Bind(R.id.gv_grallery)
    protected GridView gvGrallery;

    //更多相册
    @Bind(R.id.tv_gallery_more)
    protected TextView tvGalleryMore;

    private GralleryAdapter gralleryAdapter;

    //特殊成就
    @Bind(R.id.parent_teacher_detail_achievement_fl)
    protected FlowLayout mAchievement;

    //收藏老师
    @Bind(R.id.btn_collect)
    protected TextView tvCollect;

    //马上报名
    @Bind(R.id.parent_teacher_signup_btn)
    protected TextView tvSignUp;

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

    private boolean teacherInfoFlag = false;
    private boolean schoolFlag = false;

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
        //初始化数据
        initData();
        setEvent();

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


    private void stopProcess() {
        if (teacherInfoFlag && schoolFlag) {
            stopProcessDialog();
        }
    }

    private void initViews() {
        mHighScoreList.setFocusable(false);
        listviewSchool.setFocusable(false);
    }

    private void setEvent() {
        tvSignUp.setOnClickListener(this);
        llSchoolMore.setOnClickListener(this);
        tvGalleryMore.setOnClickListener(this);
        scrollView.setScrollViewListener(this);
        titleBarView.setOnTitleBarClickListener(this);
        gvGrallery.setOnItemClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        mTeacherId = intent.getLongExtra(EXTRA_TEACHER_ID, 0);
        mAllSchools = new ArrayList<>();
        mFirstSchool = new ArrayList<>();
        mSchoolAdapter = new SchoolAdapter(this);
        listviewSchool.setAdapter(mSchoolAdapter);
        gralleryAdapter = new GralleryAdapter(this);
        gvGrallery.setAdapter(gralleryAdapter);

        startProcessDialog("正在加载数据···");
        loadData();
    }

    private void loadData() {
        //老师
        loadTeacherInfo();
        //请求教学环境信息
        loadSchools();
    }

    private void loadTeacherInfo() {
        if (mTeacherId == null) {
            return;
        }
        ApiExecutor.exec(new LoadTeacherInfoRequest(this, mTeacherId));
    }

    private void loadSchools() {
        ApiExecutor.exec(new LoadSchoolListRequest(this));
    }

    private void loadSchoolListSuccess(SchoolListResult result) {
        //获取体验中心
        mAllSchools.addAll(result.getResults());
        //无数据
        if (mAllSchools.size() <= 0) {
            return;
        }

        //获取位置
        Location location = LocManager.getInstance().getLocation();
        if (location!=null) {
            //排序
            LocationUtil.sortByDistance(mAllSchools, location.getLatitude(), location.getLongitude());
            mFirstSchool.clear();
            mFirstSchool.add(mAllSchools.get(0));
            tvSchoolMore.setText(String.format("离您最近的社区中心 (%s)", LocationUtil.formatDistance(mAllSchools.get(0).getDistance())));
        } else {
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
            tvSchoolMore.setText("其他社区中心");
        }
        isShowAllSchools = false;
        mSchoolAdapter.addAll(mFirstSchool);
        mSchoolAdapter.notifyDataSetChanged();
    }


    private void loadTeacherInfoSuccess(@NonNull Teacher teacher) {
        mTeacher = teacher;
        updateUI(mTeacher);
        tvSignUp.setEnabled(true);
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
            if (!EmptyUtils.isEmpty(string)){
                mHeadPortrait.setImageURI(Uri.parse(string));
            }

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
            setGradeTeaching(grades);

            //分格标签
            String[] tags = mTeacher.getTags();
            if (tags != null && tags.length > 0) {
                setFlowDatas(flTags, tags, R.drawable.bg_text_tag, R.color.tag_text_color);
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
            Integer level = teacher.getLevel();
            if (null!=level){
                viewTeacherLevel.setProgress(level);
                tvTeacherLevel.setText("T"+level);
            }
            Integer teachAge = teacher.getTeaching_age();
            if (teachAge!=null){
                viewTeacherSeniority.setProgress(teachAge);
                tvTeacherSeniority.setText(teachAge.toString() + "年");
            }
        }
    }

    private void setGradeTeaching(String[] grades) {
        //数据处理
        int count = 0;
        List<List<String>> gradelist = Grade.getGradesByGroup(grades);

        if (gradelist!=null&&gradelist.get(0)!=null&&gradelist.get(0).size()>0){
            setFlowDatas(flTeachPrimary, (String[]) gradelist.get(0).toArray(new String[gradelist.get(0).size()]),R.drawable.bg_text_primary,R.color.primary_text_color);
        }else{
            rlTeachPrimary.setVisibility(View.GONE);
            count++;
        }

        if (gradelist!=null&&gradelist.get(1)!=null&&gradelist.get(1).size()>0){
            setFlowDatas(flTeachJunior, (String[]) gradelist.get(1).toArray(new String[gradelist.get(1).size()]),R.drawable.bg_text_junior,R.color.junior_text_color);
        }else{
            rlTeachJunior.setVisibility(View.GONE);
            count++;
        }

        if (gradelist!=null&&gradelist.get(2)!=null&&gradelist.get(2).size()>0){
            setFlowDatas(flTeachSenior, (String[]) gradelist.get(2).toArray(new String[gradelist.get(2).size()]),R.drawable.bg_text_senior,R.color.senior_text_color);
        }else{
            rlTeachSenior.setVisibility(View.GONE);
            count++;
        }

        if (count<=1){
            viewPrimaryLine.setVisibility(View.GONE);
            viewJuniorLine.setVisibility(View.GONE);
            return;
        }else if (count==2){
            if (gradelist.get(0).size()>0){
                viewJuniorLine.setVisibility(View.GONE);
                return;
            }else if (gradelist.get(2).size()>0){
                viewPrimaryLine.setVisibility(View.GONE);
                return;
            }
        }
    }


    private void setFlowCertDatas(FlowLayout flowlayout, final List<Achievement> datas, int drawable) {
        flowlayout.setFocusable(false);
        flowlayout.removeAllViews();

        for (int i = 0; datas != null && i < datas.size(); i++) {
            TextView textView = buildCertTextView(datas.get(i).getTitle(),0);
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

    private TextView buildCertTextView(String title, int drawableId) {
        TextView textView = new TextView(this);

        int leftPadding = getResources().getDimensionPixelSize(R.dimen.flow_textview_left_padding);
        int rightPadding = getResources().getDimensionPixelSize(R.dimen.flow_textview_right_padding);
        int margin = getResources().getDimensionPixelSize(R.dimen.flow_textview_left_margin);
        int height = getResources().getDimensionPixelSize(R.dimen.flow_textview_height);
        int drawablePadding = getResources().getDimensionPixelSize(R.dimen.flow_textview_spacing);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, height);

        layoutParams.setMargins(margin , margin , margin , margin);
        textView.setLayoutParams(layoutParams);
        int topPadding = textView.getPaddingTop();
        int bottomPadding = textView.getPaddingBottom();
        textView.setPadding(leftPadding,topPadding,rightPadding,bottomPadding);

        Drawable drawable= getResources().getDrawable(R.drawable.ic_certificate_icon);
        // 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        textView.setCompoundDrawables(null,null,drawable,null);
        textView.setCompoundDrawablePadding(drawablePadding);
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        textView.setTextColor(getResources().getColor(R.color.certificate_text_color));
        textView.setBackground(getResources().getDrawable(R.drawable.item_text_bg));
        return textView;
    }

    private void setFlowDatas(FlowLayout flowlayout, String[] datas, int bgDrawableId, int colorId) {
        flowlayout.setFocusable(false);
        flowlayout.removeAllViews();
        for (int i = 0; datas != null && i < datas.length; i++) {
            TextView textView = buildFlowTextView(datas[i],bgDrawableId);
            textView.setTextColor(getResources().getColor(colorId));
            flowlayout.addView(textView, i);
        }
    }

    private TextView buildFlowTextView(String data, int bgDrawableId) {
        TextView textView = new TextView(this);
        int leftPadding = getResources().getDimensionPixelSize(R.dimen.flow_textview_left_padding);
        int rightPadding = getResources().getDimensionPixelSize(R.dimen.flow_textview_right_padding);
        int margin = getResources().getDimensionPixelSize(R.dimen.flow_textview_left_margin);
        int height = getResources().getDimensionPixelSize(R.dimen.flow_textview_height);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, height);

        layoutParams.setMargins(margin , margin , margin , margin);
        textView.setLayoutParams(layoutParams);
        int topPadding = textView.getPaddingTop();
        int bottomPadding = textView.getPaddingBottom();
        textView.setPadding(leftPadding,topPadding,rightPadding,bottomPadding);

        textView.setText(data);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        textView.setTextColor(getResources().getColor(R.color.certificate_text_color));
        textView.setBackground(getResources().getDrawable(bgDrawableId));
        return textView;
    }


    void loadGallery(String[] gallery) {
        if (gallery == null || gallery.length <= 0) {
            hsGrallery.setVisibility(View.GONE);
            return;
        }
        gralleryAdapter.addAll(Arrays.asList(gallery));
        gvGrallery.setFocusable(true);
        MeasureGrallery(gvGrallery,gralleryAdapter);
        gvGrallery.setVerticalScrollBarEnabled(true);
        gvGrallery.setAdapter(gralleryAdapter);
    }

    private void MeasureGrallery(GridView gvGrallery, GralleryAdapter gralleryAdapter) {

        int childCount = gralleryAdapter.getCount();
        int gralleryWidth = getResources().getDimensionPixelSize(R.dimen.grallery_width);
        int gralleryHeight = getResources().getDimensionPixelSize(R.dimen.grallery_height);
        int gralleryHorizontalSpacing = getResources().getDimensionPixelSize(R.dimen.grallery_horizontal_spacing);
        int gridviewWidth = 0;
        if (childCount>0){
            gridviewWidth = (gralleryWidth+gralleryHorizontalSpacing)*childCount - gralleryHorizontalSpacing;
            if (childCount==1){
                gridviewWidth -= gralleryHorizontalSpacing;
            }
        }
        ViewGroup.LayoutParams params = gvGrallery.getLayoutParams();
        params.width = gridviewWidth;
        params.height = gralleryHeight;
        gvGrallery.setLayoutParams(params);   //重点
        gvGrallery.setStretchMode(GridView.NO_STRETCH);
        gvGrallery.setNumColumns(childCount);   //重点
    }

    private void requestError() {
        Toast.makeText(this, "网络请求失败!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parent_teacher_signup_btn:
                //
                signUp();
                break;
            case R.id.tv_gallery_more:
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
            mSchoolAdapter.clear();
            mSchoolAdapter.addAll(mAllSchools);
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
            mSchoolAdapter.clear();
            mSchoolAdapter.addAll(mFirstSchool);
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
        Subject subject = Subject.getSubjectIdByName(mTeacher.getSubject());
        if (mTeacher!=null&&mTeacher.getId()!=null&&subject!=null){
            CourseConfirmActivity.open(this,mTeacher.getId(),mTeacher.getName(),mTeacher.getAvatar(),subject);
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //查看更多照片
        Intent intent = new Intent(TeacherInfoActivity.this, GalleryActivity.class);
        intent.putExtra(GalleryActivity.GALLERY_URLS, mTeacher.getPhoto_set());
        intent.putExtra(GalleryActivity.GALLERY_CURRENT_INDEX, position);
        startActivity(intent);
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
            if (response!=null){
                get().loadTeacherInfoSuccess(response);
            }
        }

        @Override
        public void onApiFinished() {
            get().teacherInfoFlag = true;
            get().stopProcess();
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().requestError();
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
                get().loadSchoolListSuccess(response);
            }
        }

        @Override
        public void onApiFinished() {
            get().schoolFlag = true;
            get().stopProcess();
        }

    }

    @Override
    protected String getStatName() {
        return "老师详情页";
    }
}
