//
//  TimeScheduleModel.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/27.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class TimeScheduleModel: NSObject {
    
    // MARK: - Property
    /// 日期（粒度为天）
    var date: NSDate = NSDate()
    /// 一组上课时间（［开始时间, 结束时间］）
    var times: [[NSDate]] = []
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }

    
    // MARK: - Description
    override var description: String {
        let string = "{\"date\":\(date), \"times\":\(times.description)}"
        return "\n"+string+"\n"
    }
}