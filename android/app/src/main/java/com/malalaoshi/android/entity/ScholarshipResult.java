package com.malalaoshi.android.entity;

import java.util.List;

/**
 * Scholarship
 * Created by tianwei on 3/5/16.
 */
public class ScholarshipResult {
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
        private int id;
        private String name;
        private int amount;
        private long expired_at;
        private boolean used;

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

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public long getExpired_at() {
            return expired_at;
        }

        public void setExpired_at(long expired_at) {
            this.expired_at = expired_at;
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }
    }
}
