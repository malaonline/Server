package com.malalaoshi.android.events;

import com.malalaoshi.android.core.event.BusEvent;

/**
 * Created by kang on 16/5/10.
 */
public class UnpayOrderEvent extends BusEvent {

    private Long unpayCount = new Long(-1);
    public UnpayOrderEvent(int eventType) {
        super(eventType);
    }

    public Long getUnpayCount() {
        return unpayCount;
    }

    public void setUnpayCount(Long unpayCount) {
        this.unpayCount = unpayCount;
    }
}
