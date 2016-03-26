package com.malalaoshi.android.core.usercenter;

import android.os.Bundle;

import com.malalaoshi.android.core.R;
import com.malalaoshi.android.core.base.BaseActivity;

/**
 * 手机号登录
 * Created by tianwei on 3/26/16.
 */
public class LoginActivity extends BaseActivity {
    public static final int RESULT_CODE_LOGIN_SUCCESSED = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core__activity_login);
        LoginFragment fragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, fragment).commit();
    }
}
