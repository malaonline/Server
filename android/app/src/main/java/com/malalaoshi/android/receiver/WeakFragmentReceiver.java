package com.malalaoshi.android.receiver;

import android.content.BroadcastReceiver;
import android.support.v4.app.Fragment;

import java.lang.ref.WeakReference;

/**
 * 弱引用广播
 * Created by tianwei on 16-5-10.
 */
public abstract class WeakFragmentReceiver extends BroadcastReceiver {
    protected final WeakReference<Fragment> reference;

    public WeakFragmentReceiver(Fragment fragment) {
        reference = new WeakReference<>(fragment);
    }

}
