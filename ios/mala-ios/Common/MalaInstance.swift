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
var MalaServiceObject: [OtherServiceModel] = MalaOtherService
/// 需支付金额
var amount: Int = 0
/// 订单对象
var MalaOrderObject: OrderForm = OrderForm()
/// 服务器返回订单对象
var ServiceResponseOrder: OrderForm = OrderForm()
/// 用户拥有优惠券数据模型数组
var MalaUserCoupons: [CouponModel] = []