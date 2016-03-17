package com.malalaoshi.android.entity;

import com.malalaoshi.android.util.CalendarUtils;
import com.malalaoshi.android.util.MiscUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Course confirm class
 * Created by tianwei on 3/17/16.
 */
public class TimesModel {
    private List<List<String>> data;
    private List<String> displayTimes;

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }

    public List<String> getDisplayTimes() {
        if (MiscUtil.isEmpty(data)) {
            return new ArrayList<>();
        }
        displayTimes = new ArrayList<>();
        for (List<String> item : data) {
            try {
                String[] begins = CalendarUtils.format(item.get(0));
                String[] ends = CalendarUtils.format(item.get(1));
                displayTimes.add(String.format("%s (%s-%s)", begins[0], begins[1], ends[1]));
            } catch (Exception e) {

            }
        }
        return displayTimes;
    }
}
