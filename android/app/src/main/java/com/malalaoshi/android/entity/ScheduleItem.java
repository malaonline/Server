package com.malalaoshi.android.entity;

import com.malalaoshi.android.util.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kang on 15/12/29.
 */
public class ScheduleItem {
    public static int TYPE_DATE = 0;
    public static int TYPE_COURSE = 1;
    protected int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
