package com.malalaoshi.android.util;

/**
 * UI result callback
 * Created by tianwei on 16-3-15.
 */
public abstract class UIResultCallback<T> {
    protected abstract void onResult(T t);

    public void setResult(final T t) {
        MalaContext.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                onResult(t);
            }
        });
    }
}
