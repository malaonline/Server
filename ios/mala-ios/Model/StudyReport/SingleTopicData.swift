//
//  SingleTopicData.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/31.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class SingleTopicData: NSObject {
    
    // MARK: - Property
    /// 题目类型id
    var id: String = ""
    /// 题目名称
    var name: String = ""
    /// 所做此题目练习总数
    var total_item: Int = 0
    /// 所做此题目练习正确数
    var right_item: Int = 0
    /// 正确率
    var rightRate: Double {
        get {
            return Double(right_item)/Double(total_item == 0 ? 1 : total_item)
        }
    }
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(id: String, name: String, totalItem: Int, rightItem: Int) {
        self.init()
        self.id = id
        self.name = name
        self.total_item = totalItem
        self.right_item = rightItem
    }
}