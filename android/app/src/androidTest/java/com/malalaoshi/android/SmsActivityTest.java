package com.malalaoshi.android;

import android.os.SystemClock;
import android.widget.ImageView;

import com.malalaoshi.android.usercenter.SmsAuthActivity;

/**
 * Sms unit test
 * Created by tianwei on 1/17/16.
 */
public class SmsActivityTest extends BaseActivityTest {
    private SmsAuthActivity activity;
    private ImageView backView;

    @Override
    protected void onSetUp() {
        activity = startActivity(SmsAuthActivity.class);
        backView = (ImageView) activity.findViewById(R.id.iv_quit);
    }

    public void testActivityNotNull() {
        assertEquals(activity != null, true);
    }

    public void testTitle() {
        performClick(backView);
        SystemClock.sleep(1000);
        assertEquals(activity.isFinishing(), true);
    }

    @Override
    protected void onTearDown() throws IllegalAccessException {
        activity.finish();
        scrubClass(this.getClass());
    }
}
