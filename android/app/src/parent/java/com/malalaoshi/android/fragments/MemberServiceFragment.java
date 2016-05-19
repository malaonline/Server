package com.malalaoshi.android.fragments;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.usercenter.UserManager;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/5/16.
 * 会员专享
 */

public class MemberServiceFragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.ll_rootview)
    protected LinearLayout llRootview;

    AnimationDrawable refreshAnimation = null;

    @Bind(R.id.rl_refresh_refreshing)
    protected RelativeLayout rlRefreshRefreshing;
    @Bind(R.id.sub_non_learning_report)
    protected ViewStub subNonLearningReport;

    @Bind(R.id.sub_learning_report)
    protected ViewStub subLearningReport;

    private TextView tvAnswerNumber = null;
    private TextView tvCorrectRate = null;
    private TextView tvShowLearningReport = null;

    private boolean hasinflated = false;
    private TextView tvPrompt = null;
    private TextView tvSubmit = null;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_service,container, false);
        ButterKnife.bind(this,view);
        initView();
        initData();
        return view;
    }

    private void initData() {
        loadData();
    }

    private void loadData() {
        if (!UserManager.getInstance().isLogin()){

        }else{
            rlRefreshRefreshing.setVisibility(View.GONE);
            //updateNotSignedView();
            updateReportView();
        }
    }

    private void inflateNonLearningReport(){
        RelativeLayout layout = null;
        try {
            //如果没有被inflate过，使用inflate膨胀
            layout = (RelativeLayout) subNonLearningReport.inflate();
            tvPrompt = (TextView) layout.findViewById(R.id.tv_content);
            tvSubmit = (TextView) layout.findViewById(R.id.tv_submit);
        } catch (Exception e) {
            //如果使用inflate膨胀报错，就说明已经被膨胀过了，使用setVisibility方法显示
            subNonLearningReport.setVisibility(View.VISIBLE);
        }
        hasinflated = true;
    }

    private void inflateLearningReport(){
        RelativeLayout layout = null;
        try {
            //如果没有被inflate过，使用inflate膨胀
            layout = (RelativeLayout) subLearningReport.inflate();
            tvAnswerNumber = (TextView) layout.findViewById(R.id.tv_answer_number);
            tvCorrectRate = (TextView) layout.findViewById(R.id.tv_correct_rate);
            tvShowLearningReport = (TextView) layout.findViewById(R.id.tv_show_learning_report);
            tvShowLearningReport.setOnClickListener(this);
        } catch (Exception e) {
            //如果使用inflate膨胀报错，就说明已经被膨胀过了，使用setVisibility方法显示
            subLearningReport.setVisibility(View.VISIBLE);
        }
        hasinflated = true;
    }

    private void updateNotSignedView() {
        if (!hasinflated){
            inflateNonLearningReport();
        }
        tvPrompt.setText("登录可查看专属学习报告哦···");
        tvSubmit.setText("登录");
    }

    private void updateNotSignUpView(){
        if (!hasinflated){
            inflateNonLearningReport();
        }
        tvPrompt.setText("您还没有报名,先看看其他样本报告吧···");
        tvSubmit.setText("查看学习报告样本");
    }

    private void updateNotReportView(){
        if (!hasinflated){
            inflateNonLearningReport();
        }
        tvPrompt.setText("当前科目暂未开通学习报告,敬请期待···");
        tvSubmit.setText("查看数学学习报告样本");
    }

    private void updateReportView(){
        inflateLearningReport();
        tvAnswerNumber.setText("93");
        tvCorrectRate.setText("91%");
    }

    private void initView() {
        //refreshAnimation =  (AnimationDrawable) mHeaderChrysanthemumIv.getDrawable();
    }

    //查看学习报告
    private void showLearningReport() {
    }

    /*************************会员专享*************************/
    public void onClick(View view){
        int position = 0;
        switch (view.getId()){
            case R.id.tv_show_learning_report:
                showLearningReport();
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
        startServiceAvtivity(position);
    }

    private void startServiceAvtivity(int type){
       // Intent intent = new Intent(getContext(), TeacherInfoActivity.class);
        //intent.putExtra();
        //startActivity(intent);
    }

    @Override
    public String getStatName() {
        return "会员专享";
    }

}
