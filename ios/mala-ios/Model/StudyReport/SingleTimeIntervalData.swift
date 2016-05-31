//
//  SingleTimeIntervalData.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/31.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class SingleTimeIntervalData: NSObject {
    
    // MARK: - Property
    /// 答题数量
    var totalItem: Int = 0
    /// 错题数量
    var errorItem: Int = 0
    /// 年份
    var year: Int = 0
    /// 月份
    var month: Int = 0
    /// 日期（为1/16，对应标记此数据是上旬／下旬）
    var day: Int = 0
    
    // 上下旬文字（上／下）
    var periodString: String {
        get {
            return day == 1 ? "上" : "下"
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
    
    convenience init(totalItem: Int, errorItem: Int, year: Int, month: Int, day: Int) {
        self.init()
        self.totalItem = totalItem
        self.errorItem = errorItem
        self.year = year
        self.month = month
        self.day = day
    }
}