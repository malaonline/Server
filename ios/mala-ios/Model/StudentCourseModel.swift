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
    /// 开始时间 时间戳
    var start: NSTimeInterval = 0
    /// 结束时间 时间戳
    var end: NSTimeInterval = 0
    /// 学科名称
    var subject: String = ""
    /// 年级名称
    var grade: String = ""
    /// 上课地点名称
    var school: String = ""
    /// 是否完成标记
    var is_passed: Bool = false
    /// 老师模型
    var teacher: TeacherModel?
    /// 评论模型
    var comment: CommentModel?
    /// 标示是否过期
    var is_expired: Bool = false
    
    /// 是否评价标记
    var is_commented: Bool? = false
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
            if date.isToday() {
                return .Today
            }else if date.isEarlierThan(NSDate()) {
                return .Past
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
    
    convenience init(id: Int, start: NSTimeInterval, end: NSTimeInterval, subject: String, grade: String, school: String, is_passed: Bool, is_commented: Bool = false, is_expired: Bool) {
        self.init()
        self.id = id
        self.start = start
        self.end = end
        self.subject = subject
        self.grade = grade
        self.school = school
        self.is_passed = is_passed
        self.is_commented = is_commented
        self.is_expired = is_expired
    }
    
    // MARK: - Override
    override public func setValue(value: AnyObject?, forUndefinedKey key: String) {
        println("StudentCourseModel - Set for UndefinedKey: \(key)")
    }
    
    
    // MARK: - Description
    override public var description: String {
        let keys = ["id", "start", "end", "subject", "grade", "school", "is_passed"]
        return "\n"+dictionaryWithValuesForKeys(keys).description+" Date: "+(getDateTimeString(end))+"\n"
    }
}