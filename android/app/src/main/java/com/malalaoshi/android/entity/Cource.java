package com.malalaoshi.android.entity;

import com.malalaoshi.android.adapter.SimpleMonthAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kang on 16/2/17.
 */
public class Cource {
    private Integer id;
    private String subject;
    private boolean is_passed;
    private Long end;
    private SimpleMonthAdapter.CalendarDay data;

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public void setIs_passed(boolean is_passed) {
        this.is_passed = is_passed;
    }
    public boolean is_passed() {
        return is_passed;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public SimpleMonthAdapter.CalendarDay getData() {
        return data;
    }

    public void setData(SimpleMonthAdapter.CalendarDay data) {
        this.data = data;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public static List<Cource> getCources(List<Cource> cources,SimpleMonthAdapter.CalendarDay data) {
        List<Cource>  courceList = new ArrayList<>();
        for (int i=0;i<cources.size();i++){
            if (cources.get(i).getData().equals(data)){
                courceList.add(cources.get(i));
            }
        }
        return courceList;
    }

    //将List<Cource>以"year+month"为Key方式存储
    public static Map<String, List<Cource>> getCourses(List<Cource> cources){
        Map<String, List<Cource>> mapCourse = new HashMap<>();
        for (int i=0;i<cources.size();i++){
            SimpleMonthAdapter.CalendarDay calendar = cources.get(i).getData();
            List<Cource> listCourse = mapCourse.get(calendar.getYear()+calendar.getMonth());
            if (listCourse==null){
                listCourse = new ArrayList<>();
                mapCourse.put(calendar.getYear()+calendar.getMonth()+"",listCourse);
            }
            listCourse.add(cources.get(i));
        }
        return mapCourse;
    }
}
