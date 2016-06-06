//
//  ThemeDate.swift
//  mala-ios
//
//  Created by 王新宇 on 1/28/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit
import DateTools

class ThemeDate {

    ///  根据提供的课表对象和课时数量，返回对应的上课时间表（字符串数组）
    ///
    ///  - parameter days:   课表对象, 注意调整课表对象的id为对应的星期数。（0~6, 对应星期日~星期六）
    ///  - parameter Period: 课时数量（基数应为[课表对象]数量）
    ///
    ///  - returns: 上课时间表（字符串数组）
    class func dateArray(days: [ClassScheduleDayModel], period: Int) -> [String] {
        
        /// 上课时间表字符串数组
        var timeSchedule: [String] = []
        /// 课表数组排序
        let sortDays = days.sort { (model0, model1) -> Bool in
            return (model0.weekID == 0 ? 7 : model0.weekID) < (model1.weekID == 0 ? 7 : model1.weekID)
        }
        /// 课程表时间模型数组
        var modelArray = sortDays
        /// 课时数
        let classPeriod = period%2 == 0 ? period : period+1
        /// 当前Date对象
        let today = NSDate()
        // 若首次购课，则[计算上课时间]需要间隔两天，以用于用户安排[建档测评服务]
        let intervals = MalaIsHasBeenEvaluatedThisSubject == true ? 3 : 1
        
        
        
        
        
        

        return timeSchedule
    }
    
}