//
//  GradeModel.swift
//  mala-ios
//
//  Created by Elors on 12/21/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class GradeModel: BaseObjectModel {

    // MARK: - Variable
    var subset: [GradeModel]? = []
    var subjects: [Int] = []
    
    
    // MARK: - Constructed
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
                for dict in dicts {
                    let set = GradeModel(dict: dict)
                    subset?.append(set)
                }
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
