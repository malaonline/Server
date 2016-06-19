package com.malalaoshi.android.entity;

import java.util.List;

/**
 * Scholarship
 * Created by tianwei on 3/5/16.
 */
public class ScholarshipModel {
    private int count;
    private String next;
    private String previous;
    private List<ScholarshipEntity> results;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<ScholarshipEntity> getResults() {
        return results;
    }

    public void setResults(List<ScholarshipEntity> results) {
        this.results = results;
    }

    public static class ScholarshipEntity {

    }
}
