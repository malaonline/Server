package com.malalaoshi.android.core.network.api;

import android.support.annotation.NonNull;

/**
 * Api context
 * Created by tianwei on 4/17/16.
 */
public interface ApiContext<T> {
    T request() throws Exception;

    void onApiSuccess(@NonNull T response);

    void onApiFailure(Exception exception);

    void onApiStarted();

    void onApiFinished();
}
