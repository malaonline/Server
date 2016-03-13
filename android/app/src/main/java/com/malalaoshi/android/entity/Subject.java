package com.malalaoshi.android.entity;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zl on 15/12/14.
 */
public class Subject extends BaseEntity {

    public Subject() {
    }

    public Subject(Long id, String name) {
        super(id, name);
    }

    @Override
    public String toString() {
        return "Subject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public static List<Subject> subjectList;
    public static Map<Long, Subject> subjectMap;

    static {
        subjectList = new ArrayList<>();

        subjectList.add(new Subject(1L, "数学"));
        subjectList.add(new Subject(2L, "英语"));
        subjectList.add(new Subject(3L, "语文"));
        subjectList.add(new Subject(4L, "物理"));
        subjectList.add(new Subject(5L, "化学"));
        subjectList.add(new Subject(6L, "地理"));
        subjectList.add(new Subject(7L, "历史"));
        subjectList.add(new Subject(8L, "政治"));
        subjectList.add(new Subject(9L, "生物"));

        updateSubjectDict();
    }

    public static void updateSubjectDict() {
        if (subjectList == null) {
            return;
        }
        if (subjectMap == null) {
            subjectMap = new HashMap<>(subjectList.size() * 2);
        } else {
            subjectMap.clear();
        }
        for (Subject s : subjectList) {
            subjectMap.put(s.getId(), s);
        }
    }

    public static Subject getSubjectById(Long id) {
        if (subjectMap == null) {
            return getSubjectFromListById(id, subjectList);
        }
        return subjectMap.get(id);
    }

    public static Subject getSubjectIdByName(String name) {
        if (subjectMap != null) {
            for (Map.Entry<Long, Subject> entry : subjectMap.entrySet()) {
                if (entry.getValue().getName().equals(name)) {
                    return entry.getValue();
                }
            }
        }
        for (Subject subject : subjectList) {
            if (subject.getName().equals(name)) {
                return subject;
            }
        }
        return null;
    }

    public static Subject getSubjectFromListById(Long id, List<Subject> subjectList) {
        if (id == null || subjectList == null) {
            return null;
        }
        for (Subject subject : subjectList) {
            if (id.equals(subject.getId())) {
                return subject;
            }
        }
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    protected Subject(Parcel in) {
        super(in);
    }

    public static final Creator<Subject> CREATOR = new Creator<Subject>() {
        public Subject createFromParcel(Parcel source) {
            return new Subject(source);
        }

        public Subject[] newArray(int size) {
            return new Subject[size];
        }
    };
}
