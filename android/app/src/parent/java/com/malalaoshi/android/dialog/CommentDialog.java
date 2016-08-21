package com.malalaoshi.android.dialog;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.api.CommentApi;
import com.malalaoshi.android.api.PostCommentApi;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.entity.Comment;
import com.malalaoshi.android.net.Constants;
import com.malalaoshi.android.util.MiscUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kang on 16/3/2.
 */
public class CommentDialog extends DialogFragment {

    public interface OnCommentResultListener {
        void onSuccess(Comment response);
    }

    private static String ARGS_DIALOG_TEACHER_NAME = "teacher name";
    private static String ARGS_DIALOG_TEACHER_AVATAR = "teacher avatar";
    private static String ARGS_DIALOG_COURSE_NAME = "course name";
    private static String ARGS_DIALOG_COMMENT = "comment";
    private static String ARGS_DIALOG_TIMESLOT = "timeslot";

    private String teacherName;
    private String teacherAvatarUrl;
    private String courseName;
    private Long timeslot;
    private Comment comment;
    private boolean commentSuccess;
    private OnCommentResultListener resutListener;

    @Bind(R.id.iv_teacher_avater)
    MalaImageView teacherAvater;

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

    public static CommentDialog newInstance(String teacherName, String teacherAvatarUrl, String courseName,
                                            Long timeslot, Comment comment) {
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
        teacherName = getArguments().getString(ARGS_DIALOG_TEACHER_NAME, "");
        teacherAvatarUrl = getArguments().getString(ARGS_DIALOG_TEACHER_AVATAR, "");
        courseName = getArguments().getString(ARGS_DIALOG_COURSE_NAME, "");
        comment = getArguments().getParcelable(ARGS_DIALOG_COMMENT);
        timeslot = getArguments().getLong(ARGS_DIALOG_TIMESLOT, 0L);
        init();
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    private void init() {
    }

    public void SetOnCommentResultListener(OnCommentResultListener listener) {
        resutListener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        final View rootView = view;//view.findViewById(R.id.ll_dialog);
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            int lastY = 0;

            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                rootView.getLocationOnScreen(location);
                if (lastY == 0) {
                    lastY = location[1];
                }
                int diff = location[1] - lastY;
                Log.i("CommentDialog11", "layout height:" + diff + " x" + location[0] + " y" + location[1]);
                if (diff > 0) {
                    setCommentLayout(false);
                } else if (diff < 0) {
                    setCommentLayout(true);
                }
                lastY = location[1];
            }
        });
        //tvSubmit.setEnabled(false);
        //initDatas();
        return view;
    }


    private void initViews() {
        //查看课程评价
        if (comment != null) {  //已评价
            StatReporter.commentPage(true);
            //不需要下载
            updateLoadSuccessedUI();
            //查看课程评价
            updateUI(comment);
            //updateLoadingUI();
            //控件不可编辑
            editComment.setFocusableInTouchMode(false);
            editComment.setCursorVisible(false);

            ratingbar.setIsIndicator(true);
        } else {
            StatReporter.commentPage(false);
            this.setCancelable(false);          // 设置点击屏幕Dialog不消失
            tvSubmit.setEnabled(false);
            //评价课程
            llContent.setVisibility(View.VISIBLE);
            llLoading.setVisibility(View.GONE);
            llLoadFail.setVisibility(View.GONE);
            //可以编辑
            editComment.setFocusableInTouchMode(true);
            editComment.setCursorVisible(true);

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
        teacherAvater.loadCircleImage(teacherAvatarUrl, R.drawable.ic_default_teacher_avatar);
        tvTeacherName.setText(teacherName);
        tvCourse.setText(courseName);
    }

    private void checkSubmitButtonStatus() {
        boolean status = editComment.getText().length() > 0 && ratingbar.getRating() > 0;
        if (!status) {
            tvSubmit.setEnabled(false);
        } else {
            tvSubmit.setEnabled(true);
        }
    }

    private void initDatas() {
        if (comment != null) {
            //开始下载
            loadData();
        }
    }

    private static final class FetchCommentRequest extends BaseApiContext<CommentDialog, Comment> {

        private long commentId;

        public FetchCommentRequest(CommentDialog commentDialog, long commnetId) {
            super(commentDialog);
            this.commentId = commnetId;
        }

        @Override
        public Comment request() throws Exception {
            return new CommentApi().get(commentId);
        }

        @Override
        public void onApiSuccess(@NonNull Comment comment) {
            get().updateUI(comment);
            get().updateLoadSuccessedUI();
        }
    }

    private void loadData() {
        //TODO 重构前参数也是空，为什么总为空？
        if (comment != null) {
            ApiExecutor.exec(new FetchCommentRequest(this, comment.getId()));
        }
    }

    private void updateUI(Comment comment) {
        if (comment != null) {
            ratingbar.setRating(comment.getScore());
            editComment.setText(comment.getContent());
        } else {
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


    void editAnimation(int start, int end) {
        final int llY = (int) llCourse.getY();
        final int editY = (int) editComment.getY();
        final LinearLayout.LayoutParams editLinearParams = (LinearLayout.LayoutParams) editComment
                .getLayoutParams(); //取控件textView当前的布局参数
        final int editHeight = editComment.getMeasuredHeight();
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
     * @throws
     * @MethodName:closeInputMethod
     * @Description:关闭系统软键盘
     */
    public void closeInputMethod() {
        try {
            ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(getDialog().getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
        } finally {
        }
    }

    /**
     * @throws
     * @MethodName:openInputMethod
     * @Description:打开系统软键盘
     */
    public void openInputMethod(final EditText editText) {

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {

            public void run() {
                InputMethodManager inputManager = (InputMethodManager) editText.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(editText, 0);
            }
        }, 50);
    }

    public void setCommentLayout(boolean isOpen) {
        int llHeight = llCourse.getMeasuredHeight();
        if (isOpen && !isOpenInputMethod) {
            editAnimation(0, llHeight);
            //openInputMethod(editComment);
            isOpenInputMethod = !isOpenInputMethod;
        } else if (!isOpen && isOpenInputMethod) {
            editAnimation(0, -llHeight);
            //closeInputMethod();
            isOpenInputMethod = !isOpenInputMethod;
        }
    }

    @OnClick(R.id.tv_load_fail)
    public void OnClickLoadFail(View v) {
        updateLoadingUI();
        loadData();
    }

    private static final class PostCommentRequest extends BaseApiContext<CommentDialog, Comment> {

        private String body;

        public PostCommentRequest(CommentDialog commentDialog, String body) {
            super(commentDialog);
            this.body = body;
        }

        @Override
        public Comment request() throws Exception {
            return new PostCommentApi().post(body);
        }

        @Override
        public void onApiSuccess(@NonNull Comment response) {
            get().commentSucceed(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().commentFailed();
        }
    }

    @OnClick(R.id.tv_submit)
    public void onClickSubmit(View v) {
        StatReporter.commentSubmit();
        if (comment != null) {
            dismiss();
            return;
        }
        String content = editComment.getText().toString();
        float score = ratingbar.getRating();
        JSONObject json = new JSONObject();
        try {
            json.put(Constants.TIMESLOT, timeslot);
            json.put(Constants.SCORE, score);
            json.put(Constants.CONTENT, content);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        ApiExecutor.exec(new PostCommentRequest(this, json.toString()));
    }

    private void commentSucceed(Comment response) {
        //跟新课表
        commentSuccess = true;
        //EventBus.getDefault().post(new BusEvent(BusEvent.BUS_EVENT_RELOAD_TIMETABLE_DATA));
        MiscUtil.toast(R.string.comment_succeed);
        if (resutListener != null) {
            resutListener.onSuccess(response);
        }
        dismiss();
    }

    private void commentFailed() {
        MiscUtil.toast(R.string.comment_failed);
    }


    @OnClick(R.id.tv_Close)
    public void onClickClose(View v) {
        dismiss();
    }
}
