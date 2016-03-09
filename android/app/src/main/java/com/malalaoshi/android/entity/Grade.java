package com.malalaoshi.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zl on 15/12/14.
 */
public class Grade extends BaseEntity{
    public static final long PRIMARY_ID = 1;
    public static final long MIDDLE_ID = 8;
    public static final long SENIOR_ID = 12;

    private Boolean leaf;
    private Long supersetId;

    public Boolean getLeaf(){
        return leaf;
    }

    public void setLeaf(Boolean leaf){
        this.leaf = leaf;
    }

    public Long getSupersetId() {
        return supersetId;
    }

    public void setSupersetId(Long supersetId) {
        this.supersetId = supersetId;
    }

    public Grade(){
    }

    public Grade(Long id, String name, Boolean leaf){
        super(id, name);
        this.setLeaf(leaf);
    }

    public Grade(Long id, String name, Boolean leaf, Long supersetId){
        super(id, name);
        this.setLeaf(leaf);
        this.setSupersetId(supersetId);
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", leaf=" + leaf +
                ", supersetId=" + supersetId +
                '}';
    }

    public static List<Grade> gradeList;
    public static Map<Long, Grade> gradeMap;
    static{
        gradeList = new ArrayList<Grade>();

        gradeList.add(new Grade(1L, "小学", false));
        gradeList.add(new Grade(2L, "一年级", true, PRIMARY_ID));
        gradeList.add(new Grade(3L, "二年级", true, PRIMARY_ID));
        gradeList.add(new Grade(4L, "三年级", true, PRIMARY_ID));
        gradeList.add(new Grade(5L, "四年级", true, PRIMARY_ID));
        gradeList.add(new Grade(6L, "五年级", true, PRIMARY_ID));
        gradeList.add(new Grade(7L, "六年级", true, PRIMARY_ID));

        gradeList.add(new Grade(8L, "初中", false));
        gradeList.add(new Grade(9L, "初一", true, MIDDLE_ID));
        gradeList.add(new Grade(10L, "初二", true, MIDDLE_ID));
        gradeList.add(new Grade(11L, "初三", true, MIDDLE_ID));

        gradeList.add(new Grade(12L, "高中", false));
        gradeList.add(new Grade(13L, "高一", true, SENIOR_ID));
        gradeList.add(new Grade(14L, "高二", true, SENIOR_ID));
        gradeList.add(new Grade(15L, "高三", true, SENIOR_ID));

        gradeMap = new HashMap<>(gradeList.size()*2);
        for(Grade g: gradeList) {
            gradeMap.put(g.getId(), g);
        }
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
    public static Grade getGradeById(Long id){
        return gradeMap.get(id);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeValue(this.leaf);
        dest.writeValue(this.supersetId);
    }

    protected Grade(Parcel in) {
        super(in);
        this.leaf = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.supersetId = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<Grade> CREATOR = new Creator<Grade>() {
        public Grade createFromParcel(Parcel source) {
            return new Grade(source);
        }

        public Grade[] newArray(int size) {
            return new Grade[size];
        }
    };
}
