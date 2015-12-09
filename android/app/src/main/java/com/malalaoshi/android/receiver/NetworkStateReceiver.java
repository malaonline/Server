package com.malalaoshi.android.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;

/**
 * Created by liumengjun on 12/9/15.
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    private static final String TAG = NetworkStateReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: onReceive() 执行了两次
        boolean success = isNetworkConnected(context);

        MalaApplication.getInstance().setIsNetworkOk(success);
        if (!success) {
            String msg = context.getString(R.string.networt_disconnected);
            Log.e(TAG, msg);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        } else {
            String msg = context.getString(R.string.networt_connected);
            Log.i(TAG, msg);
//            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isNetworkConnected(Context context) {
        boolean success = false;

        //获得网络连接服务
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取WIFI网络连接状态
        NetworkInfo.State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        Log.d(TAG, "wifi stat: " + state);
        // 判断是否正在使用WIFI网络
        if (NetworkInfo.State.CONNECTED == state) {
            success = true;
        }

        if (!success) {
            // 获取MOBILE网络连接状态
            state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            Log.d(TAG, "mobile stat: " + state);
            // 判断是否正在使用MOBILE网络
            if (NetworkInfo.State.CONNECTED == state) {
                success = true;
            }
        }
        return success;
    }
}
