//
//  AchievementModel.swift
//  mala-ios
//
//  Created by 王新宇 on 2/17/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class AchievementModel: NSObject {

    // MARK: - Property
    var title: String?
    var img: NSURL?
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(title: String, img: NSURL) {
        self.init()
        self.title = title
        self.img = img
    }
    
    // MARK: - Override
    override func setValue(value: AnyObject?, forUndefinedKey key: String) {
        println("AchievementModel - Set for UndefinedKey: \(key)")
    }
    
    override func setValue(value: AnyObject?, forKey key: String) {
        if key == "img" {
            if let urlString = value as? String {
                img = NSURL(string: urlString)
            }
            return
        }
        super.setValue(value, forKey: key)
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["title", "img"]
        return dictionaryWithValuesForKeys(keys).description
    }
}