package com.malalaoshi.android.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.EditText;
import android.widget.Toast;

import com.malalaoshi.android.R;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.view.TitleBarView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/1/25.
 */
public class SingleInfoActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {

    public static int RESULT_CODE_VALUE = 0x002;
    public static String EXTRA_VALUE = "value";
    public static String EXTRA_TITLE = "title";

    @Bind(R.id.titleBar)
    TitleBarView titleBar;

    @Bind(R.id.et_value)
    EditText etValue;

    private String strValue;

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
        strValue = intent.getStringExtra(EXTRA_VALUE);
        if (strValue==null){
            strValue = "";
        }
        etValue.setText(strValue);
        etValue.setSelection(strValue.length());
        String strTitle = intent.getStringExtra(EXTRA_TITLE);
        if (strTitle==null){
            strTitle = "";
        }
        titleBar.setTitle(strTitle);
    }

    @Override
    public void onTitleLeftClick() {
        this.finish();
    }

    @Override
    public void onTitleRightClick() {
        setActivityResult();
        finish();
    }

    private void setActivityResult(){
        Intent intent = new Intent();
        strValue = etValue.getText().toString();
        intent.putExtra(EXTRA_VALUE,strValue);
        setResult(RESULT_CODE_VALUE,intent);
    }

}
