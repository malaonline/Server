//
//  SingleHomeworkData.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/31.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class SingleHomeworkData: NSObject {
    
    // MARK: - Property
    /// 作业类型id
    var id: Int = 0
    /// 作业名称
    var name: String = ""
    /// 比率（错题比率）
    var rate: NSNumber = 0
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
}