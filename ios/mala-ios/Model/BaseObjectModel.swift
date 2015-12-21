//
//  BaseObjectModel.swift
//  mala-ios
//
//  Created by Elors on 15/12/21.
//  Copyright © 2015年 Mala Online. All rights reserved.
//

import UIKit

class BaseObjectModel: NSObject {

    // MARK: - Variable
    var id: Int = 0
    var name: String?
    
    
    // MARK: - Constructed
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    
    // MARK: - Override
    override func setValue(value: AnyObject?, forUndefinedKey key: String) {
        debugPrint("BaseObjectModel - Set for UndefinedKey: \(key)")
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["id", "name"]
        return dictionaryWithValuesForKeys(keys).description
    }
    
}
