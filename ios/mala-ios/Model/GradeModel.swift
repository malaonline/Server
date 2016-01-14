//
//  GradeModel.swift
//  mala-ios
//
//  Created by Elors on 12/21/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class GradeModel: BaseObjectModel {

    // MARK: - Property
    var subset: [GradeModel]? = []
    var subjects: [NSNumber] = []
    
    
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
        debugPrint("GradeModel - Set for UndefinedKey: \(key)")
    }
    
    override func setValue(value: AnyObject?, forKey key: String) {
        if key == "subset" {
            if let dicts = value as? [[String: AnyObject]] {
                var tempDict: [GradeModel]? = []
                for dict in dicts {
                    let set = GradeModel(dict: dict)
                    tempDict?.append(set)
                }
                subset = tempDict
            }
            return
        }
        super.setValue(value, forKey: key)
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["id", "name", "subset", "subjects"]
        return dictionaryWithValuesForKeys(keys).description
    }
}
