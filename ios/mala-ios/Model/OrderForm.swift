//
//  OrderForm.swift
//  mala-ios
//
//  Created by 王新宇 on 2/29/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class OrderForm: NSObject {
    
    // MARK: - Property
    var teacher: Int?
    var school: Int?
    var grade: Int?
    var subject: Int?
    var coupon: Int?
    var hours: Int?
    var weekly_time_slots: [Int]?
    var channel: MalaPaymentChannel?
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    convenience init(teacher: Int, school: Int, grade: Int, subject: Int, coupon: Int, hours: Int, timeSchedule: [Int]) {
        self.init()
        self.teacher = teacher
        self.school = school
        self.grade = grade
        self.subject = subject
        self.coupon = coupon
        self.hours = hours
        self.weekly_time_slots = timeSchedule
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["teacher", "school", "grade", "subject", "coupon", "hours", "weekly_time_slots"]
        return dictionaryWithValuesForKeys(keys).description
    }
    
    func jsonDictionary() -> [String: AnyObject?]{
        let keys = ["teacher", "school", "grade", "subject", "coupon", "hours", "weekly_time_slots"]
        return dictionaryWithValuesForKeys(keys)
    }
}
