package com.malalaoshi.android.report.entity;

import java.io.Serializable;

/**
 * 练习题错误分布
 * Created by tianwei on 6/4/16.
 */
public class ExerciseErrorDistribution implements Serializable {
    private int id;
    private String name;
    private float rate;

    public ExerciseErrorDistribution(int id, String name, float rate) {
        setId(id);
        setName(name);
        setRate(rate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }
}
