//
//  GradePriceModel.swift
//  mala-ios
//
//  Created by Elors on 12/29/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class GradePriceModel: NSObject {

    // MARK: - Variables
    var grade: BaseObjectModel?
    var price: Int?
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    convenience init(name: String, id: Int, price: Int) {
        self.init()
        let grade = BaseObjectModel(id: id, name: name)
        self.grade = grade
        self.price = price
    }
    
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    
    // MARK: - Override
    override func setValue(value: AnyObject?, forUndefinedKey key: String) {
        debugPrint("GradePriceModel - Set for UndefinedKey: \(key)")
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["grade", "price"]
        return dictionaryWithValuesForKeys(keys).description
    }
}
