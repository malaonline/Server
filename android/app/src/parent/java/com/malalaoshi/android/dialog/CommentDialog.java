package com.malalaoshi.android.dialog;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.entity.Comment;
import com.malalaoshi.android.event.BusEvent;
import com.malalaoshi.android.net.Constants;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.JsonUtil;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.view.CircleNetworkImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by kang on 16/3/2.
 */
public class CommentDialog extends DialogFragment{

    private static String ARGS_DIALOG_TEACHER_NAME   = "teacher name";
    private static String ARGS_DIALOG_TEACHER_AVATAR = "teacher avatar";
    private static String ARGS_DIALOG_COURSE_NAME    = "course name";
    private static String ARGS_DIALOG_COMMENT        = "comment";
    private static String ARGS_DIALOG_TIMESLOT       = "timeslot";

    private String teacherName;
    private String teacherAvatarUrl;
    private String courseName;
    private Long timeslot;
    private Comment comment;

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

    @Bind(R.id.ll_load_fail)
    protected LinearLayout llLoadFail;

    @Bind(R.id.ll_loading)
    protected LinearLayout llLoading;

    @Bind(R.id.ll_content)
    protected LinearLayout llContent;

    private boolean isOpenInputMethod = false;
    //图片缓存
    private ImageLoader mImageLoader;

    public static CommentDialog newInstance(String teacherName,String teacherAvatarUrl,String courseName, Long timeslot, Comment comment) {
        CommentDialog f = new CommentDialog();
        Bundle args = new Bundle();
        args.putString(ARGS_DIALOG_TEACHER_NAME, teacherName);
        args.putString(ARGS_DIALOG_TEACHER_AVATAR, teacherAvatarUrl);
        args.putString(ARGS_DIALOG_COURSE_NAME, courseName);
        args.putParcelable(ARGS_DIALOG_COMMENT, comment);
        args.putLong(ARGS_DIALOG_TIMESLOT, timeslot);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.setCancelable(false);          // 设置点击屏幕Dialog不消失
        teacherName = getArguments().getString(ARGS_DIALOG_TEACHER_NAME,"");
        teacherAvatarUrl = getArguments().getString(ARGS_DIALOG_TEACHER_AVATAR,"");
        courseName = getArguments().getString(ARGS_DIALOG_COURSE_NAME,"");
        comment = getArguments().getParcelable(ARGS_DIALOG_COMMENT);
        timeslot = getArguments().getLong(ARGS_DIALOG_TIMESLOT, 0L);
        init();
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    private void init() {
        mImageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent arg2) {
                // TODO Auto-generated method stub 返回键关闭dialog
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        View view = inflater.inflate(R.layout.dialog_comment, container, false);
        ButterKnife.bind(this, view);
        initViews();
        //tvSubmit.setEnabled(false);
        //initDatas();
        return view;
    }


    private void initViews() {
        //查看课程评价
        if (comment!=null){  //已评价
            //不需要下载
            updateLoadSuccessedUI();
            //查看课程评价
            updateUI(comment);
            //updateLoadingUI();
            //控件不可编辑
            editComment.setEnabled(false);
            ratingbar.setIsIndicator(true);
        }else{
            this.setCancelable(false);          // 设置点击屏幕Dialog不消失
            tvSubmit.setEnabled(false);
            //评价课程
            llContent.setVisibility(View.VISIBLE);
            llLoading.setVisibility(View.GONE);
            llLoadFail.setVisibility(View.GONE);
            editComment.setEnabled(true);
            ratingbar.setIsIndicator(false);

            editComment.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    checkSubmitButtonStatus();

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    checkSubmitButtonStatus();
                }
            });

        }
        //初始化控件
        mImageLoader.get(teacherAvatarUrl != null ? teacherAvatarUrl : "", ImageLoader.getImageListener(teacherAvater, R.drawable.ic_default_teacher_avatar, R.drawable.ic_default_teacher_avatar));
        tvTeacherName.setText(teacherName);
        tvCourse.setText(courseName);
    }

    private void checkSubmitButtonStatus() {
        boolean status = editComment.getText().length() > 0 && ratingbar.getRating() > 0;
        if (status != true) {
            tvSubmit.setEnabled(false);
        }else{
            tvSubmit.setEnabled(true);
        }
    }

    private void initDatas() {
        if (comment!=null){
            //开始下载
            loadDatas();
        }
    }

    private void loadDatas() {

        NetworkSender.getComment("", new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                Comment comment = JsonUtil.parseStringData(json.toString(), Comment.class);
                updateUI(comment);
                updateLoadSuccessedUI();
            }

            @Override
            public void onFailed(VolleyError error) {
                updateLoadFailedUI();
            }
        });
    }

    private void updateUI(Comment comment) {
        if (comment!=null){
            ratingbar.setRating(comment.getScore());
            editComment.setText(comment.getContent());
        }else{
            ratingbar.setRating(0);
            editComment.setText("");
        }
    }

    private void updateLoadingUI() {
        llLoading.setVisibility(View.VISIBLE);
        llContent.setVisibility(View.GONE);
        llLoadFail.setVisibility(View.GONE);
        tvSubmit.setEnabled(false);
    }

    private void updateLoadSuccessedUI() {
        llLoading.setVisibility(View.GONE);
        llContent.setVisibility(View.VISIBLE);
        llLoadFail.setVisibility(View.GONE);
        //tvSubmit.setEnabled(false);
        tvSubmit.setText("知道了");
    }

    private void updateLoadFailedUI() {
        llLoading.setVisibility(View.GONE);
        llContent.setVisibility(View.GONE);
        llLoadFail.setVisibility(View.VISIBLE);
        tvSubmit.setEnabled(false);
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

    @OnClick(R.id.tv_load_fail)
    public void OnClickLoadFail(View v){
        updateLoadingUI();
        loadDatas();
    }

    @OnClick(R.id.tv_submit)
    public void onClickSubmit(View v){
        if(comment!=null){
            dismiss();;
            return;
        }
        String content = editComment.getText().toString();
        float scorce = ratingbar.getRating();
        if (content==null){
            content = "";
        }
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.TIMESLOT, timeslot);
            json.put(Constants.SCORE, scorce);
            json.put(Constants.CONTENT, content);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        NetworkSender.submitComment(json, new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                try {
                    Comment  comment = JsonUtil.parseStringData(json.toString(),Comment.class);
                    if (comment!=null) {
                        Log.i("CommentDialog", "Set student's name succeed : " + json.toString());
                        commentSucceed();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                commentFailed();
            }

            @Override
            public void onFailed(VolleyError error) {
                commentFailed();
            }
        });
    }

    private void commentSucceed() {
        //跟新课表
        EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_TIMETABLE_DATA));
        MiscUtil.toast(R.string.comment_succeed);
        dismiss();
    }

    private void commentFailed() {
        MiscUtil.toast(R.string.comment_failed);
    }


    @OnClick(R.id.tv_Close)
    public void onClickClose(View v){
        dismiss();
    }
}
