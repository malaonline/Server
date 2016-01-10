package com.malalaoshi.android.util;

import android.widget.Toast;

import com.malalaoshi.android.MalaApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Mist util
 * Created by tianwei on 1/9/16.
 */
public class MiscUtil {

    public static boolean isMobilePhone(String phone) {
        Pattern p = Pattern.compile("^((17[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    public static void toast(int rid) {
        Toast.makeText(MalaApplication.getInstance().getApplicationContext(),
                rid, Toast.LENGTH_SHORT).show();
    }
}
