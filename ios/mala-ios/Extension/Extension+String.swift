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
        let targetWeekInt = (MalaConfig.malaWeekdays().indexOf(self) == 0 ? 7 : MalaConfig.malaWeekdays().indexOf(self))
        
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
    
    ///  根据时间戳返回对应字符串（"yyyy.MM.dd"）
    ///
    ///  - parameter timeStamp: 时间戳
    ///
    ///  - returns: 字符串
    init(timeStamp: NSTimeInterval) {
        let date = NSDate(timeIntervalSince1970: timeStamp)
        let dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "yyyy.MM.dd"
        self = dateFormatter.stringFromDate(date)
    }
    
    func subStringToIndex(index: Int) -> String {
        if self.characters.count != 0 {
            return self.substringToIndex(self.startIndex.advancedBy(index))
        }else {
            return ""
        }
    }
    
    func subStringFromIndex(index: Int) -> String {
        if self.characters.count != 0 {
            return self.substringFromIndex(self.startIndex.advancedBy(index))
        }else {
            return ""
        }
    }
    
    ///  格式化字符串为日期数据
    ///
    ///  - parameter format: 字符串格式化规则
    ///
    ///  - returns: 日期数据
    func dateWithFormatter(format: String = "yyyy/MM/dd") -> NSDate? {
        let formatter = NSDateFormatter()
        formatter.dateFormat = format
        return formatter.dateFromString(self)
    }
    
    
    init(MinPrice min: String, MaxPrice max: String) {
        self = String(format: "%@-%@元/小时", min, max)
    }
    
    init(showDistance distance: Double) {
        if distance > 1000 {
            self = String(format: "%.1fkm", distance/1000)
        }else {
            self = String(format: "%.1fm", distance)
        }
    }
    
}