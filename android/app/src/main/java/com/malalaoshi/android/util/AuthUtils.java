package com.malalaoshi.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.malalaoshi.android.usercenter.SmsAuthActivity;

/**
 * Created by kang on 16/3/5.
 */
public class AuthUtils {

    public static void redirectLoginActivity(Context context){
        Intent intent = new Intent(context, SmsAuthActivity.class);
        context.startActivity(intent);
    }
}
