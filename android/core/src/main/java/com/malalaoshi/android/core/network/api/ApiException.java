package com.malalaoshi.android.core.network.api;

/**
 * Api exception
 * Created by tianwei on 3/27/16.
 */
public class ApiException extends Exception {
    public ApiException(String msg) {
        super(msg);
    }

    public ApiException(Exception e) {
        super(e);
    }
}
