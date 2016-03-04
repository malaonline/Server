package com.malalaoshi.android.dialog;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.view.CircleNetworkImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kang on 16/3/2.
 */
public class CommentDialog extends DialogFragment{

    private static String ARGS_DIALOG_TEACHER_NAME   = "teacher name";
    private static String ARGS_DIALOG_TEACHER_AVATAR = "teacher avatar";
    private static String ARGS_DIALOG_COURSE_NAME    = "course name";
    private static String ARGS_DIALOG_COMMENT_ID      = "course_id";

    private String teacherName;
    private String teacherAvatarUrl;
    private String courseName;
    private String commentId;

    @Bind(R.id.iv_teacher_avater)
    CircleNetworkImage teacherAvater;

    @Bind(R.id.tv_teacher_name)
    TextView tvTeacherName;

    @Bind(R.id.tv_course)
    TextView tvCourse;

    @Bind(R.id.edit_review)
    protected EditText editComment;

    @Bind(R.id.ll_course)
    protected LinearLayout llCourse;

    @Bind(R.id.ratingbar)
    protected RatingBar ratingbar;

    @Bind(R.id.tv_submit)
    protected TextView tvSubmit;

    private boolean isOpenInputMethod = false;
    //图片缓存
    private ImageLoader mImageLoader;

    //网络请求消息队列
    private RequestQueue requestQueue;
    private String hostUrl;
    private List<String> requestQueueTags;

    public static CommentDialog newInstance(String teacherName,String teacherAvatarUrl,String courseName,String commentId) {
        CommentDialog f = new CommentDialog();
        Bundle args = new Bundle();
        args.putString(ARGS_DIALOG_TEACHER_NAME, teacherName);
        args.putString(ARGS_DIALOG_TEACHER_AVATAR, teacherAvatarUrl);
        args.putString(ARGS_DIALOG_COURSE_NAME, courseName);
        args.putString(ARGS_DIALOG_COMMENT_ID, commentId);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        teacherName = getArguments().getString(ARGS_DIALOG_TEACHER_NAME,"");
        teacherAvatarUrl = getArguments().getString(ARGS_DIALOG_TEACHER_AVATAR,"");
        courseName = getArguments().getString(ARGS_DIALOG_COURSE_NAME,"");
        commentId = getArguments().getString(ARGS_DIALOG_COMMENT_ID,"");
        init();
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    private void init() {
        requestQueueTags = new ArrayList<String>();
        requestQueue = MalaApplication.getHttpRequestQueue();
        hostUrl = MalaApplication.getInstance().getMalaHost();
        mImageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_comment, container, false);
        ButterKnife.bind(this, view);
        initViews();
        tvSubmit.setEnabled(false);
        initDatas();
        return view;
    }

    private void initViews() {
        if (commentId!=null&&!commentId.isEmpty()){
            //查看课程
            editComment.setEnabled(false);
            ratingbar.setIsIndicator(true);
            tvSubmit.setEnabled(false);
        }else{
            //评价课程
            editComment.setEnabled(true);
            ratingbar.setIsIndicator(false);
        }
        //
        if (teacherAvatarUrl != null && !teacherAvatarUrl.equals("")) {
            teacherAvater.setDefaultImageResId(R.drawable.user_detail_header_bg);
            teacherAvater.setErrorImageResId(R.drawable.user_detail_header_bg);
            teacherAvater.setImageUrl(teacherAvatarUrl, mImageLoader);
        }
        tvTeacherName.setText(teacherName);
        tvCourse.setText(courseName);
    }

    private void initDatas() {
        loadDatas();
    }

    private void loadDatas() {
        if (commentId!=null&&!commentId.isEmpty()){
            //开始下载
        }
    }


    void editAnimation(int start, int end){
        final int llY = (int) llCourse.getY();
        final int editY = (int) editComment.getY();
        final LinearLayout.LayoutParams editLinearParams =(LinearLayout.LayoutParams) editComment.getLayoutParams(); //取控件textView当前的布局参数
        final int editHeight =  editComment.getMeasuredHeight();
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.setDuration(100).start();
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                llCourse.setY(llY - value);
                editLinearParams.height = editHeight + value;
                editComment.setLayoutParams(editLinearParams);
                editComment.setY(editY - value);
            }
        });
    }

    /**
     * @MethodName:closeInputMethod
     * @Description:关闭系统软键盘
     * @throws
     */
    public void closeInputMethod(){
        try {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getDialog().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) { }finally{ }
    }

    /**
     * @MethodName:openInputMethod
     * @Description:打开系统软键盘
     * @throws
     */
    public void openInputMethod(final EditText editText){

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputManager = (InputMethodManager) editText
                        .getContext().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 50);
    }


    @OnClick(R.id.edit_review)
    public void onClickCommentEdit(View v){
        int llHeight = llCourse.getMeasuredHeight();
        if (!isOpenInputMethod) {
            editAnimation(0, llHeight);
            openInputMethod(editComment);
        } else {
            editAnimation(0, -llHeight);
            closeInputMethod();
        }
        isOpenInputMethod = !isOpenInputMethod;
    }

    @OnClick(R.id.tv_submit)
    public void onClickSubmit(View v){

    }

    @OnClick(R.id.tv_Close)
    public void onClickClose(View v){
        dismiss();
    }
}
