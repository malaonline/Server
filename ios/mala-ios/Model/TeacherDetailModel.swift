//
//  TeacherDetailModel.swift
//  mala-ios
//
//  Created by Elors on 12/29/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailModel: BaseObjectModel {
    
    // MARK: - Variables
    var avatar: String?
    var gender: String?
    var degree: String?
    var teaching_age: Int = 0
    var level: String?
    var subject: String?
    var grades: [String] = []
    var tags: [String] = []
    var photo_set: [String]? = []
    var certificate_set: [String]? = []
    var highscore_set: [HighScoreModel]? = []
    var prices: [GradePriceModel] = []
    
    
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
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["avatar", "gender", "degree", "teaching_age", "level", "subject", "grades", "tags", "photo_set", "certificate_set", "highscore_set", "prices"]
        return super.description + dictionaryWithValuesForKeys(keys).description
    }
}
