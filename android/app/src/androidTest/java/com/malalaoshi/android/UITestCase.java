package com.malalaoshi.android;

import android.app.Activity;
import android.content.Intent;
import android.test.InstrumentationTestCase;
import android.view.View;

/**
 * Mala base test case
 * Created by tianwei on 1/17/16.
 */
public abstract class UITestCase extends InstrumentationTestCase {

    private static final String PACKAGE_NAME = "com.malalaoshi.android.parent.debug";

    @Override
    protected void setUp() throws Exception {
        try {
            super.setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }
        onSetUp();
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            super.tearDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        onTearDown();
    }

    protected <T extends Activity> T startActivity(Class<T> clazz) {
        Intent intent = new Intent();
        intent.setClassName(PACKAGE_NAME, clazz.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return (T) getInstrumentation().startActivitySync(intent);
    }

    protected abstract void onSetUp();

    protected abstract void onTearDown() throws IllegalAccessException;

    public void performClick(View view) {
        TestUtils.performClick(getInstrumentation(), view);
    }
}
