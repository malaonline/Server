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
/// 用户是否首次购买该学科课程标记
/// 进入[课程购买]页面时请求服务端并赋值，退出[课程购买]页面时置空
var MalaIsHasBeenEvaluatedThisSubject: Bool? = nil
/// 用户预览订单模型
var MalaOrderOverView: OrderForm = OrderForm()

/// 支付页面控制器，用于APPDelegate处理回调
weak var MalaPaymentController: PaymentViewController?
/// 筛选条件选择下标记录
var MalaFilterIndexObject = filterSelectedIndexObject()
/// 用户学习报告数据对象
var MalaSubjectReport = SubjectReport()