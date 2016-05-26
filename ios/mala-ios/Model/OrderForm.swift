//
//  OrderForm.swift
//  mala-ios
//
//  Created by 王新宇 on 2/29/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class OrderForm: BaseObjectModel {
    
    // MARK: - Property
    // 创建订单参数
    var teacher: Int = 0
    var school: Int = 0
    var grade: Int = 0
    var subject: Int = 0
    var coupon: Int = 0
    var hours: Int = 0
    var weekly_time_slots: [Int]?
    
    // 订单结果参数
    var order_id: String?
    var parent: Int = 0
    var total: Int = 0
    var price: Int = 0
    var status: String?
    /// 若支付过程中课程被抢买，此参数应为为false
    var is_timeslot_allocated: Bool?
    
    // 订单显示信息
    var teacherName: String?
    var subjectName: String?
    var gradeName: String?
    var schoolName: String?
    var avatarURL: String?
    var amount: Int = 0
    var evaluated: Bool?
    var timeSlots: [[Int]]?
    var chargeChannel: String?
    var createAt: NSTimeInterval?
    var paidAt: NSTimeInterval?
    
    // 其他
    var result: Bool?
    var code: Int?
    var channel: MalaPaymentChannel {
        set{
            self.chargeChannel = newValue.rawValue
        }
        get{
            if let channel = MalaPaymentChannel(rawValue: self.chargeChannel ?? "other") {
                return channel
            }else {
                return .Other
            }
        }
    }
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    convenience init(result: Bool, code: Int) {
        self.init()
        self.result = result
        self.code = code
    }
    
    convenience init(id: Int, orderId: String?, teacherId: Int?, teacherName: String?, avatarURL: String?, schoolName: String?, gradeName: String?, subjectName: String?,
                     orderStatus: String?, amount: Int, evaluated: Bool?) {
        self.init()
        self.id = id
        self.order_id = orderId
        self.teacher = teacherId ?? -1
        self.teacherName = teacherName
        self.avatarURL = avatarURL
        self.schoolName = schoolName
        self.gradeName = gradeName
        self.subjectName = subjectName
        self.status = orderStatus
        self.amount = amount
        self.evaluated = evaluated
    }
    
    convenience init(id: Int?, name: String?, teacher: Int?, school: Int?, grade: Int?, subject: Int?, coupon: Int?, hours: Int?, timeSchedule: [Int]?,
                     order_id: String?, parent: Int?, total: Int?, price: Int?, status: String?, is_timeslot_allocated: Bool?, timeSlots: [[Int]]? = nil,
                     chargeChannel: String? = "other", createAt: NSTimeInterval? = 0) {
            self.init()
            self.id = id ?? 0
            self.name = name
            self.teacher = teacher ?? 0
            self.school = school ?? 0
            self.grade = grade ?? 0
            self.subject = subject ?? 0
            self.coupon = coupon ?? 0
            self.hours = hours ?? 0
            self.weekly_time_slots = timeSchedule
            
            self.order_id = order_id
            self.parent = parent ?? 0
            self.total = total ?? 0
            self.price = price ?? 0
            self.status = status
            self.is_timeslot_allocated = is_timeslot_allocated
            self.timeSlots = timeSlots
            self.chargeChannel = chargeChannel
            self.createAt = createAt
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["id", "name", "teacher", "school", "grade", "subject", "coupon", "hours", "weekly_time_slots", "order_id", "parent", "total", "price", "status"]
        return dictionaryWithValuesForKeys(keys).description
    }
    
    func jsonDictionary() -> JSONDictionary {
        var json: JSONDictionary = [
            "teacher": teacher ?? 0,
            "school": school ?? 0,
            "grade": grade ?? 0,
            "subject": subject ?? 0,
            "hours": hours ?? 0,
            "coupon": coupon ?? 0,
            "weekly_time_slots": weekly_time_slots ?? [],
        ]
        if coupon == 0 {
            json["coupon"] = NSNull()
        }
        return json
    }
}