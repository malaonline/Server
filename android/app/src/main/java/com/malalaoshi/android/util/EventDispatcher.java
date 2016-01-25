package com.malalaoshi.android.util;

import com.malalaoshi.android.pay.CouponActivity;

import de.greenrobot.event.EventBus;

/**
 * EventBus Dispatcher
 * Created by tianwei on 1/24/16.
 */
public class EventDispatcher {

    private static final class Holdder {
        private static EventDispatcher instance = new EventDispatcher();
    }

    public static EventDispatcher getInstance() {
        return Holdder.instance;
    }

    private EventDispatcher() {

    }

    /**
     * Maybe is not in UI thread. Be careful.
     */
    public void post(Object event) {
        EventBus.getDefault().post(event);
    }

    public void register(CouponActivity couponActivity) {
        EventBus.getDefault().register(couponActivity);
    }

    public void unregister(CouponActivity couponActivity) {
        EventBus.getDefault().unregister(couponActivity);
    }
}
