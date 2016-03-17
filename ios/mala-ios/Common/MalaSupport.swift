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
        //TODO: 注销推送消息
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