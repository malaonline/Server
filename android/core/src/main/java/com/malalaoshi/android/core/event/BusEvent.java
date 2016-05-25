package com.malalaoshi.android.core.event;

/**
 * User center
 * Created by kang on 16/3/13.
 */
public class BusEvent {
    //跟个人中心UI
    public static final int BUS_EVENT_LOGIN_SUCCESS = 0x01;
    public static final int BUS_EVENT_LOGOUT_SUCCESS = 0x02;
    public static final int BUS_EVENT_UPDATE_USER_NAME_SUCCESS = 0x03;
    public static final int BUS_EVENT_UPDATE_USERCENTER_UI = 0x04;
    public static final int BUS_EVENT_RELOAD_USERCENTER_DATA = 0x05;
    public static final int BUS_EVENT_UPDATE_TIMETABLE_UI = 0x06;
    public static final int BUS_EVENT_RELOAD_TIMETABLE_DATA = 0x07;
    public static final int BUS_EVENT_RELOAD_FETCHEVALUATED = 0x08;
    private int eventType = -1;

    public BusEvent(int eventType) {
        this.eventType = eventType;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }
}
