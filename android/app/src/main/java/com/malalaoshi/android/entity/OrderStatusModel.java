package com.malalaoshi.android.entity;

/**
 * Order status model
 * Created by tianwei on 16-3-18.
 */
public class OrderStatusModel {
    private long id;
    private String status;
    private boolean is_timeslot_allocated;
    private Boolean is_teacher_published;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean is_timeslot_allocated() {
        return is_timeslot_allocated;
    }

    public void setIs_timeslot_allocated(boolean is_timeslot_allocated) {
        this.is_timeslot_allocated = is_timeslot_allocated;
    }

    public Boolean getIs_teacher_published() {
        return is_teacher_published;
    }

    public void setIs_teacher_published(Boolean is_teacher_published) {
        this.is_teacher_published = is_teacher_published;
    }
}
