package com.malalaoshi.android;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import com.malalaoshi.android.entity.GCoursePrice;
import com.malalaoshi.android.entity.GGrade;
import com.malalaoshi.android.entity.GLevel;
import com.malalaoshi.android.entity.GMemberService;
import com.malalaoshi.android.entity.GSubject;
import com.malalaoshi.android.entity.GTag;
import com.malalaoshi.android.entity.GTeacher;
import com.malalaoshi.android.fragments.LoginFragment;
import com.malalaoshi.android.listener.NavigationFinishClickListener;
import com.malalaoshi.android.result.MemberServiceListResult;
import com.malalaoshi.android.result.TagListResult;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.JsonUtil;
import com.malalaoshi.android.util.ThemeUtils;
import com.malalaoshi.android.view.CircleImageView;
import com.malalaoshi.android.view.FlowLayout;
import java.util.List;

/**
 * Created by zl on 15/11/30.
 */
public class TeacherDetailActivity extends StatusBarActivity implements View.OnClickListener, CoursePriceAdapter.OnClickItem {
    private static final String EXTRA_TEACHER_ID = "teacherId";
    //
    private String mTeacherId;

    private static final String TAGS_PATH_V1 = "/api/v1/tags/";
    private static final String MEMBERSERVICES_PATH_V1 = "/api/v1/memberservices/";
    private static final String TEACHERS_PATH_V1 = "/api/v1/teachers/";

    //
    private List<GTag> mTags;
    private MemberServiceListResult mMemberServicesResult;
    private GTeacher mTeacher;

    private ImageLoader mImageLoader;

    private RequestQueue requestQueue;
    private String hostUrl;

    @Bind(R.id.parent_teacher_detail_toolbar)
    protected Toolbar toolbar;

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
    @Bind(R.id.parent_teacher_signup_tv)
    protected TextView mSignUp;

    public static void open(Context context, String teacherId) {
        if (teacherId!=null){
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

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ThemeUtils.setMargins(toolbar, 0, ThemeUtils.getStatusBarHeight(this), 0, 0);
        }else{
            ThemeUtils.setMargins(toolbar, 0, ThemeUtils.getStatusBarHeight(this)/2, 0, 5);
        }

        toolbar.setNavigationOnClickListener(new NavigationFinishClickListener(this));

        //初始化数据
        initData();
        setEvent();
    }


    private void setEvent() {
        mMoreGallery.setOnClickListener(this);
        mSignUp.setOnClickListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        mTeacherId = intent.getStringExtra(EXTRA_TEACHER_ID);
        requestQueue = MalaApplication.getHttpRequestQueue();
        hostUrl =  MalaApplication.getInstance().getMalaHost();
        mTags = MalaApplication.getInstance().getTags();
        mImageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
        //没有标签时去下载标签
        if (mTags==null){
            loadTags();
        }
        loadTeacherInfo();
        loadMemeberServices();
    }


    private void loadTeacherInfo() {
        String url = hostUrl +TEACHERS_PATH_V1 + mTeacherId + "/";
        StringRequest jstringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //mTeacher = JsonUtil.parseData(R.raw.teacher, GTeacher.class, TeacherDetailActivity.this);
                mTeacher = JsonUtil.parseStringData(response, GTeacher.class);
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

        jstringRequest.setTag(TEACHERS_PATH_V1);
        requestQueue.add(jstringRequest);
    }


