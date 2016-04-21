//
//  GradePriceModel.swift
//  mala-ios
//
//  Created by Elors on 12/29/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class GradePriceModel: NSObject {

    // MARK: - Property
    var grade: BaseObjectModel?
    var price: Int = 0
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(name: String, id: Int, price: Int) {
        self.init()
        let grade = BaseObjectModel(id: id, name: name)
        self.grade = grade
        self.price = price
    }
    
    
    // MARK: - Override
    override func setValue(value: AnyObject?, forUndefinedKey key: String) {
        println("GradePriceModel - Set for UndefinedKey: \(key)")
    }
    
    override func setValue(value: AnyObject?, forKey key: String) {
        if key == "grade" {
            if let dict = value as? [String: AnyObject] {
                let model = BaseObjectModel(dict: dict)
                grade = model
            }
            return
        }
        super.setValue(value, forKey: key)
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["grade", "price"]
        return dictionaryWithValuesForKeys(keys).description
    }
}
