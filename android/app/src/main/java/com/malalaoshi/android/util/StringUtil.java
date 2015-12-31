package com.malalaoshi.android.util;

import com.malalaoshi.android.entity.BaseEntity;

import java.util.List;

/**
 * Created by liumengjun on 12/31/15.
 */
public class StringUtil {

    public static final String DEFAULT_SEPERATOR = " | ";

    public static String join(String[] ss) {
        return join(ss, DEFAULT_SEPERATOR);
    }

    public static String join(String[] ss, String spot) {
        if (ss == null || ss.length == 0) {
            return "";
        }
        if (spot == null) {
            spot = DEFAULT_SEPERATOR;
        }

        StringBuilder sb = new StringBuilder(ss.length * 8);
        for (String s : ss) {
            sb.append(s).append(spot);
        }
        sb.setLength(sb.length() - spot.length());

        return sb.toString();
    }

    public static String joinEntityName(List<? extends BaseEntity> entities) {
        return joinEntityName(entities, DEFAULT_SEPERATOR);
    }

    public static String joinEntityName(List<? extends BaseEntity> entities, String spot) {
        if (entities == null || entities.size() == 0) {
            return "";
        }
        if (spot == null) {
            spot = DEFAULT_SEPERATOR;
        }

        StringBuilder sb = new StringBuilder(entities.size() * 8);
        for (BaseEntity ele : entities) {
            sb.append(ele.getName()).append(spot);
        }
        sb.setLength(sb.length() - spot.length());

        return sb.toString();
    }
}
