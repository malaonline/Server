package com.malalaoshi.android.events;

import com.malalaoshi.android.core.event.BusEvent;

/**
 * Created by kang on 16/5/10.
 */
public class NoticeEvent extends BusEvent {

    private Long unpayCount = new Long(-1);
    private Long uncommentCount = new Long(-1);
    public NoticeEvent(int eventType) {
        super(eventType);
    }

    public Long getUnpayCount() {
        return unpayCount;
    }

    public void setUnpayCount(Long unpayCount) {
        this.unpayCount = unpayCount;
    }

    public Long getUncommentCount() {
        return uncommentCount;
    }

    public void setUncommentCount(Long uncommentCount) {
        this.uncommentCount = uncommentCount;
    }
}
