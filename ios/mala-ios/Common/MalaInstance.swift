//
//  MalaInstance.swift
//  mala-ios
//
//  Created by 王新宇 on 2/29/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit


// MARK: - Instance
/// 当前课程选择对象
var MalaCourseChoosingObject: CourseChoosingObject = CourseChoosingObject()
/// 其他课程服务数组
var MalaServiceObject: [OtherServiceCellModel] = MalaOtherService
/// 需支付金额
var amount: Int = 0
/// 订单对象
var MalaOrderObject: OrderForm = OrderForm()




// MARK: - Method
/// 获取最终需支付金额
public func getAmount() -> Int? {
    var amount = MalaCourseChoosingObject.getPrice()
    //  循环其他服务数组，计算折扣、减免
    for object in MalaServiceObject {
        switch object.priceHandleType {
        case .Discount:
            amount = amount - (object.price ?? 0)
            break
        case .Reduce:
            
            break
        }
    }
    amount = amount < 0 ? 0 : amount
    return amount
}
