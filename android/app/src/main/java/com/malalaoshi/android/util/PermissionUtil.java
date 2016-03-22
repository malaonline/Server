package com.malalaoshi.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;


import java.util.ArrayList;
import java.util.List;

/**
 * 权限处理工具类,android 6.0需要
 * Created by kang on 16/3/21.
 */
public class PermissionUtil {


    public static List<String> checkPermission(Context context, String[] permissions) {
        if (permissions.length <= 0) {
            return null;
        }
        List<String> needPermissions = new ArrayList<String>();

        for (int i = 0; i < permissions.length; i++) {
            int hasPermission = ContextCompat.checkSelfPermission(context, permissions[i]);
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                needPermissions.add(permissions[i]);
            }
        }
        return needPermissions;

    }


    public static void requestPermissions(final Fragment fragment, final List<String> permissions, final int requestCode ) {
       if (permissions.size()>0){
           /* if (!fragment.shouldShowRequestPermissionRationale(permissions.get(i))) {

                showMessageOKCancel(context, message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                fragment.requestPermissions(permissions.toArray(new String[permissions.size()]), requestCode);
                            }
                        });
                return;
            }*/
            fragment.requestPermissions(permissions.toArray(new String[permissions.size()]), requestCode);
        }
    }

    public static void requestPermissions(Activity activity,  final List<String> permissions, final int requestCode) {
        if (permissions.size()>0){
           /* if (!ActivityCompat.shouldShowRequestPermissionRationale(activity ,permissions.get(i))) {

                showMessageOKCancel(context,message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.requestPermissions(permissions.toArray(new String[permissions.size()]), requestCode);
                            }
                        });
                return;
            }*/
            ActivityCompat.requestPermissions(activity, permissions.toArray(new String[permissions.size()]), requestCode);
        }
    }

    public static boolean permissionsResult(int[] grantResults){
        List<String> failePermissions = new ArrayList<>();
        //如果请求被取消，那么 result 数组将为空
        boolean res = true;
        if (grantResults.length<=0){
            res = false;
        }
        for (int i=0;i<grantResults.length;i++){
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                res = false;
                break;
            }
        }
        return res;
    }

}
