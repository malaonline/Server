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
        
        /// 上课时间表
        var timeSchedule: [String] = []
        /// 课表数组排序
        let sortDays = days.sort { (model0, model1) -> Bool in
            return (model0.id == 0 ? 7 : model0.id) < (model1.id == 0 ? 7 : model1.id)
        }
        /// 课程表时间模型数组
        var modelArray = sortDays
        /// 课时数
        var classPeriod = period
        /// 当前Date对象
        let today = NSDate()
        /// 推迟周数
        var postpone = 0
        
        
        // 过滤出本周可上课的时间对象（星期数大于今天）
        var classArrayInThisWeek: [ClassScheduleDayModel] = []
        for model in modelArray {
            let id = (model.id == 0 ? 7 : model.id)
            // 若首次购课，则[计算上课时间]需要间隔两天，以用于用户安排[建档测评服务]
            let intervals = MalaIsHasBeenEvaluatedThisSubject == true ? 3 : 0
            // 只有提前三天以上的课程，才会在本周开始授课。
            //（例如在周五预约了周日的课程，仅相隔周六一天不符合要求，将从下周日开始上课）
            //（例如在周四预约了周日的课程，相隔周五、周六两天符合要求，将从本周日开始上课）
            if id >= (weekdayInt(today)+intervals) {
                classArrayInThisWeek.append(model)
                modelArray.removeAtIndex(modelArray.indexOf(model)!)
            }else {
                // 加上间隔时间后推迟到下周的课程，推迟周数+1
                postpone++
            }
        }
        // 将本周即可上课的日期字符串，添加到[上课时间表]中
        for model in classArrayInThisWeek {
            let date = MalaWeekdays[model.id].dateInThisWeek()
            var dateString = date.formattedDateWithFormat("YYYY/MM/dd")
            // dateString = dateString + String(format: " ( %@-%@ ) ", model.start!, model.end!)
            dateString = dateString + String(format: " %@ ( %@-%@ ) ", MalaWeekdays[model.id], model.start!, model.end!)
            timeSchedule.append(dateString)
            classArrayInThisWeek.removeAtIndex(classArrayInThisWeek.indexOf(model)!)
            classPeriod = classPeriod - 2
        }
        // 若课时循环完毕则返回字符串数组
        if classPeriod == 0 || days.count == 0{
            return timeSchedule
        }

        /// 遍历剩余的课时数，将剩余的上课日期字符串，添加到[上课时间表]中
        /// 数组下标计数
        var index = 0
        /// 遍历循环次数（除本周外的上课周数）
        var weeks = 1
        for _ in 1...(classPeriod - classArrayInThisWeek.count*2)/2 {
            let model = sortDays[index]
            var date = MalaWeekdays[model.id].dateInThisWeek()
            // 获取到对应的本周日期后，加上对应的周数即为上课时间。
            if postpone != 0 && (model.id == 1 || model.id == 2){
                date = date.dateByAddingWeeks(2*weeks)
            }else {
                date = date.dateByAddingWeeks(1*weeks)
            }
            var dateString = date.formattedDateWithFormat("YYYY/MM/dd")
            // dateString = dateString + String(format: " ( %@-%@ ) ", model.start!, model.end!)
            dateString = dateString + String(format: " %@ ( %@-%@ ) ", MalaWeekdays[model.id], model.start!, model.end!)
            timeSchedule.append(dateString)
            
            // 累加计数器。超出数组下标归零，循环次数+1
            index++
            if index == days.count {
                index = 0
                weeks++
            }
        }
        return timeSchedule
    }
}