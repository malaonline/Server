package com.malalaoshi.android.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.malalaoshi.android.R;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.dialog.RadioDailog;
import com.malalaoshi.android.entity.BaseEntity;
import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.view.TitleBarView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kang on 16/1/24.
 */
public class ModifyUserSchoolActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener{
    public static int RESULT_CODE_SCHOOL = 0x003;
    public static String EXTRA_USER_GRADE = "grade";
    public static String EXTRA_USER_SCHOOL = "school";

    @Bind(R.id.titleBar)
    TitleBarView titleBar;

    @Bind(R.id.tv_user_grade)
    TextView tvUserGrade;

    @Bind(R.id.tv_user_school)
    TextView tvUserSchool;

    private Grade userGrade;
    private String userSchool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_school);
        ButterKnife.bind(this);
        titleBar.setOnTitleBarClickListener(this);
        initDatas();
    }


    private void initDatas() {
        Intent intent = getIntent();
        userGrade = intent.getParcelableExtra(EXTRA_USER_GRADE);
        if (userGrade!=null){
            tvUserGrade.setText(userGrade.getName()!=null?userGrade.getName():"");
        }
        userSchool = intent.getStringExtra(EXTRA_USER_SCHOOL);
        if (userSchool==null){
            userSchool = "";
        }
        titleBar.setTitle("学校信息");
        tvUserSchool.setText(userSchool);
    }

    @OnClick(R.id.rl_user_grade)
    public void onClickUserGrade(View view){
        int width = getResources().getDimensionPixelSize(R.dimen.filter_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.filter_dialog_height);
        ArrayList<BaseEntity> datas = new ArrayList<>();
        initGradeDatas(datas);
        RadioDailog dailog = RadioDailog.newInstance(width, height, "选择年级", datas);
        dailog.setOnOkClickListener(new RadioDailog.OnOkClickListener() {
            @Override
            public void onOkClick(View view, BaseEntity entity) {
                if (entity != null) {
                    if (userGrade==null||userGrade.getId()!=entity.getId()){
                        if (userGrade==null) userGrade = new Grade();
                        userGrade.setId(entity.getId());
                        userGrade.setName(entity.getName());
                        tvUserGrade.setText(userGrade.getName()!=null?userGrade.getName():"");
                    }
                }
            }
        });
        dailog.show(getSupportFragmentManager(), RadioDailog.class.getName());
    }

    private void initGradeDatas(ArrayList<BaseEntity> datas) {
        // 小学
        Grade primary = Grade.getGradeById(Grade.PRIMARY_ID);
        // 初中
        Grade middle = Grade.getGradeById(Grade.MIDDLE_ID);
        // 高中
        Grade senior = Grade.getGradeById(Grade.SENIOR_ID);

        BaseEntity entity = null;
        for (Grade g: Grade.gradeList) {
            if (g.getSupersetId() == null) {
                continue;
            }
            if (g.getSupersetId() == Grade.PRIMARY_ID) {
                entity = new BaseEntity();
                entity.setId(g.getId());
                entity.setName(primary.getName() + g.getName());
                datas.add(entity);
            }
            if (g.getSupersetId() == Grade.MIDDLE_ID) {
                entity = new BaseEntity();
                entity.setId(g.getId());
                entity.setName(middle.getName() + g.getName());
                datas.add(entity);
            }
            if (g.getSupersetId() == Grade.SENIOR_ID) {
                entity = new BaseEntity();
                entity.setId(g.getId());
                entity.setName(senior.getName() + g.getName());
                datas.add(entity);
            }
        }
    }

    @OnClick(R.id.rl_user_school)
    public void onClickUserSchool(View view){
        if (userSchool==null){
            userSchool = "";
        }
        Intent intent = new Intent(this,SingleInfoActivity.class);
        intent.putExtra(SingleInfoActivity.EXTRA_TITLE, "所在学校");
        intent.putExtra(SingleInfoActivity.EXTRA_VALUE, userSchool);
        startActivityForResult(intent, SingleInfoActivity.RESULT_CODE_VALUE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==SingleInfoActivity.RESULT_CODE_VALUE){
            userSchool = data.getStringExtra(SingleInfoActivity.EXTRA_VALUE);
            if (userSchool==null){
                userSchool = "";
            }
            tvUserSchool.setText(userSchool);
        }
    }

    @Override
    public void onTitleLeftClick() {
        this.finish();

    }

    @Override
    public void onTitleRightClick() {
         postModifySchool();
    }

    private void postModifySchool() {
        userSchool = tvUserSchool.getText().toString();
        //异步处理
        if (true){
            setActivityResult();
            finish();
        }else{
            Toast.makeText(this,"学校信息修改失败!",Toast.LENGTH_SHORT);
        }
    }

    private void setActivityResult(){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_GRADE, userGrade);
        intent.putExtra(EXTRA_USER_SCHOOL,userSchool);
        setResult(RESULT_CODE_SCHOOL,intent);
    }


}
