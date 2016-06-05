package com.malalaoshi.android.report.entity;

/**
 * 提分点分析(各知识点全部用户平均得分率及指定学生得分率)
 * Created by tianwei on 6/4/16.
 */
public class ScoreAnalyses {
    private int id;
    private String name;
    private float my_score;
    private float ave_score;

    public ScoreAnalyses(int my, int ave, String name) {
        setMy_score(my);
        setAve_score(ave);
        setName(name);
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

    public float getMy_score() {
        return my_score;
    }

    public void setMy_score(float my_score) {
        this.my_score = my_score;
    }

    public float getAve_score() {
        return ave_score;
    }

    public void setAve_score(float ave_score) {
        this.ave_score = ave_score;
    }
}
