package com.malalaoshi.android.entity;

/**
 * Created by kang on 16/1/30.
 */
public class Achievement extends BaseEntity {
    private String title;
    private String img;
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
