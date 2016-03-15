package com.malalaoshi.android.pay;

import com.android.volley.VolleyError;
import com.malalaoshi.android.entity.CreateChargeEntity;
import com.malalaoshi.android.entity.JsonBodyBase;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.util.UIResultCallback;

/**
 * Pingplusplus payer
 * Created by tianwei on 2/28/16.
 */
public class PingppPayer implements Payer {

    @Override
    public void createOrder(JsonBodyBase body, final ResultCallback<Object> callback) {
        NetworkSender.createCourseOrder(body.toJson(), new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                try {
                    if (callback != null) {
                        callback.onResult(json);
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onResult(null);
                    }
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                if (callback != null) {
                    callback.onResult(null);
                }
            }
        });
    }

    @Override
    public void createOrderInfo(String orderId, String channel, UIResultCallback<String> callback) {
        CreateChargeEntity chargeEntity = new CreateChargeEntity();
        chargeEntity.setAction("pay");
        chargeEntity.setChannel(channel);
        NetworkSender.getCharge(orderId, chargeEntity, callback);
    }
}
