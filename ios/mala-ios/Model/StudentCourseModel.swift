//
//  StudentCourseModel.swift
//  mala-ios
//
//  Created by 王新宇 on 3/17/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

public class StudentCourseModel: BaseObjectModel {

    // MARK: - Property
    /// 结束时间 时间戳
    var end: NSTimeInterval = 0 {
        didSet {
            println("当前为\(date.month())月\(date.day())日 - 状态为: \(self.status)")
        }
    }
    /// 学科名称
    var subject: String = ""
    /// 是否完成标记
    var is_passed: Bool = false
    /// 是否评价标记
    var is_commented: Bool = false
    /// 日期对象
    var date: NSDate {
        get {
            return NSDate(timeIntervalSince1970: end)
        }
    }
    /// 课程状态
    var status: CourseStatus {
        get {
            // 设置课程状态
            if date.isEarlierThan(NSDate()) {
                return .Past
            }else if date.isToday() {
                return .Today
            }else {
                return .Future
            }
        }
    }
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    override init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(id: Int, end: NSTimeInterval, subject: String, is_passed: Bool, is_commented: Bool) {
        self.init()
        self.id = id
        self.end = end
        self.subject = subject
        self.is_passed = is_passed
        self.is_commented = is_commented
    }
    
    // MARK: - Override
    override public func setValue(value: AnyObject?, forUndefinedKey key: String) {
        debugPrint("StudentCourseModel - Set for UndefinedKey: \(key)")
    }
    
    
    // MARK: - Description
    override public var description: String {
        let keys = ["id", "end", "subject", "is_passed", "is_commented", "date"]
        return "\n"+dictionaryWithValuesForKeys(keys).description+"\n"
    }
}