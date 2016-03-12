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
    dynamic var gradePrice: GradePriceModel? {
        didSet {
            originalPrice = getPrice()
        }
    }
    /// 上课地点
    dynamic var school: SchoolModel? {
        didSet {
            println("CourseChoosing-school didSet")
        }
    }
    /// 已选上课时间
    dynamic var selectedTime: [ClassScheduleDayModel] = [] {
        didSet {
            // 保证[课时数]和[选择上课时间]匹配
            classPeriod = selectedTime.count*2
        }
    }
    /// 上课小时数
    dynamic var classPeriod: Int = 2 {
        didSet {
            originalPrice = getPrice()
        }
    }
    /// 优惠券
    dynamic var coupon: CouponModel? {
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
        // [课程]、[上课时间]、[课时]三个条件均符合规则, 且[课时数]大于等于[选择上课时间数*2]时，使用[课时]进行费用计算
        if (gradePrice?.price != nil && selectedTime.count != 0 && classPeriod >= selectedTime.count*2) {
            return (gradePrice?.price)! * classPeriod
        
        // 若[课时数]和[上课时间数]不符合，则按照[上课时间数]来进行费用计算
        }else if (gradePrice?.price != nil && selectedTime.count != 0){
            return (gradePrice?.price)! * selectedTime.count*2
            
        // 不合规则
        }else {
            return 0
        }
    }
    
    /// 获取最终需支付金额
    func getAmount() -> Int? {
        var amount = MalaCourseChoosingObject.getPrice()
        //  循环其他服务数组，计算折扣、减免
        //  暂时注释，目前仅有奖学金折扣
        /* for object in MalaServiceObject {
            switch object.priceHandleType {
            case .Discount:
                amount = amount - (object.price ?? 0)
                break
            case .Reduce:
                
                break
            }
        } */
        
        // 计算其他优惠服务
        if coupon != nil {
            amount = amount - (coupon?.amount ?? 0)
        }
        // 确保需支付金额不小于零
        amount = amount < 0 ? 0 : amount
        return amount
    }
    
    
    /// 刷新选课模型
    func refresh() {
        selectedTime.removeAll()
        classPeriod = 2
        MalaClassPeriod_StepValue = 2
    }
    
    /// 重置选课模型
    func reset() {
        gradePrice = nil
        school = nil
        school?.id = -1
        selectedTime.removeAll()
        classPeriod = 2
        MalaClassPeriod_StepValue = 2
    }
}