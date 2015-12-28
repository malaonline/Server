package com.malalaoshi.android.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kang on 15/12/25.
 */
public class GSubject {
    private Long id;
    private String name;

    public GSubject(Long id, String name) {
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

    public static List<GSubject> subjectList;
    static{
        subjectList = new ArrayList<GSubject>();
        subjectList.add(new GSubject(1L, "语文"));
        subjectList.add(new GSubject(2L, "数学"));
        subjectList.add(new GSubject(3L, "英语"));
        subjectList.add(new GSubject(4L, "物理"));
        subjectList.add(new GSubject(5L, "化学"));
        subjectList.add(new GSubject(6L, "生物"));
        subjectList.add(new GSubject(7L, "历史"));
        subjectList.add(new GSubject(8L, "地理"));
        subjectList.add(new GSubject(9L, "政治"));
    }

    public static GSubject getSubjectById(Long id){

        for(GSubject subject : subjectList){
            if(subject.getId().equals(id)){
                return subject;
            }
        }
        return null;
    }
    @Override
    public String toString() {
        return "GSubject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }


}
