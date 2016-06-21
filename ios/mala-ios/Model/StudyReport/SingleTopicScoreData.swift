//
//  SingleTopicScoreData.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/31.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class SingleTopicScoreData: NSObject {
    
    // MARK: - Property
    /// 提分点id
    var id: Int = 0
    /// 提分点名称
    var name: String = ""
    /// 用户分数
    var score: NSNumber = 0
    /// 所有用户平均分数
    var aveScore: NSNumber = 0
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(id: Int, name: String, score: NSNumber, aveScore: NSNumber) {
        self.init()
        self.id = id
        self.name = name
        self.score = score
        self.aveScore = aveScore
    }
    
    override var description: String {
        return "id: \(id), name: \(name), score: \(score), aveScore: \(aveScore)\n"
    }
}