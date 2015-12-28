package com.malalaoshi.android.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kang on 15/12/24.
 */
public class GGrade {
    private Long id;
    private String name;
    public static List<GGrade> gradeList;
    static{
        gradeList = new ArrayList<GGrade>();

        gradeList.add(new GGrade(1L, "小学"));
        gradeList.add(new GGrade(2L, "一年级"));
        gradeList.add(new GGrade(3L, "二年级"));
        gradeList.add(new GGrade(4L, "三年级"));
        gradeList.add(new GGrade(5L, "四年级"));
        gradeList.add(new GGrade(6L, "五年级"));
        gradeList.add(new GGrade(7L, "六年级"));

        gradeList.add(new GGrade(8L, "初中"));
        gradeList.add(new GGrade(9L, "初一"));
        gradeList.add(new GGrade(10L, "初二"));
        gradeList.add(new GGrade(11L, "初三"));
        gradeList.add(new GGrade(12L, "初四"));

        gradeList.add(new GGrade(13L, "高中"));
        gradeList.add(new GGrade(14L, "高一"));
        gradeList.add(new GGrade(15L, "高二"));
        gradeList.add(new GGrade(16L, "高三"));
    }

    public static GGrade getGradeById(Long id){
        for(GGrade grade: gradeList){
            if(grade.getId().equals(id)){
                return grade;
            }
        }
        return null;
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


    public GGrade(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public String toString() {
        return "GGrade{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

}
