package com.malalaoshi.android.fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.util.AuthUtils;

import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/5/16.
 * 会员专享
 */

public class MemberServiceFragment extends BaseFragment implements View.OnClickListener {

    AnimationDrawable refreshAnimation = null;

    @Bind(R.id.ll_refresh_refreshing)
    protected LinearLayout llRefreshRefreshing;

    @Bind(R.id.iv_refresh_refreshing)
    protected ImageView ivRefreshRefreshing;

    @Bind(R.id.tv_refresh_refreshing)
    protected TextView tvRefreshRefreshing;

    @Bind(R.id.sub_non_learning_report)
    protected RelativeLayout rlNonLearningReport;

    @Bind(R.id.sub_learning_report)
    protected RelativeLayout rlLearningReport;

    @Bind(R.id.tv_answer_number)
    protected TextView tvAnswerNumber;

    @Bind(R.id.tv_correct_rate)
    protected TextView tvCorrectRate;

    @Bind(R.id.tv_open_learning_report)
    protected TextView tvOpenLearReport;

    @Bind(R.id.tv_report_prompt)
    protected TextView tvReportPrompt;

    @Bind(R.id.tv_report_submit)
    protected TextView tvReportSubmit;

    enum EnumReportStatus {
        LONGING, LONGFAILED, NOTSIGNIN, NOTSIGNUP, EMPTYREPORT, REPORT;
    }

