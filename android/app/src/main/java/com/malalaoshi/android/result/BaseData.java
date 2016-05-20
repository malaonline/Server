package com.malalaoshi.android.result;

/**
 * Created by kang on 16/5/20.
 */
public class BaseData<T> {
    private Integer code;
    private String message;
    private T results;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResults() {
        return results;
    }

    public void setResults(T results) {
        this.results = results;
    }
}
