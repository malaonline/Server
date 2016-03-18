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
    
    // 其他
    var channel: MalaPaymentChannel = .Alipay {
        didSet{
            println("OrderForm - Channel: \(channel.rawValue)")
        }
    }
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    convenience init(id: Int?, name: String?, teacher: Int?, school: Int?, grade: Int?, subject: Int?, coupon: Int?, hours: Int?, timeSchedule: [Int]?,
        order_id: String?, parent: Int?, total: Int?, price: Int?, status: String?) {
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