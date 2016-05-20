package com.malalaoshi.android.entity;

/**
 * Created by kang on 16/5/20.
 */
public class Report {
    private Long subject_id;
    private boolean supported;
    private Integer right_nums;
    private Integer total_nums;

    public Long getSubject_id() {
        return subject_id;
    }

    public void setSubject_id(Long subject_id) {
        this.subject_id = subject_id;
    }

    public boolean isSupported() {
        return supported;
    }

    public void setSupported(boolean supported) {
        this.supported = supported;
    }

    public Integer getRight_nums() {
        return right_nums;
    }

    public void setRight_nums(Integer right_nums) {
        this.right_nums = right_nums;
    }

    public Integer getTotal_nums() {
        return total_nums;
    }

    public void setTotal_nums(Integer total_nums) {
        this.total_nums = total_nums;
    }
}
