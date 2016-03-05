//
//  TeacherDetailModel.swift
//  mala-ios
//
//  Created by Elors on 12/29/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailModel: BaseObjectModel {
    
    // MARK: - Property
    var avatar: String?
    var gender: String?
    var degree: String?
    var teaching_age: Int = 0
    var level: String?
    var subject: String?
    var grades: [String] = []
    var tags: [String] = []
    var photo_set: [String]? = []
    var achievement_set: [AchievementModel?] = []
    var highscore_set: [HighScoreModel?] = []
    var prices: [GradePriceModel?] = []
    var min_price: Int = 0
    var max_price: Int = 0
    
    
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
        debugPrint("TeacherDetailModel - Set for UndefinedKey: \(key)")
    }
    
    override func setValue(value: AnyObject?, forKey key: String) {
        if key == "highscore_set" {
            if let dicts = value as? [[String: AnyObject]] {
                var tempDict: [HighScoreModel?] = []
                for dict in dicts {
                    let set = HighScoreModel(dict: dict)
                    tempDict.append(set)
                }
                highscore_set = tempDict
            }
            return
        }
        if key == "achievement_set" {
            if let dicts = value as? [[String: AnyObject]] {
                var tempDict: [AchievementModel?] = []
                for dict in dicts {
                    let set = AchievementModel(dict: dict)
                    tempDict.append(set)
                }
                achievement_set = tempDict
            }
            return
        }
        if key == "prices" {
            if let dicts = value as? [[String: AnyObject]] {
                var tempDict: [GradePriceModel?] = []
                for dict in dicts {
                    let set = GradePriceModel(dict: dict)
                    tempDict.append(set)
                }
                prices = tempDict
            }
            return
        }
        super.setValue(value, forKey: key)
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["avatar", "gender", "degree", "teaching_age", "level", "subject", "grades", "tags",
            "photo_set", "achievement_set", "highscore_set", "prices", "min_price", "max_price"]
        return super.description + dictionaryWithValuesForKeys(keys).description
    }
}
