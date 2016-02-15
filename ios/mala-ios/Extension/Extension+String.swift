//
//  Extension+String.swift
//  mala-ios
//
//  Created by 王新宇 on 1/28/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import Foundation

extension String {
    
    ///  返回文字对应星期在本周的NSDate对象
    ///  (文字需要符合指定格式,like"周一")
    ///  本周星期范围为周一到周日
    ///
    ///  - returns: NSDate对象
    func dateInThisWeek() -> NSDate {
        
        let today = NSDate()
        let todayWeekInt = weekdayInt(today)
        let targetWeekInt = (MalaWeekdays.indexOf(self) == 0 ? 7 : MalaWeekdays.indexOf(self))
//        assert((targetWeekInt == nil), "String.dateInThisWeek StringFormat Error")
        
        // 若指定日期为今天
        if todayWeekInt == targetWeekInt {
            return today
        }
        // 若为今天之前
        if todayWeekInt > targetWeekInt {
            let days = todayWeekInt - targetWeekInt!
            return today.dateBySubtractingDays(days)
        }
        // 若为今天之后
        if todayWeekInt < targetWeekInt {
            let days = targetWeekInt! - todayWeekInt
            return today.dateByAddingDays(days)
        }
        return today
    }
}