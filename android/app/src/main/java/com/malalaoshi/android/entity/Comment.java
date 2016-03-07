package com.malalaoshi.android.entity;

/**
 * Created by kang on 16/3/7.
 */
public class Comment {
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




}