    private EnumReportStatus reportStatus = EnumReportStatus.LONGING;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_service,container, false);
        ButterKnife.bind(this,view);
        initView();
        initData();
        setEvent();
        return view;
    }

    private void setEvent() {
        llRefreshRefreshing.setOnClickListener(this);
        tvReportSubmit.setOnClickListener(this);
        tvOpenLearReport.setOnClickListener(this);
    }

    private void initData() {
        loadData();
    }

    private void loadData() {
        if (!UserManager.getInstance().isLogin()){
            showNotSignInView();
        }else{
            showLoadingView();
            new HttpThread(myHandler).start();
        }
    }

    public Handler myHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshAnimation.stop();
            if (msg.arg1==0){
               showEmptyReportView();
            }else if (msg.arg1==1){
             showLoadFailedView();
            }else if (msg.arg1==2){
               showNotSignInView();
            }else if (msg.arg1==3){
                showNotSignUpView();
            } else{
                showReportView();
            }
        }
    };

    public static class HttpThread extends Thread{
        private WeakReference<Handler> handlerWeakReference;
        public HttpThread(Handler handler){
            handlerWeakReference = new WeakReference<Handler>(handler);
        }
        Message message ;
        @Override
        public void run() {
            super.run();
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int res = (int)Math.random()%4;
            message = new Message();
            if (res==0){
                message.arg1 = 0;
                handlerWeakReference.get().sendMessage(message);
            }else if (res==1){
                message.arg1 = 1;
                handlerWeakReference.get().sendMessage(message);
            }else if (res==2){
                message.arg1 = 2;
                handlerWeakReference.get().sendMessage(message);
            }else if (res==3){
                message.arg1 = 3;
                handlerWeakReference.get().sendMessage(message);
            }else{
                message.arg1 = 3;
                handlerWeakReference.get().sendMessage(message);
            }

        }
    }

    private void showLoadingView(){
        rlNonLearningReport.setVisibility(View.GONE);
        rlLearningReport.setVisibility(View.GONE);
        llRefreshRefreshing.setVisibility(View.VISIBLE);
        llRefreshRefreshing.setOnClickListener(null);
        ivRefreshRefreshing.setImageDrawable(refreshAnimation);
        refreshAnimation.start();
        tvRefreshRefreshing.setText("正在加载数据···");
        reportStatus = EnumReportStatus.LONGING;
    }

    private void showLoadFailedView(){
        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.GONE);
        rlLearningReport.setVisibility(View.GONE);
        llRefreshRefreshing.setVisibility(View.VISIBLE);
        ivRefreshRefreshing.setImageDrawable(getResources().getDrawable(R.drawable.ic_course));
        tvRefreshRefreshing.setText("加载失败,点击重试!");
        reportStatus = EnumReportStatus.LONGFAILED;
    }

    private void showNotSignInView(){
        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.VISIBLE);
        rlLearningReport.setVisibility(View.GONE);
        llRefreshRefreshing.setVisibility(View.GONE);
        tvReportPrompt.setText("登录可查看专属学习报告哦···");
        tvReportSubmit.setText("登录");
        reportStatus = EnumReportStatus.NOTSIGNIN;
    }

    private void showNotSignUpView(){
        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.VISIBLE);
        rlLearningReport.setVisibility(View.GONE);
        llRefreshRefreshing.setVisibility(View.GONE);
        tvReportPrompt.setText("您还未报名,先看看其他样本报告吧···");
        tvReportSubmit.setText("查看学习报告样本");
        reportStatus = EnumReportStatus.NOTSIGNUP;
    }

    private void showEmptyReportView(){
        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.GONE);
        rlLearningReport.setVisibility(View.VISIBLE);
        llRefreshRefreshing.setVisibility(View.GONE);
        tvReportPrompt.setText("当前科目暂未开通学习报告,敬请期待···");
        tvReportSubmit.setText("查看数学学习报告样本");
        reportStatus = EnumReportStatus.EMPTYREPORT;
    }

    private void showReportView(){
        refreshAnimation.stop();
        rlNonLearningReport.setVisibility(View.GONE);
        rlLearningReport.setVisibility(View.VISIBLE);
        llRefreshRefreshing.setVisibility(View.GONE);
        tvAnswerNumber.setText("93");
        tvCorrectRate.setText("93%");
        reportStatus = EnumReportStatus.REPORT;
    }

    private void initView() {
        refreshAnimation = (AnimationDrawable)ivRefreshRefreshing.getDrawable();
    }

    //查看学习报告
    private void openLearningReport() {
    }

    //查看学习报告样本
    private void openSampleReport() {
    }

    //登录
    private void openLoginActivity() {
        AuthUtils.redirectLoginActivity(getContext());
    }

    //重新加载
    private void reloadData() {
        loadData();
    }

    /*************************会员专享*************************/
    public void onClick(View view){
        int position = 0;
        switch (view.getId()){
            case R.id.ll_refresh_refreshing:
                if (reportStatus==EnumReportStatus.LONGFAILED){
                    reloadData();
                }
                return;
            case R.id.tv_report_submit:
                if (reportStatus==EnumReportStatus.NOTSIGNIN){
                    openLoginActivity();
                }else if (reportStatus==EnumReportStatus.NOTSIGNUP){
                    openSampleReport();
                }else if (reportStatus==EnumReportStatus.EMPTYREPORT){
                    openSampleReport();
                }
                return;
            case R.id.tv_open_learning_report:
                if (reportStatus==EnumReportStatus.REPORT){
                    openLearningReport();
                }
                return;
            case R.id.tv_with_read://自习陪读
                //position =
                break;
            case R.id.tv_learning_report://学习报告
                //position =
                break;
            case R.id.tv_counseling://心理辅导
                //position =
                break;
            case R.id.tv_lectures://特色讲座
                //position =
                break;
            case R.id.tv_exam_explain://考前串讲
                //position =
                break;
            case R.id.tv_mistake://错题本
                //position =
                break;
            case R.id.tv_spps_evaluation://SPPS测评
                //position =
                break;
            case R.id.tv_expect_more://敬请期待
                //position =
                break;
            default:
                return;
        }
        openMemberServiceAvtivity(position);
    }

    private void openMemberServiceAvtivity(int type){
       // Intent intent = new Intent(getContext(), TeacherInfoActivity.class);
        //intent.putExtra();
        //startActivity(intent);
    }

    @Override
    public String getStatName() {
        return "会员专享";
    }

}
