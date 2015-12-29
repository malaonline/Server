package com.malalaoshi.android.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kang on 15/12/24.
 */
public class GTag {

    private Long id;
    private String name;

    public GTag() {
    }
    public GTag(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GTag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static GTag findTagById(Long tagId, List<GTag> allTag){
        for(GTag tag: allTag){
            if(tag.getId().equals(tagId)){
                return tag;
            }
        }
        return null;
    }
}
