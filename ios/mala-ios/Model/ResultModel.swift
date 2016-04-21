//
//  ResultModel.swift
//  mala-ios
//
//  Created by Elors on 12/21/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class ResultModel: NSObject {

    // MARK: - Property
    var count: NSNumber?
    var previous: NSNumber?
    var next: NSNumber?
    var results: [AnyObject]? {
        didSet {
            // make sure results can't be Empty Array
            if results != nil && results?.count == 0 {
                results = nil
            }
        }
    }
    var status_code: Int = 0
    var detail: String?
    
    
    // MARK: - Constructed
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    
    // MARK: - Override
    override func setValue(value: AnyObject?, forUndefinedKey key: String) {
        println("ResultModel - Set for UndefinedKey: \(key) -")
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["count", "next", "previous", "results"]
        return dictionaryWithValuesForKeys(keys).description
    }
}
