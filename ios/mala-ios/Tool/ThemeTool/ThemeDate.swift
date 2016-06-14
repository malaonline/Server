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
        /// 课时数
        var classPeriod = period%2 == 0 ? period : period+1
        
        var index = 0
        var currentWeekAdding = 0
        
        repeat {
            
            let singleDate = sortDays[index]
            let firstAvailableDate = ThemeDate().getFirstAvailableDate(singleDate).dateByAddingWeeks(currentWeekAdding)
            
            let dateString = getDateString(date: firstAvailableDate, format: "yyyy-MM-dd")
            let startString = getDateString(date: firstAvailableDate, format: "HH:mm")
            let endString = getDateString(date: firstAvailableDate.dateByAddingHours(2), format: "HH:mm")
            
            timeSchedule.append(String(format: "%@ (%@-%@)", dateString, startString, endString))
            
            classPeriod -= 2
            index += 1
            
            if index == days.count {
                index = 0
                currentWeekAdding += 1
            }
            
        } while classPeriod > 0
        
        return timeSchedule
    }
    
    
    ///  获取指定上课时间的首个有效开始上课时间
    ///  (例如：若A老师被买连续三周1节课，则返回第四周的该节课的开始时间)
    ///
    ///  - parameter timeSlot: 上课时间模型
    ///
    ///  - returns: 首个有效开始上课时间
    private func getFirstAvailableDate(timeSlot: ClassScheduleDayModel) -> NSDate {
        
        if let lastDateTimeInterval = timeSlot.last_occupied_end {
            var lastDate = NSDate(timeIntervalSince1970: lastDateTimeInterval.doubleValue)
            // 下一周的课程开始时间
            lastDate = lastDate.dateByAddingWeeks(1)
            lastDate = lastDate.dateBySubtractingHours(2)
            return lastDate
        }else {
            
            var date = MalaConfig.malaWeekdays()[timeSlot.weekID].dateInThisWeek()
            
            // 只有提前两天以上的课程，才会在本周开始授课。
            //（例如在周五预约了周日的课程，仅相隔周六一天不符合要求，将从下周日开始上课）
            //（例如在周四预约了周日的课程，相隔周五、周六两天符合要求，将从本周日开始上课）
            if !couldStartInThisWeek(timeSlot) {
                date = date.dateByAddingWeeks(1)
            }
            
            let startDateString = getDateString(date: date, format: "yyyy-MM-dd")
            let dateString = startDateString + " " + (timeSlot.start ?? "")
            return dateString.dateWithFormatter("yyyy-MM-dd HH:mm")!
        }
    }
    
    
    ///  验证指定上课时间是否可在本周开始上课
    ///
    ///  - parameter timeSlot: 上课时间模型
    ///
    ///  - returns: 可否上课结果
    private func couldStartInThisWeek(timeSlot: ClassScheduleDayModel) -> Bool {
        
        /// 若首次购课，则[计算上课时间]需要间隔两天，以用于用户安排[建档测评服务]
        let intervals = MalaIsHasBeenEvaluatedThisSubject == true ? 2 : 0
        let weekId = timeSlot.weekID == 0 ? 7 : timeSlot.weekID
        let date = MalaConfig.malaWeekdays()[timeSlot.weekID].dateInThisWeek()
        
        /// 若首次购课
        if weekId < (weekdayInt(NSDate())+intervals) {
            return false
        }
        
        let dateString = getDateString(date: date, format: "yyyy-MM-dd") + " " + (timeSlot.start ?? "")
        let selectedDate = dateString.dateWithFormatter("yyyy-MM-dd HH:mm")!
        
        // 若所选时间与当前时间相距不足1小时，则从下周开始计算
        if selectedDate.hoursFrom(NSDate()) < 1 {
            return false
        }
        return true
    }
}