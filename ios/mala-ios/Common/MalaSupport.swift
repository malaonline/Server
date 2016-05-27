//
//  MalaSupport.swift
//  mala-ios
//
//  Created by 王新宇 on 2/25/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import Foundation

// MARK: - Task
typealias CancelableTask = (cancel: Bool) -> Void

///  延迟执行任务
///
///  - parameter time: 延迟秒数(s)
///  - parameter work: 任务闭包
///
///  - returns: 任务对象闭包
func delay(time: NSTimeInterval, work: dispatch_block_t) -> CancelableTask? {
    
    var finalTask: CancelableTask?
    
    let cancelableTask: CancelableTask = { cancel in
        if cancel {
            finalTask = nil // key
            
        } else {
            dispatch_async(dispatch_get_main_queue(), work)
        }
    }
    
    finalTask = cancelableTask
    
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, Int64(time * Double(NSEC_PER_SEC))), dispatch_get_main_queue()) {
        if let task = finalTask {
            task(cancel: false)
        }
    }
    
    return finalTask
}

func cancel(cancelableTask: CancelableTask?) {
    cancelableTask?(cancel: true)
}


// MARK: - unregister

///  注销推送消息
func unregisterThirdPartyPush() {
    dispatch_async(dispatch_get_main_queue()) {
        JPUSHService.setAlias(nil, callbackSelector: nil, object: nil)
        UIApplication.sharedApplication().applicationIconBadgeNumber = 0
    }
}

///  清空缓存
func cleanCaches() {
    
}


// MARK: - Alert Message
func showSaveResult(viewController: UIViewController?, result: Bool, property: String) {
    guard viewController != nil else {
        return
    }
    
    dispatch_async(dispatch_get_main_queue()) { () -> Void in
        let string = result ? "成功" : "失败"
        _ = JSSAlertView().show(
            viewController!,
            title: String(format: "%@保存%@", property, string)
        )
    }
}

///  解析学生上课时间表
///
///  - returns: ClassScheduleViewController.model数据
func parseStudentCourseTable(courseTable: [StudentCourseModel]) -> [Int:[Int:[StudentCourseModel]]] {
    
    var monthDicts = [Int:[Int:[StudentCourseModel]]]()
    
    ///  遍历上课时间表
    for course in courseTable {
        
        let day = course.date.day()
        let month = course.date.month()
        
        println("当前遍历 - 月份: \(course.date.month()) - 日期:\(day)")
        
        if monthDicts[month] == nil {
            // 没有月份字典
            monthDicts[month] = [Int:[StudentCourseModel]]()
            monthDicts[month]![day] = [StudentCourseModel]()
            
        }else if monthDicts[month]![day] == nil {
            // 没有日期字典
            monthDicts[month]![day] = [StudentCourseModel]()
        }
        
        // 月份与日期均存在，添加数据到list
        monthDicts[month]![day]!.append(course)
    }
    return monthDicts
}

///  根据时间戳获取时间字符串（例如12:00）
///
///  - parameter timeStamp: 时间戳
///
///  - returns: 时间字符串
func getTimeString(timeStamp: NSTimeInterval) -> String {
    return NSDate(timeIntervalSince1970: timeStamp).formattedDateWithFormat("HH:mm")
}

///  根据时间戳获取时间字符串（例如2000/10/10）
///
///  - parameter timeStamp: 时间戳
///
///  - returns: 时间字符串
func getDateString(timeStamp: NSTimeInterval) -> String {
    return NSDate(timeIntervalSince1970: timeStamp).formattedDateWithFormat("yyyy/MM/dd")
}

///  根据时间戳获取时间字符串（例如2000/10/10 12:01:01）
///
///  - parameter timeStamp: 时间戳
///
///  - returns: 时间字符串
func getDateTimeString(timeStamp: NSTimeInterval) -> String {
    return NSDate(timeIntervalSince1970: timeStamp).formattedDateWithFormat("yyyy-MM-dd HH:mm:ss")
}


///  获取行距为8的文本
///
///  - parameter string: 文字
///
///  - returns: 文本样式
func getLineSpacingAttrString(string: String, lineSpace: CGFloat) -> NSAttributedString {
    let attrString = NSMutableAttributedString(string: string)
    let paragraphStyle = NSMutableParagraphStyle()
    paragraphStyle.lineSpacing = lineSpace
    attrString.addAttribute(NSParagraphStyleAttributeName, value: paragraphStyle, range: NSRange(location: 0, length: string.characters.count))
    return attrString
}

///  获取当前ViewController
///
///  - returns: UIViewController
func getActivityViewController() -> UIViewController? {
    
    var activityViewController: UIViewController? = nil
    var keyWindow = UIApplication.sharedApplication().keyWindow
    
    if keyWindow?.windowLevel != UIWindowLevelNormal {
        let windows = UIApplication.sharedApplication().windows
        for window in windows {
            if window.windowLevel == UIWindowLevelNormal {
                keyWindow = window
                break
            }
        }
    }
    
    let viewsArray = keyWindow?.subviews
    
    if viewsArray?.count > 0 {
        
        let frontView = viewsArray![0]
        let nextResponder = frontView.nextResponder()
        
        if nextResponder is UIViewController {
            activityViewController = nextResponder as? UIViewController
        }else {
            activityViewController = keyWindow!.rootViewController
        }
    }
    
    return activityViewController
}

///  根据一组成对的时间戳生成上课时间表
///
///  - parameter timeIntervals: 一组成对的时间戳（分别代表上课和结束的时间）
///
///  - returns: 文本样式
func getTimeSchedule(timeIntervals timeStamps: [[Int]]) -> [String] {
    
    var timeSchedule: [String] = []
    
    for timeStamp in timeStamps {
        
        let startDate = NSTimeInterval(timeStamp[0])
        let endDate = NSTimeInterval(timeStamp[1])
        
        let string = String(format: "%@ (%@-%@)", getDateString(startDate), getTimeString(startDate), getTimeString(endDate))
        timeSchedule.append(string)
    }
    
    return timeSchedule
}

///  解析学生上课时间表
///
///  - parameter timeSchedule: 上课时间表数据
///
///  - returns:
///  dates:     日期字符串
///  times:     上课时间字符串
///  height:    所需高度
func parseTimeSchedules(timeSchedule: [[NSTimeInterval]]) -> (dates: [String], times: [String], height: CGFloat) {
    
    var dateStrings = [String]()
    var timeStrings = [String]()
    var height: CGFloat = 0
    
    var list: [TimeScheduleModel] = []
    
    println("学生上课时间数据 ＊＊ \(timeSchedule)")
    
    for singleTime in timeSchedule {
        
        let currentStartDate = NSDate(timeIntervalSince1970: singleTime[0])
        let currentEndDate = NSDate(timeIntervalSince1970: singleTime[1])
        
        var appendedDate: TimeScheduleModel?
        
        // 遍历当前日期数组
        for dateResult in list {
            if currentStartDate.isSameDay(dateResult.date) {
                appendedDate = dateResult
                break
            }
        }
        
        if appendedDate != nil {
            // 若当前日期已存在于数组
            appendedDate!.times.append([currentStartDate, currentEndDate])
        }else {
            // 若日期不存在于数组
            let result = TimeScheduleModel()
            result.date = currentStartDate
            result.times.append([currentStartDate, currentEndDate])
            list.append(result)
        }
        
    }
    
    println("解析学生上课时间表 : \(list)")
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    return (dateStrings, timeStrings, height)
}