    private void loadTags() {
        //String url = hostUrl +TAGS_PATH_V1;
        String url = hostUrl +TEACHERS_PATH_V1 + mTeacherId + "/";
        StringRequest jstringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Type listType = new TypeToken<ArrayList<GTag>>(){}.getType();
                TagListResult tagsResult;
                //tagsResult = JsonUtil.parseData(R.raw.tags, TagListResult.class, TeacherDetailActivity.this);
                tagsResult = JsonUtil.parseStringData(response, TagListResult.class);
                mTags = tagsResult.getResults();
                MalaApplication.getInstance().setTags(mTags);
                updateUITags();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dealRequestError(error.getMessage());
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
            }
        });

        jstringRequest.setTag(TAGS_PATH_V1);
        requestQueue.add(jstringRequest);
    }

    private void loadMemeberServices() {
        //String url = hostUrl +MEMBERSERVICES_PATH_V1;
        String url = hostUrl +TEACHERS_PATH_V1 + mTeacherId + "/";
        StringRequest jstringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //Type listType = new TypeToken<ArrayList<GMemberService>>(){}.getType();
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

        jstringRequest.setTag(MEMBERSERVICES_PATH_V1);
        requestQueue.add(jstringRequest);
    }

    private void updateUITags() {
        if (mTags!=null&&mTeacher!=null){
            String spot = " | ";
            StringBuilder stringBuilder = new StringBuilder();
            Long[] tagId = mTeacher.getTags();
            if (tagId!=null){
                for (int i=0;tagId!=null&&i<tagId.length;i++){
                    GTag tag = GTag.findTagById(tagId[i],mTags);
                    if (tag!=null){
                        stringBuilder.append(tag.getName() + spot);
                    }
                }
                stringBuilder.delete(stringBuilder.lastIndexOf(spot),stringBuilder.length()-1);
                mTagLayout.setVisibility(View.VISIBLE);
                mTeachingTags.setText(stringBuilder.toString());
            }


        }
    }

    private void updateUIServices(List<GMemberService> mMemberServices) {
        if (mMemberServices!=null&&mMemberServices.size()>0){
            StringBuilder stringBuilder = new StringBuilder();
            String spot = " | ";
            for(GMemberService service:mMemberServices){
                stringBuilder.append(service.getName()+ spot);
            }
            stringBuilder.delete(stringBuilder.lastIndexOf(spot),stringBuilder.length()-1);
            mMemberServiceLayout.setVisibility(View.VISIBLE);
            mMemberServiceTv.setText(stringBuilder.toString());
        }
    }

    private void updateUI(GTeacher teacher){
        if (teacher!=null){
            String string;
            //姓名
            string = teacher.getName();
            if (string!=null){
                mTeacherName.setText(string);
            }

            //性别
            mImageLoader.get(teacher.getAvatar(), ImageLoader.getImageListener(mHeadPortrait, R.drawable.user_detail_header_bg, R.drawable.user_detail_header_bg));
            string = teacher.getGender();
            if (string!=null&&teacher.getGender().equals("m")){
                mTeacherGender.setImageDrawable(getResources().getDrawable(R.drawable.user_detail_header_bg));
            }else if (string!=null&&teacher.getGender().equals("w")){
                mTeacherGender.setImageDrawable(getResources().getDrawable(R.drawable.user_detail_header_bg));
            }else{
                mTeacherGender.setVisibility(View.GONE);
            }

            //教学科目
            String spot = " | " ;
            StringBuilder strSubject = new StringBuilder();
            Long subjectId = mTeacher.getSubject();
            GSubject subject = GSubject.getSubjectById(subjectId);
            String subjectName = "";
            if (subject!=null){
                subjectName = subject.getName();
            }

            //年级
            Long[] grades = teacher.getGrades();
            if (grades!=null){
                for (int i=0;i<grades.length;i++){
                    Long gradeId = grades[i];
                    GGrade grade = GGrade.getGradeById(gradeId);
                    if (grade!=null) {
                        strSubject.append(grade.getName() + subjectName + spot);
                    }
                }
                strSubject.delete(strSubject.lastIndexOf(spot),strSubject.length()-1);
            }
            mTeacherSubject.setText(strSubject.toString());

            //分格标签
            if (mTags!=null){
                updateUITags();
            }

            //提分榜
            HighScoreAdapter highScoreAdapter = new HighScoreAdapter(this,mTeacher.getHighscore_set());
            mHighScoreList.setAdapter(highScoreAdapter);
            setListViewHeightBasedOnChildren(mHighScoreList);
            //个人相册
            loadGallery(mTeacher.getGallery());
            //特殊成就
            StringBuilder strCertificate = new StringBuilder();
            if (teacher.getCertificate()!=null){
                for (int i=0;i<teacher.getCertificate().length;i++){
                    strCertificate.append(teacher.getCertificate()[i]+spot);
                }
                strCertificate.delete(strCertificate.lastIndexOf(spot), strCertificate.length() - 1);
            }

            mCertificate.setText(strCertificate.toString());

            //教龄级别
            Long age = teacher.getTeaching_age();
            GLevel level = teacher.getLevel();
            if (age!=null){
                if (level!=null&&level.getName()!=null){
                    mTeacherLevel.setText(age+spot+ teacher.getLevel().getName());
                }else{
                    mTeacherLevel.setText(age+"");
                }

            }else if (age==null&&level!=null&&level.getName()!=null){
                if (level!=null&&level.getName()!=null){
                    mTeacherLevel.setText(teacher.getLevel().getName());
                }else{
                    mTeacherLevel.setText("");
                }
            }

            //价格表
            List<GCoursePrice> coursePrices = teacher.getPrices();
            CoursePriceAdapter coursePriceAdapter = new CoursePriceAdapter(this,coursePrices);
            //添加按钮监听事件
            coursePriceAdapter.setOnClickItem(this);
            mCoursePriceList.setAdapter(coursePriceAdapter);
            setListViewHeightBasedOnChildren(mCoursePriceList);
        }
    }


    void loadGallery(String[] gallery){

        mGallery.removeAllViews();
        int width = mGallery.getWidth()/3;
        for (int i = 0; gallery!=null&&i < gallery.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new ViewGroup.MarginLayoutParams(
                    width, width));
            imageView.setPadding(5, 5, 5, 5);
            mImageLoader.get(gallery[i], ImageLoader.getImageListener(imageView, R.drawable.user_detail_header_bg, R.drawable.user_detail_header_bg));
            mGallery.addView(imageView, i);
        }
    }

    private void dealRequestError(String errorCode){
        Toast.makeText(this,"网络请求失败!",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_item_signup:
                //将界面移动到最底端
                break;
            case R.id.parent_teacher_detail_gallery_more_tv:
                //查看更多照片
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //volley联动,取消请求
        requestQueue.cancelAll(TAGS_PATH_V1);
        requestQueue.cancelAll(MEMBERSERVICES_PATH_V1);
        requestQueue.cancelAll(TEACHERS_PATH_V1);
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
        params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    @Override
    public void onClickItem(int position, Long gradeId) {


    }
}
