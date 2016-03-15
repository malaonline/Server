package com.malalaoshi.android.pay;

import com.malalaoshi.android.entity.JsonBodyBase;
import com.malalaoshi.android.util.UIResultCallback;

/**
 * Payer interface
 * Created by tianwei on 2/28/16.
 */
public interface Payer {
    void createOrder(JsonBodyBase body, ResultCallback<Object> callback);
    void createOrderInfo(String orderId, String channel, UIResultCallback<String> callback);
}
