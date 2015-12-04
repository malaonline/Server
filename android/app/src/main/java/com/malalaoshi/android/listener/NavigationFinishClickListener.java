package com.malalaoshi.android.listener;

import android.app.Activity;
import android.support.v4.app.ActivityCompat;
import android.view.View;

/**
 * Created by zl on 15/12/1.
 */
public class NavigationFinishClickListener implements View.OnClickListener{
    private Activity activity;

    public NavigationFinishClickListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onClick(View v) {
        ActivityCompat.finishAfterTransition(activity);
    }
}
