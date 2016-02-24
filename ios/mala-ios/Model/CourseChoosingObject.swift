//
//  CourseChoosingObject.swift
//  mala-ios
//
//  Created by 王新宇 on 2/24/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

// MARK: - 课程购买模型
class CourseChoosingObject: NSObject {
    
    // MARK: - Property
    /// 授课年级
    dynamic var price: GradePriceModel? {
        didSet {
            originalPrice = getPrice()
        }
    }
    /// 上课地点
    dynamic var school: SchoolModel?
    /// 已选上课时间
    dynamic var selectedTime: [ClassScheduleDayModel] = [] {
        didSet {
            originalPrice = getPrice()
        }
    }
    /// 上课小时数
    dynamic var classPeriod: Int = 2 {
        didSet {
            originalPrice = getPrice()
        }
    }
    /// 原价
    dynamic var originalPrice: Int = 0
    
    
    // MARK: - API
    ///  根据当前选课条件获取价格, 选课条件不正确时返回0
    ///
    ///  - returns: 原价
    func getPrice() ->Int {
        if (price?.price != nil && selectedTime.count != 0 && classPeriod != 0) {
            return (price?.price)! * (selectedTime.count*2)
        }else {
            return 0
        }
    }
    
    ///  重置选课模型
    func reset() {
        price = nil
        school = nil
        selectedTime.removeAll()
        classPeriod = 2
        MalaClassPeriod_StepValue = 2
    }
}