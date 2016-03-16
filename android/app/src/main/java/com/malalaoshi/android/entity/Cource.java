package com.malalaoshi.android.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.malalaoshi.android.adapter.SimpleMonthAdapter;
import com.malalaoshi.android.util.CalendarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kang on 16/2/17.
 */
public class Cource implements Parcelable {
    private Integer id;
    private String subject;
    private boolean is_passed;
    private Long start;
    private Long end;
    private boolean is_commentted;
    private String school;
    private Teacher teacher;
    private Comment comment;

    public Cource() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public boolean is_passed() {
        return is_passed;
    }

    public void setIs_passed(boolean is_passed) {
        this.is_passed = is_passed;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public boolean is_commentted() {
        return is_commentted;
    }

    public void setIs_commentted(boolean is_commentted) {
        this.is_commentted = is_commentted;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public static List<Cource> getCources(List<Cource> cources,SimpleMonthAdapter.CalendarDay data) {
        List<Cource>  courceList = new ArrayList<>();
        for (int i=0;i<cources.size();i++){
            if ((CalendarUtils.timestampToCalendarDay(cources.get(i).getEnd())).equals(data)){
                courceList.add(cources.get(i));
            }
        }
        return courceList;
    }

    //将List<Cource>以"year+month"为Key方式存储
    public static Map<String, List<Cource>> getCourses(List<Cource> cources){
        Map<String, List<Cource>> mapCourse = new HashMap<>();
        for (int i=0;i<cources.size();i++){
            SimpleMonthAdapter.CalendarDay calendar = (CalendarUtils.timestampToCalendarDay(cources.get(i).getEnd()));
            List<Cource> listCourse = mapCourse.get(calendar.getYear()+calendar.getMonth());
            if (listCourse==null){
                listCourse = new ArrayList<>();
                mapCourse.put(calendar.getYear()+calendar.getMonth()+"",listCourse);
            }
            listCourse.add(cources.get(i));
        }
        return mapCourse;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.subject);
        dest.writeByte(is_passed ? (byte) 1 : (byte) 0);
        dest.writeValue(this.start);
        dest.writeValue(this.end);
        dest.writeByte(is_commentted ? (byte) 1 : (byte) 0);
        dest.writeString(this.school);
        dest.writeParcelable(this.teacher, 0);
        dest.writeParcelable(this.comment, flags);
    }

    protected Cource(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.subject = in.readString();
        this.is_passed = in.readByte() != 0;
        this.start = (Long) in.readValue(Long.class.getClassLoader());
        this.end = (Long) in.readValue(Long.class.getClassLoader());
        this.is_commentted = in.readByte() != 0;
        this.school = in.readString();
        this.teacher = in.readParcelable(Teacher.class.getClassLoader());
        this.comment = in.readParcelable(Comment.class.getClassLoader());
    }

    public static final Parcelable.Creator<Cource> CREATOR = new Parcelable.Creator<Cource>() {
        public Cource createFromParcel(Parcel source) {
            return new Cource(source);
        }

        public Cource[] newArray(int size) {
            return new Cource[size];
        }
    };
}
