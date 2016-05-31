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
    var id: Int = 0
    /// 题目名称
    var name: String = ""
    /// 所做此题目练习总数
    var totalItem: Int = 0
    /// 所做此题目练习正确数
    var rightItem: Int = 0
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
}