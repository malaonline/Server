//
//  TeacherModel.swift
//  mala-ios
//
//  Created by Erdi on 12/23/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class TeacherModel: BaseObjectModel {
    
    // MARK: - Variable
    var avatar: NSURL?
    var gender: String?
    var degree: String?
    var min_price: Int?
    var max_price: Int?
    var subject: Int?
    var grades: [Int]?
    var tags: [Int]?
    
    // MARK: - Constructed
    override init(dict: [String: AnyObject]) {
        super.init(dict: dict)
        setValuesForKeysWithDictionary(dict)
    }
    
    // MARK: - Override
    override func setValue(value: AnyObject?, forUndefinedKey key: String) {
        debugPrint("TeacherModel - Set for UndefinedKey: \(key)")
    }
    
    override func setValue(value: AnyObject?, forKey key: String) {
        if key == "avatar" {
            if let urlString = value as? String {
                avatar = NSURL(string: urlString)
            }
            return
        }
        super.setValue(value, forKey: key)
    }
    
    // MARK: - Description
    override var description: String {
        let keys = ["id", "name", "avatar", "gender", "degree", "min_price", "max_price", "subject", "grades", "tags"]
        return dictionaryWithValuesForKeys(keys).description
    }
}