//
//  MemberServiceModel.swift
//  mala-ios
//
//  Created by Elors on 1/12/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class MemberServiceModel: BaseObjectModel {

    // MARK: - Property
    var detail: String?
    var enbaled: Bool = false
    
    
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
        println("SchoolModel - Set for UndefinedKey: \(key)")
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["detail", "enbaled"]
        return super.description + dictionaryWithValuesForKeys(keys).description
    }
}
