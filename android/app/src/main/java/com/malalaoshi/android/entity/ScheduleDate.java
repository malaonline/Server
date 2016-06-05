package com.malalaoshi.android.entity;

/**
 * Created by kang on 16/6/5.
 */
public class ScheduleDate extends ScheduleItem {
    private Long timestamp;
    public ScheduleDate(){
        this.type = TYPE_DATE;
    }

    public ScheduleDate(Long timestamp) {
        this.timestamp = timestamp;
        this.type = TYPE_DATE;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
