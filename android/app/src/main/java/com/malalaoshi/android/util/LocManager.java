package com.malalaoshi.android.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.core.MalaContext;

/**
 * Created by kang on 16/1/6.
 */
public class LocManager {
    private static final String TAG = "LocManager";
    private static LocManager instance = new LocManager();
    //定位相关对象
    private LocationManager locationManager;
    private Context mContext;
    private LocListener locationListener;
    private ReceiveLocationListener receiveLocationListener;

    //定位状态
    public final static int NOT_LOCATION = 0;   //未开始定位
    public final static int BEING_LOCATION = 1; //正在定位
    public final static int OK_LOCATION = 2;    //定位成功
    public final static int ERROR_LOCATION = 3; //定位失败
    private int locationStatus = NOT_LOCATION;

    //位置
    //location
    private Location mLocation;

    public int getLocationStatus() {
        return locationStatus;
    }

    public void unregisterLocationListener(ReceiveLocationListener listener) {
        receiveLocationListener = listener;
    }

    public void registerLocationListener(ReceiveLocationListener listener) {
        receiveLocationListener = null;
    }

    private LocManager() {
    }

    public static LocManager getInstance() {
        instance.mContext = MalaApplication.getInstance().getApplicationContext();
        instance.initLocation();
        return instance;
    }

    private void initLocation() {
        //通过上下文获得得到手机位置的系统服务，
        if (locationManager==null){
            locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        }
        if (locationListener==null){
            locationListener = new LocListener();
        }
    }

    public void start() {
        //mLocation = null;
        //开始定位
        locationStatus = BEING_LOCATION;
        //调用getProvider方法，获得最好的位置提供者
        String provider = getProvider(locationManager);
        //Location loc = locationManager.getLastKnownLocation(provider);
        //获得位置更新的操作
        //manager.requestLocationUpdates(provider, minTime, minDistance, listener)
        //其中的4个参数分别为：
        //provider：使用的定位设备，基站定位、GPS定位、网络定位等
        //minTime：多长时间更新一次定位信息，单位为毫秒，最少为一分钟
        //minDistance：位置移动了多少米之后，重新获取一次定位信息
        //listener：在位置发生变化时的回调方法。定义一个类（LocListener），实现LocationListener接口
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            locationStatus = ERROR_LOCATION;
            return;
        }
        locationManager.requestLocationUpdates(provider, 3000, 10, locationListener);
    }

    public void stop() {
        if (locationListener != null) {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(locationListener);
        }
    }

    private class LocListener implements LocationListener {

        @Override
        /**
         * 当位置发生变化时调用的方法
         */
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            if (location!=null){
                locationStatus = OK_LOCATION;
                mLocation = new Location(location);
            }else{
                MiscUtil.toast("定位失败!");
                locationStatus = ERROR_LOCATION;
            }

            if (receiveLocationListener!=null){
                receiveLocationListener.onReceiveLocation(location);
            }
            Log.i(TAG,"latitude:"+location.getLatitude()+" longtitude:"+location.getLongitude());
            //停止定位
            stop();
        }
        /**
         * 设备状态（可用、不可用）发生改变时回调的方法
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.i(TAG,"onStatusChanged, status:"+status+",provider"+provider);
        }

        @Override
        /**
         * 设备被禁用时的回调方法
         */
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub
            Log.i(TAG,"onProviderDisabled"+provider);

        }

        @Override
        /**
         * 设备被打开时的回调方法
         */
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub
            Log.i(TAG, "onProviderEnabled"+provider);
        }

    }
    /**
     * manager.requestLocationUpdates(provider, minTime, minDistance, listener)
     * 中的provider，即定位设备
     * @param manager 位置管理服务
     * @return 最好的位置提供者
     */
    private String getProvider(LocationManager manager) {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        /*Criteria criteria = new Criteria();
        // 设置精准度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        // 设置是否对海拔敏感
        criteria.setAltitudeRequired(false);
        // 设置对手机的耗电量，定位要求越高，越耗电
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        //设置对速度变化是否敏感
        criteria.setSpeedRequired(false);
        //设置在定位时，是否允许产生与运营商交换数据的开销
        criteria.setCostAllowed(true);*/
        //这个方法是用来得到最好的定位方式，它有两个参数
        //1、Criteria(类似于Map集合)，一组关于定位的条件，速度、海拔、耗电量等
        //2、enableOnly，布尔类型，false，有可能是已经关掉了的设备；true，就只会得到已经打开了的设备。
        //如果手机中的GPS设备已经关闭，那么如果设置为false，则手机有可能仍然使用GPS设备提供定位，
        //如果为true，则手机将不适应关闭的GPS设备定位，而是使用手机中开启的网络或其他设备提供定位
        return manager.getBestProvider(criteria, true);
    }

    public interface ReceiveLocationListener{
        public void onReceiveLocation(Location location);
    }

    public Location getLocation() {
        return mLocation;
    }
}
