package com.malalaoshi.android.entity;

/**
 * Created by kang on 15/12/24.
 */
public class GMemberService {

    private Long id;
    private String name;
    private String detail;
    private boolean enbaled;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean isEnbaled() {
        return enbaled;
    }

    public void setEnbaled(boolean enbaled) {
        this.enbaled = enbaled;
    }

    @Override
    public String toString() {
        return "GMemberService{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", enbaled=" + enbaled +
                '}';
    }

}
