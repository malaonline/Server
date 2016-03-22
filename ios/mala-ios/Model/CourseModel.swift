//
//  CourseModel.swift
//  mala-ios
//
//  Created by 王新宇 on 3/21/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

public class CourseModel: BaseObjectModel {
    
    // MARK: - Property
    /// 开始时间 时间戳
    var start: NSTimeInterval = 0
    /// 结束时间 时间戳
    var end: NSTimeInterval = 0
    /// 学科名称
    var subject: String = ""
    /// 上课地点名称
    var school: String = ""
    /// 是否完成标记
    var is_passed: Bool = false
    /// 授课老师
    var teacher: TeacherModel?
    /// 评价
    var comment: CommentModel?
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    override init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(id: Int, start: NSTimeInterval, end: NSTimeInterval, subject: String,
        school: String, is_passed: Bool, teacher: TeacherModel?, comment: CommentModel?) {
            self.init()
            self.id = id
            self.start = start
            self.end = end
            self.subject = subject
            self.school = school
            self.is_passed = is_passed
            self.teacher = teacher
            self.comment = comment
    }
    
    // MARK: - Override
    override public func setValue(value: AnyObject?, forKey key: String) {
        if key == "teacher" {
            if let dict = value as? [String: AnyObject] {
                teacher = TeacherModel(dict: dict)
            }
            return
        }
        if key == "comment" {
            if let dict = value as? [String: AnyObject] {
                comment = CommentModel(dict: dict)
            }
            return
        }
        super.setValue(value, forKey: key)
    }
    
    override public func setValue(value: AnyObject?, forUndefinedKey key: String) {
        debugPrint("StudentCourseModel - Set for UndefinedKey: \(key)")
    }
    
    
    // MARK: - Description
    override public var description: String {
        let keys = ["id", "start", "end", "subject", "school", "is_passed", "teacher", "comment"]
        return "\n"+dictionaryWithValuesForKeys(keys).description+"\n"
    }
}