package com.malalaoshi.android.pay;

import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.JsonBodyBase;

/**
 * Payer interface
 * Created by tianwei on 2/28/16.
 */
public interface Payer {
    CreateCourseOrderResultEntity createOrder(JsonBodyBase json) throws Exception;
    String createOrderInfo(String orderId, String channel) throws Exception;
}
