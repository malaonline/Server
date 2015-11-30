package com.malalaoshi.android.entity;

/**
 * Created by zl on 15/11/26.
 */
public class Teacher {
    private String id;
    private String name;
    private char degree;
    private boolean active;
    private User user;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public char getDegree() {
        return degree;
    }

    public boolean isActive() {
        return active;
    }

    public User getUser() {
        return user;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDegree(char degree) {
        this.degree = degree;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
