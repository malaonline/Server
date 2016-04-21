//
//  SchoolModel.swift
//  mala-ios
//
//  Created by Elors on 1/12/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit
import CoreLocation

class SchoolModel: BaseObjectModel {

    // MARK: - Property
    var address: String?
    var thumbnail: String?
    var region: Int = 0
    var center: Bool = false
    var longitude: NSNumber? {
        didSet {
            getDistance()
        }
    }
    var latitude: NSNumber? {
        didSet {
            getDistance()
        }
    }
    var distance: Double = 0.0
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }

    override init(dict: [String: AnyObject]) {
        super.init(dict: dict)
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(id: Int, name: String, address: String) {
        self.init()
        self.id = id
        self.name = name
        self.address = address
    }
    
    convenience init(id: Int, name: String, address: String, thumbnail: String?, region: Int?, center: Bool?, longitude: Int?, latitude: Int?) {
        self.init()
        self.id = id
        self.name = name
        self.address = address
        self.thumbnail = thumbnail
        self.region = region ?? 0
        self.center = center ?? false
        self.longitude = longitude
        self.latitude = latitude
    }
    
    private func getDistance() {
        guard longitude != nil && latitude != nil && MalaLoginLocation != nil else {
            return
        }
        
        // 计算上课地点距用户当前位置距离
        let targetLocation = CLLocation(latitude: latitude!.doubleValue, longitude: longitude!.doubleValue)
        let distance: CLLocationDistance = MalaLoginLocation?.distanceFromLocation(targetLocation) ?? 0.0
        self.distance = distance
    }
    
    
    // MARK: - Override
    override func setValue(value: AnyObject?, forUndefinedKey key: String) {
        println("SchoolModel - Set for UndefinedKey: \(key)")
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["address", "thumbnail", "region", "center", "longitude", "latitude"]
        return super.description + dictionaryWithValuesForKeys(keys).description
    }
}