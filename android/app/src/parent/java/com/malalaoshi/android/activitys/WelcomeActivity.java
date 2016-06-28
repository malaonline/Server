package com.malalaoshi.android.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.malalaoshi.android.MainActivity;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;

import java.lang.ref.WeakReference;

/**
 * Created by kang on 16/6/27.
 */
public class WelcomeActivity extends BaseActivity {
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mHandler.postDelayed(new MyRunnable(this),1500);
    }

    static class MyRunnable implements Runnable{
        private WeakReference<Activity> activitys;
        public MyRunnable(Activity activity){
            activitys = new WeakReference<Activity>(activity);
        }
        @Override
        public void run() {
            Activity activity = activitys.get();
            if (activity!=null){
                Intent intent = new Intent(activity,MainActivity.class);
                activity.startActivity(intent);
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                activity.finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected String getStatName() {
        return "欢迎页";
    }
}
