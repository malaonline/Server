package com.malalaoshi.android.report.entity;

/**
 * 知识点正确率
 * Created by tianwei on 6/4/16.
 */
public class KnowledgePointAccuracy {
    private int id;
    private String name;
    private int total_item;
    private int right_item;

    public KnowledgePointAccuracy(String name, int total, int right) {
        setName(name);
        setTotal_item(total);
        setRight_item(right);
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

    public int getTotal_item() {
        return total_item;
    }

    public void setTotal_item(int total_item) {
        this.total_item = total_item;
    }

    public int getRight_item() {
        return right_item;
    }

    public void setRight_item(int right_item) {
        this.right_item = right_item;
    }
}
