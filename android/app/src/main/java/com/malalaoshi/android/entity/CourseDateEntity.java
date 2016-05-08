package com.malalaoshi.android.entity;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Course date entity
 * Created by tianwei on 3/12/16.
 */
public class CourseDateEntity implements Comparable<CourseDateEntity> {
    private long id;
    private String start;
    private String end;
    private boolean available;
    private int day;
    private boolean choice;
    private boolean isTitle;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public boolean isChoice() {
        return choice;
    }

    public void setChoice(boolean choice) {
        this.choice = choice;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public void setIsTitle(boolean isTitle) {
        this.isTitle = isTitle;
    }

    @Override
    public int compareTo(@NonNull CourseDateEntity another) {
        return this.id > another.id ? 1 : -1;
    }

    public static List<CourseDateEntity> format(String jsonStr) throws Exception {
        List<CourseDateEntity> list = new ArrayList<>();
        JSONObject json;
        json = new JSONObject(jsonStr);
        for (int i = 1; i <= 7; i++) {
            JSONArray sections = json.getJSONArray(i + "");
            if (sections.length() != 5) {
                throw new RuntimeException("time section's len is error");
            }
            for (int j = 0; j < 5; j++) {
                JSONObject section = sections.getJSONObject(j);
                CourseDateEntity item = new CourseDateEntity();
                item.setAvailable(section.optBoolean("available"));
                item.setEnd(section.optString("end"));
                item.setStart(section.optString("start"));
                item.setId(section.optLong("id"));
                item.setDay(i);
                list.add(item);
            }
        }
        Collections.sort(list);
        List<CourseDateEntity> resList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 7; j++) {
                resList.add(list.get(i + j * 5));
            }
        }
        list.clear();
        return resList;
    }
}
