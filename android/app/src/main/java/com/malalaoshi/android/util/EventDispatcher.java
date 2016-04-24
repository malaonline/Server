package com.malalaoshi.android.util;

import de.greenrobot.event.EventBus;

/**
 * EventBus Dispatcher
 * Created by tianwei on 1/24/16.
 */
public class EventDispatcher {

    private static final class Holder {
        private static final EventDispatcher instance = new EventDispatcher();
    }

    public static EventDispatcher getInstance() {
        return Holder.instance;
    }

    private EventDispatcher() {

    }

    /**
     * Maybe is not in UI thread. Be careful.
     */
    public void post(Object event) {
        EventBus.getDefault().post(event);
    }

    public void register(Object obj) {
        EventBus.getDefault().register(obj);
    }

    public void unregister(Object obj) {
        EventBus.getDefault().unregister(obj);
    }
}
