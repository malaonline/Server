package com.malalaoshi.android.util;


import com.malalaoshi.android.entity.School;

import java.util.Collections;
import java.util.List;

/**
 * Created by kang on 16/1/5.
 */
public class LocationUtil {
    public static double getDistance(double latitude1, double longitude1,double latitude2, double longitude2){
        /*LatLng latLng1 = new LatLng(latitude1,longitude1);
        LatLng latLng2 = new LatLng(latitude2,longitude2);
        return DistanceUtil.getDistance(latLng1,latLng2);*/
        return -1.0D;
    }

    public static void sortByRegion(List<School> list,double latitude, double longitude ){
        for (int i=0;i<list.size();i++){
            School data = list.get(i);
            double distance = LocationUtil.getDistance(latitude, longitude, data.getLatitude(), data.getLongitude());
            data.setRegion(distance);
        }
        Collections.sort(list);
    }

}
