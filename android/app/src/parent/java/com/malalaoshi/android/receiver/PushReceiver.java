package com.malalaoshi.android.receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.malalaoshi.android.MainActivity;
import com.malalaoshi.android.util.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by kang on 16/2/25.
 */
public class PushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPush";
    public static String NOTIFICATION_TYPE = "notificationtype";
    private final String NOTIFICATION_TYPE_CLASS_CHANGED = "1";
    private final String NOTIFICATION_TYPE_CLASS_STOPED = "2";
    private final String NOTIFICATION_TYPE_CLASS_FINISHED = "3";
    private final String NOTIFICATION_TYPE_CLASS_STARTING = "4";


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        //Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
            processNotification(context, bundle);

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    private void processNotification(Context context, Bundle bundle) {
        //获取通知消息类型
        String notificationType = "";
        try {
            JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
            notificationType = json.getString(NOTIFICATION_TYPE);
        } catch (JSONException e) {
            Log.e(TAG, "Get message extra JSON error!");
        }

        if (NOTIFICATION_TYPE_CLASS_CHANGED.equals(notificationType)) {
            //调课成功
            dealClassChangedNotification(context, bundle);
        } else if (NOTIFICATION_TYPE_CLASS_STOPED.equals(notificationType)) {
            //停课通知
            dealClassStopedNotification(context, bundle);
        } else if (NOTIFICATION_TYPE_CLASS_FINISHED.equals(notificationType)) {
            //上课完成
            dealRClassFinishedNotification(context, bundle);
        } else if (NOTIFICATION_TYPE_CLASS_STARTING.equals(notificationType)) {
            //课前通知
            dealClassStartingNotification(context, bundle);
        }
    }

    private void dealClassStartingNotification(Context context, Bundle bundle) {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra(MainActivity.EXTRAS_PAGE_INDEX, MainActivity.PAGE_INDEX_COURSES);
        i.putExtras(bundle);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(i);
    }

    private void dealRClassFinishedNotification(Context context, Bundle bundle) {
        dealClassStartingNotification(context, bundle);
    }

    private void dealClassStopedNotification(Context context, Bundle bundle) {
        dealClassStartingNotification(context, bundle);
    }

    private void dealClassChangedNotification(Context context, Bundle bundle) {
        dealClassStartingNotification(context, bundle);

    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processMessage(Context context, Bundle bundle) {

    }

    /**
     * 判断应用当前是否运行中，包括后台运行
     * @param context
     * @return
     */
    private boolean isApplicationRunning(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = am.getRunningTasks(Integer.MAX_VALUE);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : taskList) {
            if (runningTaskInfo.topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
