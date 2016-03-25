//
//  MalaLocationService.swift
//  mala-ios
//
//  Created by 王新宇 on 3/25/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import CoreLocation

class MalaLocationService: NSObject, CLLocationManagerDelegate {

    // MARK: - Property
    /// 单例
    static let sharedManager = MalaLocationService()
    var afterUpdatedLocationAction: (CLLocation -> Void)?
    var currentLocation: CLLocation?
    
    lazy var locationManager: CLLocationManager = {
        let locationManager = CLLocationManager()
        locationManager.delegate = self
        locationManager.distanceFilter = kCLDistanceFilterNone
        locationManager.desiredAccuracy = kCLLocationAccuracyNearestTenMeters
        locationManager.pausesLocationUpdatesAutomatically = true
        locationManager.headingFilter = kCLHeadingFilterNone
        locationManager.requestWhenInUseAuthorization()
        return locationManager
    }()
    
    
    // MARK: - Method
    class func turnOn() {
        if (CLLocationManager.locationServicesEnabled()){
            println("开始获取地理位置")
            self.sharedManager.locationManager.startUpdatingLocation()
        }
    }
    
    class func turnOff() {
        if (CLLocationManager.locationServicesEnabled()){
            println("停止获取地理位置")
            self.sharedManager.locationManager.stopUpdatingLocation()
        }
    }
    
    func locationManager(manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        
        guard let newLocation = locations.last else {
            return
        }
        MalaLoginLocation = newLocation
        println("获取到地理位置 - \(MalaLoginLocation)")
        locationManager.stopUpdatingLocation()
    }
}
