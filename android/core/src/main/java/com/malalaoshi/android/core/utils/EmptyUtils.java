package com.malalaoshi.android.core.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Empty utils
 * Created by tianwei on 4/17/16.
 */
public class EmptyUtils {

    public static boolean isEmpty(String value) {
        return value == null || value.trim().length() == 0;
    }

    public static boolean isNotEmpty(String value) {
        return !isEmpty(value);
    }

    public static <T> boolean isEmpty(Collection<T> list) {
        return list == null || list.size() == 0;
    }

    public static <T> boolean isNotEmpty(Collection<T> list) {
        return !isEmpty(list);
    }

    public static boolean isEmpty(Map<String, String> map) {
        return map == null || map.size() == 0;
    }

    public static boolean isNotEmpty(Map<String, String> map) {
        return !isEmpty(map);
    }
}
