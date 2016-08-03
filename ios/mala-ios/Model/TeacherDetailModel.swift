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
    var level: Int = 1
    var subject: String?
    var grades: [String] = []
    var tags: [String] = []
    var photo_set: [String]? = []
    var achievement_set: [AchievementModel?] = []
    var highscore_set: [HighScoreModel?] = []
    var prices: [GradePriceModel?] = []
    var min_price: Int = 0
    var max_price: Int = 0
    var published: Bool = false
    var favorite: Bool = false
    
    
    // 视图变量
    var teachingAgeString: String {
        get {
            return String(format: "%d年", teaching_age)
        }
    }
    var levelString: String {
        get {
            return String(format: "T%d", level)
        }
    }
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    override init(dict: [String: AnyObject]) {
        super.init(dict: dict)
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(id: Int, name: String, avatar: String, gender: String, teaching_age: Int, level: Int, subject: String, grades: [String],
        tags: [String], photo_set: [String], achievement_set: [AchievementModel?], highscore_set: [HighScoreModel?], prices: [GradePriceModel?],
        minPrice: Int, maxPrice: Int) {
            self.init()
            self.id = id
            self.name = name
            self.avatar = avatar
            self.gender = gender
            self.teaching_age = teaching_age
            self.level = level
            self.subject = subject
            self.grades = grades
            self.tags = tags
            self.photo_set = photo_set
            self.achievement_set = achievement_set
            self.highscore_set = highscore_set
            self.prices = prices
            self.min_price = minPrice
            self.max_price = maxPrice
    }
    
    
    // MARK: - Override
    override func setValue(value: AnyObject?, forUndefinedKey key: String) {
        println("TeacherDetailModel - Set for UndefinedKey: \(key)")
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
            "photo_set", "achievement_set", "highscore_set", "prices", "min_price", "max_price", "published", "favorite"]
        return super.description + dictionaryWithValuesForKeys(keys).description
    }
}
