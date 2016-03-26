package com.malalaoshi.android.core.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.malalaoshi.android.core.MalaContext;

/**
 * 当有网络请求的时候，先把请求置与非UI线程执行，等执行完成以后，回送到UI线程
 * Created by tianwei on 16-3-15.
 */
public abstract class UIResultCallback<V, T> extends WeakRefHolder<V> implements Callback<T> {

    public UIResultCallback(V v) {
        super(v);
    }

    public abstract void onResult(@NonNull V v, T t);

    @Override
    public void setResult(final T t) {
        MalaContext.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                V v = get();
                if (v != null) {
                    try {
                        onResult(v, t);
                    } catch (Exception e) {
                        Log.i("MALA", "UIResultCallback: " + e.toString());
                        //当UI控件的状态非法导致失败
                    }
                }
            }
        });
    }
}
