//
//  MalaSupport.swift
//  mala-ios
//
//  Created by 王新宇 on 2/25/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import Foundation
import DateTools
import Kingfisher

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
    KingfisherManager.sharedManager.cache.clearDiskCache()
    KingfisherManager.sharedManager.cache.clearMemoryCache()
    KingfisherManager.sharedManager.cache.cleanExpiredDiskCache()
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
///  - returns: [我的课表]页面课表数据
func parseStudentCourseTable(courseTable: [StudentCourseModel]) -> (model: [[[StudentCourseModel]]], recently: NSIndexPath) {
    
    let courseList = [StudentCourseModel](courseTable.reverse())
    var datas = [[[StudentCourseModel]]]()
    var currentMonthsIndex: Int = 0
    var currentDaysIndex: Int = 0
    
    /// 距今天最近的上课时间位于[课表数据]的下标
    var indexPath: (section: Int, row: Int) = (0, 0)
    var recentCourse: StudentCourseModel?
    var nowTime: Int = 0
    let nowTimeInterval = NSDate().timeIntervalSince1970
    
    ///  若该课程为最近课程，则将当前下标添加到indexPath中
    ///
    ///  - parameter course: 课程模型
    func validate(course: StudentCourseModel) {
        if course === recentCourse {
            indexPath = (currentMonthsIndex, currentDaysIndex)
        }
    }
    
    for (index, course) in courseList.enumerate() {
        
        /// 该课程与当前时间的秒数差值
        let time = Int(course.end - nowTimeInterval)
        /// 时间差存在且，当前时间差为零 或 当前时间差小于时间差
        if time > 0 && (nowTime == 0 || time < nowTime) {
            nowTime = time
            recentCourse = course
        }
        
        let courseYearAndMonth = String(course.date.year())+String(course.date.month())
        let courseDay = course.date.day()
        
        if index > 0 {
            
            let previousCourse = courseList[index-1]
            
            if courseYearAndMonth == String(previousCourse.date.year())+String(previousCourse.date.month()) {
                
                if courseDay == previousCourse.date.day() {
                    // 同年同月同日
                    datas[currentMonthsIndex][currentDaysIndex].append(course)
                    validate(course)
                }else {
                    // 同年同月
                    datas[currentMonthsIndex].append([course])
                    currentDaysIndex += 1
                    validate(course)
                }
            }else {
                // 非同年同月
                datas.append([[course]])
                currentMonthsIndex += 1
                currentDaysIndex = 0
                validate(course)
            }
        }else {
            // 均不同
            datas.append([[course]])
            currentMonthsIndex = 0
            currentDaysIndex = 0
            validate(course)
        }
    }
    
    ///  若所有课程中无最近未上课程，则选定最后一节课程
    if nowTime == 0 && datas.count > 0 && datas[0].count > 0 {
        let section = datas.count-1
        let row = datas[section].count-1
        indexPath = (section, row)
    }
    return (datas, NSIndexPath(forRow: indexPath.row, inSection: indexPath.section))
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
    
    // 过滤TabbarController情况
    if let mainViewController = activityViewController as? MainViewController,
        naviVC = mainViewController.viewControllers?[mainViewController.selectedIndex] as? UINavigationController {
        activityViewController = naviVC.viewControllers[0]
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
    weekInt -= 1
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
func parseTimeSlots(timeSchedule: [[NSTimeInterval]]) -> (dates: [String], times: [String], height: CGFloat) {
    
    var dateStrings = [String]()
    var timeStrings = [String]()
    var height: CGFloat = 0
    var list: [TimeScheduleModel] = []
    let sortTimeSlots = timeSchedule.sort { (timeIntervals1, timeIntervals2) -> Bool in
        return (timeIntervals1.first ?? 0) < (timeIntervals2.first ?? 0)
    }
    
    for singleTime in sortTimeSlots {
        
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
        
        // 垂直高度
        let count = slotDate.times.count
        if (count <= 4) {
            height += 14*2+2
        }else {
            height += 14*3+2*2
        }
        height += 20
    }
    height -= 20
    println("日期表 : \(dateStrings)")
    println("时间表 : \(timeStrings)")
    println("高度 : \(height)")
 
    return (dateStrings, timeStrings, height)
}

///  解析奖学金列表数据
///  将当前不符合使用条件的奖学金设置冻结属性，同时进行排序 (排序条件为: [可用情况][减免金额][过期时间])
///
///  - parameter coupons: 奖学金列表数据
///
///  - returns: 奖学金列表数据
func parseCouponlist(coupons: [CouponModel]) -> [CouponModel] {
    
    var result = coupons
    // 当前用户选课价格
    let currentPrice = MalaCourseChoosingObject.getPrice()
    
    for coupon in result {
        
        // 冻结尚未满足要求的奖学金
        if coupon.minPrice > currentPrice {
            coupon.status = .Disabled
        }
  
    }
    
    result.sortInPlace { (coupon1, coupon2) -> Bool in
        if coupon2.status == .Disabled {
            return true
        }else {
            return false
        }
    }
    
    return result
}

///  根据距离进行学校排序
///
///  - parameter schools: 学校模型列表
///
///  - returns: 学校模型列表
func sortSchoolsByDistance(schools: [SchoolModel]) -> [SchoolModel] {
    return schools.sort({ (school1, school2) -> Bool in
        return school1.distance < school2.distance
    })
}


// MARK: - Study Report Support
func adjustHomeworkData(data: [SingleHomeworkData]) -> [SingleHomeworkData] {
    
    /// 排序
    var sortData = data.sort { (data1, data2) -> Bool in
        return data1.rate.doubleValue > data2.rate.doubleValue
    }
    var allRate: Double = 0
    
    /// 将多于8项的数据合并为第九项“其它”
    while sortData.count > 8 {
        if let lastReport = sortData.last {
            allRate += lastReport.rate.doubleValue
            sortData.removeLast()
        }
    }

    sortData.append(SingleHomeworkData(id: 999, name: "其它", rate: NSNumber(double: allRate)))
    return sortData
}

func adjustTopicData(data: [SingleTopicData]) -> [SingleTopicData] {
    /// 排序
    var sortData = data.sort { (data1, data2) -> Bool in
        return data1.rightRate > data2.rightRate
    }
    var totalItem: Int = 0
    var rightItem: Int = 0
    
    /// 将多于8项的数据合并为第九项“其它”
    while sortData.count > 8 {
        if let lastReport = sortData.last {
            totalItem += lastReport.total_item
            rightItem += lastReport.right_item
            sortData.removeLast()
        }
    }
    sortData.append(SingleTopicData(id: "9999", name: "其它", totalItem: totalItem, rightItem: rightItem))
    return sortData
}
func adjustTopicScoreData(data: [SingleTopicScoreData]) -> [SingleTopicScoreData] {
    /// 排序
    var sortData = data.sort { (data1, data2) -> Bool in
        return data1.my_score.doubleValue > data2.my_score.doubleValue
    }
    var myScore: Double = 0
    var aveScore: Double = 0
    
    /// 将多于8项的数据合并为第九项“其它”
    while sortData.count > 8 {
        if let lastReport = sortData.last {
            myScore += lastReport.my_score.doubleValue
            aveScore += lastReport.ave_score.doubleValue
            sortData.removeLast()
        }
    }
    
    sortData.append(SingleTopicScoreData(id: "9999", name: "其它", score: NSNumber(double: myScore), aveScore: NSNumber(double: aveScore)))
    return sortData
}

///  发送屏幕浏览信息（用于GoogleAnalytics屏幕浏览量数据分析）
///
///  - parameter value: 屏幕名称
func sendScreenTrack(value: String? = "其它页面") {
    #if USE_PRD_SERVER
        let tracker = GAI.sharedInstance().defaultTracker
        tracker.set(kGAIScreenName, value: value)
        
        let builder = GAIDictionaryBuilder.createScreenView()
        tracker.send(builder.build() as [NSObject : AnyObject])
    #endif
}