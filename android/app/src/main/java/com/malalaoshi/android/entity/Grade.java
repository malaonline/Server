package com.malalaoshi.android.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zl on 15/12/14.
 */
public class Grade{
    private Long id;
    private String name;
    private Boolean leaf;

    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public Boolean getLeaf(){
        return leaf;
    }

    public void setLeaf(Boolean leaf){
        this.leaf = leaf;
    }

    public Grade(){
    }

    public Grade(Long id, String name, Boolean leaf){
        this.setId(id);
        this.setName(name);
        this.setLeaf(leaf);
    }

    public static List<Grade> gradeList;
    static{
        gradeList = new ArrayList<Grade>();

        gradeList.add(new Grade(1L, "小学", false));
        gradeList.add(new Grade(2L, "一年级", true));
        gradeList.add(new Grade(3L, "二年级", true));
        gradeList.add(new Grade(4L, "三年级", true));
        gradeList.add(new Grade(5L, "四年级", true));
        gradeList.add(new Grade(6L, "五年级", true));
        gradeList.add(new Grade(7L, "六年级", true));

        gradeList.add(new Grade(8L, "初中", false));
        gradeList.add(new Grade(9L, "初一", true));
        gradeList.add(new Grade(10L, "初二", true));
        gradeList.add(new Grade(11L, "初三", true));
        gradeList.add(new Grade(12L, "初四", true));

        gradeList.add(new Grade(13L, "高中", false));
        gradeList.add(new Grade(14L, "高一", true));
        gradeList.add(new Grade(15L, "高二", true));
        gradeList.add(new Grade(16L, "高三", true));
    }

    public static boolean isPrimary(Long id){
        return id == null ? false : (id.compareTo(1L) >= 0 && id.compareTo(7L) <= 0) ? true : false;
    }
    public static boolean isMiddle(Long id){
        return id == null ? false : (id.compareTo(8L) >= 0 && id.compareTo(12L) <= 0) ? true : false;
    }
    public static boolean isSenior(Long id){
        return id == null ? false : (id.compareTo(13L) >= 0 && id.compareTo(16L) <= 0) ? true : false;
    }
    public static Grade getById(Long id){
        for(Grade grade: gradeList){
            if(grade.getId().equals(id)){
                return grade;
            }
        }
        return null;
    }
    public static String generateGradeViewString(Long [] gradesAry){
        String str = null;

        if(gradesAry != null){
            if(gradesAry.length == 1){
                Long gradeId = gradesAry[0];
                if(Grade.isPrimary(gradeId)){
                    str = "小学";
                }else if(Grade.isMiddle(gradeId)){
                    str = "初中";
                }else if(Grade.isSenior(gradeId)){
                    str = "高中";
                }
            }else{
                boolean hasPrimary = false;
                boolean hasMiddle = false;
                boolean hasSenior = false;
                for(Long id: gradesAry){
                    if(Grade.isPrimary(id)){
                        hasPrimary = true;
                    }else if(Grade.isMiddle(id)){
                        hasMiddle = true;
                    }else if(Grade.isSenior(id)){
                        hasSenior = true;
                    }
                }
                int ck = 0;
                if(hasPrimary){
                    ck++;
                    str = "小学";
                }
                if(hasMiddle){
                    ck++;
                    str = "初中";
                }
                if(hasSenior){
                    ck++;
                    str = "高中";
                }
                if(ck > 1){
                    str = "";
                    if(hasPrimary){
                        str += "小";
                    }
                    if(hasMiddle){
                        str += "初";
                    }
                    if(hasSenior){
                        str += "高";
                    }
                }
            }
        }

        return str;
    }
}
