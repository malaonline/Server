//
//  BaseObjectModel.swift
//  mala-ios
//
//  Created by Elors on 15/12/21.
//  Copyright © 2015年 Mala Online. All rights reserved.
//

import UIKit

public class BaseObjectModel: NSObject {

    // MARK: - Property
    var id: Int = 0
    var name: String?
    

    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(id: Int, name: String) {
        self.init()
        self.id = id
        self.name = name
    }
    
    // MARK: - Override
    override public func setValue(value: AnyObject?, forUndefinedKey key: String) {
        debugPrint("BaseObjectModel - Set for UndefinedKey: \(key)")
    }
    
    
    // MARK: - Description
    override public var description: String {
        let keys = ["id", "name"]
        return dictionaryWithValuesForKeys(keys).description
    }
}
