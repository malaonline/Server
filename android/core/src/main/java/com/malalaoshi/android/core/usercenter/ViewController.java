package com.malalaoshi.android.core.usercenter;

import android.view.View;

/**
 * View controller
 * Created by tianwei on 1/10/16.
 */
public interface ViewController {
    void onBack();

    void onFinish();

    void onChangeView(View current, boolean pullToStack, Class<? extends View> newClazz);
}
