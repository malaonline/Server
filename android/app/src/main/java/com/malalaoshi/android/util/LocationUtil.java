package com.malalaoshi.android.util;


import android.location.Location;

import com.malalaoshi.android.entity.School;

import java.util.Collections;
import java.util.List;

/**
 * Created by kang on 16/1/5.
 */
public class LocationUtil {
    public static double getDistance(double latitude1, double longitude1, double latitude2, double longitude2) {
        try {
            float[] results = new float[1];
            Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, results);
            return results[0];
        } catch (Exception e) {
            return -1.0D;
        }
    }

    public static void sortByRegion(List<School> list, double latitude, double longitude) {
        for (int i = 0; i < list.size(); i++) {
            School data = list.get(i);
            double distance = LocationUtil.getDistance(latitude, longitude, data.getLatitude(), data.getLongitude());
            data.setRegion(distance);
        }
        Collections.sort(list);
    }

    public static String formatRegion(double region) {
        region = region < 0 ? 0 : region;
        if (region < 1000) {
            return ((int) region) + "m";
        } else {
            return ((int) (region / 1000)) + "km";
        }
    }

}
