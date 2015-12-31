package com.malalaoshi.android.entity;

/**
 * Created by kang on 15/12/24.
 */
public class MemberService extends BaseEntity {

    private String detail;
    private boolean enbaled;

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
        return "MemberService{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", detail='" + detail + '\'' +
                ", enbaled=" + enbaled +
                '}';
    }

}
