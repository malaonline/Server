//
//  MalaSupport.swift
//  mala-ios
//
//  Created by 王新宇 on 2/25/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import Foundation
import DateTools

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


// MARK: - Common TextAttribute
public func commonTextStyle() -> [String: AnyObject]? {
    let AttributeDictionary = NSMutableDictionary()
    AttributeDictionary[NSForegroundColorAttributeName] = MalaColor_6C6C6C_0
    return AttributeDictionary.copy() as? [String : AnyObject]
}


// MARK: - Method
public func makeStatusBarBlack() {
    UIApplication.sharedApplication().statusBarStyle = .Default
}

public func makeStatusBarWhite() {
    UIApplication.sharedApplication().statusBarStyle = .LightContent
}

public func MalaRandomColor() -> UIColor {
    return MalaConfig.malaTagColors()[randomInRange(0...MalaConfig.malaTagColors().count-1)]
}

///  根据Date获取星期数
///
///  - parameter date: NSDate对象
///
///  - returns: 星期数（0~6, 对应星期日~星期六）
public func weekdayInt(date: NSDate) -> Int {
    let calendar = NSCalendar.currentCalendar()
    let components: NSDateComponents = calendar.components(NSCalendarUnit.Weekday, fromDate: date)
    return components.weekday-1
}

///  解析学生上课时间表
///
///  - returns: ClassScheduleViewController.model数据
func parseStudentCourseTable(courseTable: [StudentCourseModel]) -> [[[StudentCourseModel]]] {
    
    let courseList = [StudentCourseModel](courseTable.reverse())
    var datas = [[[StudentCourseModel]]]()
    var currentMonthsIndex: Int = 0
    var currentDaysIndex: Int = 0
    
    for (index, course) in courseList.enumerate() {
        
        let courseYearAndMonth = String(course.date.year())+String(course.date.month())
        let courseDay = course.date.day()
        
        if index > 0 {
            
            let previousCourse = courseList[index-1]
            
            if courseYearAndMonth == String(previousCourse.date.year())+String(previousCourse.date.month()) {
                
                if courseDay == previousCourse.date.day() {
                    // 同年同月同日
                    datas[currentMonthsIndex][currentDaysIndex].append(course)
                }else {
                    // 同年同月
                    datas[currentMonthsIndex].append([course])
                    currentDaysIndex += 1
                }
            }else {
                // 非同年同月
                datas.append([[course]])
                currentMonthsIndex += 1
                currentDaysIndex = 0
            }
        }else {
            // 均不同
            datas.append([[course]])
            currentMonthsIndex = 0
            currentDaysIndex = 0
        }
    }
    return datas
}

///  根据时间戳获取时间字符串（例如12:00）
///
///  - parameter timeStamp: 时间戳
///
///  - returns: 时间字符串
func getTimeString(timeStamp: NSTimeInterval, format: String = "HH:mm") -> String {
    return NSDate(timeIntervalSince1970: timeStamp).formattedDateWithFormat(format)
}

///  根据时间戳获取时间字符串（例如2000/10/10）
///
///  - parameter timeStamp: 时间戳
///
///  - returns: 时间字符串
func getDateString(timeStamp: NSTimeInterval? = nil, date: NSDate? = nil, format: String = "yyyy/MM/dd") -> String {
    if timeStamp != nil {
        return NSDate(timeIntervalSince1970: timeStamp!).formattedDateWithFormat(format)
    }else if date != nil {
        return date!.formattedDateWithFormat(format)
    }else {
        return NSDate().formattedDateWithFormat(format)
    }
}

///  根据时间戳获取时间字符串（例如2000/10/10 12:01:01）
///
///  - parameter timeStamp: 时间戳
///
///  - returns: 时间字符串
func getDateTimeString(timeStamp: NSTimeInterval, format: String = "yyyy-MM-dd HH:mm:ss") -> String {
    return NSDate(timeIntervalSince1970: timeStamp).formattedDateWithFormat(format)
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
func getTimeSchedule(timeIntervals timeStamps: [[NSTimeInterval]]) -> [String] {
    
    var timeSchedule: [String] = []
    
    for timeStamp in timeStamps {
        
        let startDate = timeStamp[0]
        let endDate = timeStamp[1]
        
        let string = String(format: "%@ (%@-%@)", getDateString(startDate), getTimeString(startDate), getTimeString(endDate))
        timeSchedule.append(string)
    }
    
    return timeSchedule
}

///  获取日期对应星期字符串
///
///  - parameter timeStamp: 时间戳
///  - parameter date:      日期对象
///
///  - returns: 星期字符串
func getWeekString(timeStamp: NSTimeInterval? = nil, date: NSDate? = nil) -> String {
    
    var weekInt = 0
    
    if let timeStamp = timeStamp {
        weekInt = NSDate(timeIntervalSince1970: timeStamp).weekday()
    }else if let date = date {
        weekInt = date.weekday()
    }
    
    weekInt = weekInt == 7 ? 0 : weekInt
    return MalaConfig.malaWeekdays()[weekInt]
}

///  解析学生上课时间表
///
///  - parameter timeSchedule: 上课时间表数据
///
///  - returns:
///  dates:     日期字符串
///  times:     上课时间字符串
///  height:    所需高度
func parseTimeSlots(timeSchedule: [[NSTimeInterval]]) -> (dates: [String], times: [String], heightCount: Int) {
    
    var dateStrings = [String]()
    var timeStrings = [String]()
    var heightCount: Int = 0
    var list: [TimeScheduleModel] = []
    
    
    for singleTime in timeSchedule {
        
        let currentStartDate = NSDate(timeIntervalSince1970: singleTime[0])
        let currentEndDate = NSDate(timeIntervalSince1970: singleTime[1])
        
        var appendedDate: TimeScheduleModel?
        
        // 判断此日期是否已存在于数组中
        for dateResult in list {
            if currentStartDate.isSameDay(dateResult.date) {
                appendedDate = dateResult
                break
            }
        }
        
        if appendedDate != nil {
            // 若当前日期已存在于数组，添加上课时间数据到对应日期中
            appendedDate!.times.append([currentStartDate, currentEndDate])
        }else {
            // 若日期不存在于数组，添加日期和上课时间数据
            let result = TimeScheduleModel()
            result.date = currentStartDate
            result.times.append([currentStartDate, currentEndDate])
            list.append(result)
        }
        
    }
    
    // 解析日期数据为字符串
    for slotDate in list {
        
        // 日期字符串
        dateStrings.append(getDateString(date: slotDate.date, format: "M月d日") + "\n" + MalaConfig.malaWeekdays()[weekdayInt(slotDate.date)])

        // 上课时间字符串
        var timeString = ""
        for (index, slot) in slotDate.times.enumerate() {
            timeString += index%2 == 1 ? "    " : "\n"
            timeString = index == 0 ? "" : timeString
            timeString += getDateString(date: slot[0], format: "HH:mm") + "-" + getDateString(date: slot[1], format: "HH:mm")
        }
        timeStrings.append(timeString)
        
        // 垂直高度数
        heightCount += slotDate.times.count > 4 ? 4 : 3
    }
    
    heightCount = heightCount == 3 ? 2 : heightCount
    
    println("日期表 : \(dateStrings)")
    println("时间表 : \(timeStrings)")
    println("高度 : \(heightCount)")
 
    return (dateStrings, timeStrings, heightCount)
}