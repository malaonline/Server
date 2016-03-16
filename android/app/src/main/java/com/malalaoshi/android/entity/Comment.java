package com.malalaoshi.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kang on 16/3/7.
 */
public class Comment implements Parcelable {
    private Long id;
    private Long timeslot;
    private Integer score;
    private String content;

    public Comment() {
    }

    public Comment(Long id, Long timeslot, Integer score, String content) {
        this.id = id;
        this.timeslot = timeslot;
        this.score = score;
        this.content = content;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(Long timeslot) {
        this.timeslot = timeslot;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.timeslot);
        dest.writeValue(this.score);
        dest.writeString(this.content);
    }

    protected Comment(Parcel in) {
        this.id = (Long) in.readValue(Long.class.getClassLoader());
        this.timeslot = (Long) in.readValue(Long.class.getClassLoader());
        this.score = (Integer) in.readValue(Integer.class.getClassLoader());
        this.content = in.readString();
    }

    public static final Parcelable.Creator<Comment> CREATOR = new Parcelable.Creator<Comment>() {
        public Comment createFromParcel(Parcel source) {
            return new Comment(source);
        }

        public Comment[] newArray(int size) {
            return new Comment[size];
        }
    };
}
