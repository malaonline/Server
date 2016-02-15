//
//  ClassScheduleDayModel.swift
//  mala-ios
//
//  Created by 王新宇 on 1/26/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class ClassScheduleDayModel: BaseObjectModel {

    // MARK: - Property
    var start: String?
    var end: String?
    var available: Bool = false
    
    
    // MARK: - Instance Method
    override init() {
        super.init()
    }
    
    convenience init(id: Int, start: String, end: String, available: Bool) {
        self.init()
        self.id = id
        self.start = start
        self.end = end
        self.available = available
    }
    
    override init(dict: [String: AnyObject]) {
        super.init(dict: dict)
        setValuesForKeysWithDictionary(dict)
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["id", "start", "end", "available"]
        return dictionaryWithValuesForKeys(keys).description
    }
}
