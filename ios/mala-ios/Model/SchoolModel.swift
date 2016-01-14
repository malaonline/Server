//
//  SchoolModel.swift
//  mala-ios
//
//  Created by Elors on 1/12/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class SchoolModel: BaseObjectModel {

    // MARK: - Property
    var address: String?
    var thumbnail: String?
    var region: Int = 0
    var center: Bool = false
    var longitude: NSNumber?
    var latitude: NSNumber?
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }

    override init(dict: [String: AnyObject]) {
        super.init(dict: dict)
        setValuesForKeysWithDictionary(dict)
    }
    
    
    // MARK: - Override
    override func setValue(value: AnyObject?, forUndefinedKey key: String) {
        debugPrint("SchoolModel - Set for UndefinedKey: \(key)")
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["address", "thumbnail", "region", "center", "longitude", "latitude"]
        return super.description + dictionaryWithValuesForKeys(keys).description
    }
}
