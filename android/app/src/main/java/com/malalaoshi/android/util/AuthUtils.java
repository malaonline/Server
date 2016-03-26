package com.malalaoshi.android.util;

import android.content.Context;
import android.content.Intent;

import com.malalaoshi.android.core.usercenter.LoginActivity;

/**
 * Created by kang on 16/3/5.
 */
public class AuthUtils {

    public static void redirectLoginActivity(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }
}
