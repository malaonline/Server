package com.malalaoshi.android.core.network.api;

import android.os.Looper;
import android.util.Log;

import com.malalaoshi.android.core.MalaContext;

/**
 * API 执行器
 * Created by tianwei on 4/17/16.
 */
public class ApiExecutor {

    /**
     * 如过在非主线程，切换到主线程。然后在主线程启动线程做网络请求。
     *
     * @param apiContext 网络请求
     * @param <T>        类型
     */
    public static <T> void exec(final ApiContext<T> apiContext) {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            request(apiContext);
        } else {
            MalaContext.postOnMainThread(new Runnable() {
                @Override
                public void run() {
                    request(apiContext);
                }
            });
        }
    }

    private static <T> void request(final ApiContext<T> apiContext) {
        apiStart(apiContext);
        MalaContext.exec(new Runnable() {
            @Override
            public void run() {
                try {
                    T data = apiContext.request();
                    if (data == null) {
                        throw new RuntimeException("API response is null");
                    } else {
                        apiSuccess(apiContext, data);
                    }
                } catch (final Exception ex) {
                    apiFailed(apiContext, ex);
                } finally {
                    finished(apiContext);
                }
            }
        });
    }

    private static <T> void apiStart(final ApiContext<T> apiContext) {
        try {
            apiContext.onApiStarted();
        } catch (Exception e) {
            Log.e("MALA", "API Start. 实现类错误： " + e.getMessage());
        }
    }

    private static <T> void apiSuccess(final ApiContext<T> apiContext, final T data) {
        MalaContext.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    apiContext.onApiSuccess(data);
                } catch (Exception ex) {
                    Log.e("MALA", "API Success:实现类错误：: " + ex.getMessage());
                }
            }
        });
    }

    private static <T> void apiFailed(final ApiContext<T> apiContext, final Exception ex) {
        MalaContext.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    apiContext.onApiFailure(ex);
                } catch (Exception ex) {
                    Log.e("MALA", "API Failed:实现类错误：" + ex.getMessage());
                }

            }
        });
    }

    private static <T> void finished(final ApiContext<T> apiContext) {
        MalaContext.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                try {
                    apiContext.onApiFinished();
                } catch (Exception ex) {
                    Log.e("MALA", "API finished");
                }

            }
        });
    }
}
