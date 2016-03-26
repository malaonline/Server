package com.malalaoshi.android.core.network;

import java.lang.ref.WeakReference;

/**
 * Hold a UI object by weak reference
 * Created by tianwei on 3/27/16.
 */
public abstract class WeakRefHolder<V> {
    private WeakReference<V> reference;

    public WeakRefHolder(V v) {
        reference = new WeakReference<>(v);
    }

    protected V get() {
        return reference.get();
    }
}
