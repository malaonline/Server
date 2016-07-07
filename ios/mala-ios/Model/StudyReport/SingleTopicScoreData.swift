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
    var id: String = ""
    /// 提分点名称
    var name: String = ""
    /// 用户分数
    var my_score: NSNumber = 0
    /// 所有用户平均分数
    var ave_score: NSNumber = 0
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(id: String, name: String, score: NSNumber, aveScore: NSNumber) {
        self.init()
        self.id = id
        self.name = name
        self.my_score = score
        self.ave_score = aveScore
    }
    
    override var description: String {
        return "id: \(id), name: \(name), score: \(my_score), aveScore: \(ave_score)\n"
    }
}