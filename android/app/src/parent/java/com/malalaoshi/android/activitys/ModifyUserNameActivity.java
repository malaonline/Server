package com.malalaoshi.android.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.malalaoshi.android.R;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.view.TitleBarView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kang on 16/1/25.
 */
public class ModifyUserNameActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {

    public static int RESULT_CODE_NAME = 0x001;
    public static String EXTRA_USER_NAME = "username";

    @Bind(R.id.titleBar)
    TitleBarView titleBar;

    @Bind(R.id.et_value)
    EditText etUserName;

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_modify_singlevalue);
        ButterKnife.bind(this);
        titleBar.setOnTitleBarClickListener(this);
        initDatas();
    }

    private void initDatas() {
        Intent intent = getIntent();
        userName = intent.getStringExtra(EXTRA_USER_NAME);
        if (userName==null){
            userName = "";
        }
        etUserName.setText(userName);
        titleBar.setTitle("更改名字");
    }

    @Override
    public void onTitleLeftClick() {
        this.finish();
    }

    @Override
    public void onTitleRightClick() {
        postModifyUserName();

    }

    private void postModifyUserName() {
        userName = etUserName.getText().toString();
        if (true){
            setActivityResult();
            finish();
        }else{
            Toast.makeText(this,"修改姓名失败!",Toast.LENGTH_SHORT);
        }
    }

    private void setActivityResult(){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_USER_NAME,userName);
        setResult(RESULT_CODE_NAME,intent);
    }
}
