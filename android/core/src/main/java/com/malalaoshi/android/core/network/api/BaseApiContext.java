package com.malalaoshi.android.core.network.api;

import java.lang.ref.WeakReference;

/**
 * Base api context
 * Created by tianwei on 4/17/16.
 */
public abstract class BaseApiContext<V, T> implements ApiContext<T> {
    private WeakReference<V> ref;

    public BaseApiContext(V v) {
        ref = new WeakReference<>(v);
    }

    protected V get() {
        V v = ref.get();
        if (v == null) {
            throw new RuntimeException("v is null");
        }
        return v;
    }

    @Override
    public void onApiFailure(Exception exception) {

    }

    @Override
    public void onApiStarted() {

    }

    @Override
    public void onApiFinished() {

    }
}
