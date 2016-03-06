package com.malalaoshi.android.entity;

/**
 * Course data choice ui entity
 * Created by tianwei on 3/6/16.
 */
public class CourseDateUI {
    public CourseDateUI(DisplayType type) {
        setType(type);
    }

    public enum DisplayType {
        BOOKED,
        CHOICE,
        VALID
    }

    private DisplayType type;

    public DisplayType getType() {
        return type;
    }

    public void setType(DisplayType type) {
        this.type = type;
    }
}
