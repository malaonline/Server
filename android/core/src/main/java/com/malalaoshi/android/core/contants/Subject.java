package com.malalaoshi.android.core.contants;

import com.malalaoshi.android.core.utils.EmptyUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 课程id对应表
 * Created by tianwei on 16-8-1.
 */
public class Subject {
    public static Map<Long, String> SUBJECT_LIST;

    static {
        SUBJECT_LIST = new HashMap<>();

        SUBJECT_LIST.put(1L, "数学");
        SUBJECT_LIST.put(2L, "英语");
        SUBJECT_LIST.put(3L, "语文");
        SUBJECT_LIST.put(4L, "物理");
        SUBJECT_LIST.put(5L, "化学");
        SUBJECT_LIST.put(6L, "地理");
        SUBJECT_LIST.put(7L, "历史");
        SUBJECT_LIST.put(8L, "政治");
        SUBJECT_LIST.put(9L, "生物");
    }

    public static String getSubjectName(long id) {
        String name = SUBJECT_LIST.get(id);
        return EmptyUtils.isEmpty(name) ? "" : name;
    }

}
