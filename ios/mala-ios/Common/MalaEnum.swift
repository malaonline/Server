//
//  MalaEnum.swift
//  mala-ios
//
//  Created by 王新宇 on 3/2/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

///  验证码操作
///
///  - Send:   发送验证码
///  - Verify: 验证
enum VerifyCodeMethod: String {
    case Send = "send"
    case Verify = "verify"
}

///  支付操作
///
///  - Pay: 支付
enum PaymentMethod: String {
    case Pay = "pay"
}

///  支付手段
///  - See: [取值范围](https://www.pingxx.com/api#api-charges)
///  - Wechat: 微信支付
///  - Alipay: 支付宝手机支付
enum MalaPaymentChannel: String {
    case Wechat = "wx"
    case Alipay = "alipay"
}

///  跳转URLScheme
///
///  - Wechat: 微信
///  - Alipay: 支付宝
enum MalaAppURLScheme: String {
    case Wechat = "wx"
    case Alipay = "malalaoshitoalipay"
}

///  订单状态
///
///  - Penging:  待付款
///  - Paid:     已付款
///  - Canceled: 已取消
///  - Refund:   退费
enum MalaOrderStatus: String {
    case Penging = "u"
    case Paid = "p"
    case Canceled = "d"
    case Refund = "r"
}

///  奖学金状态
///
///  - Unused:  未使用
///  - Used:    已使用
///  - Expired: 已过期
enum CouponStatus: Int {
    case Unused
    case Used
    case Expired
}

///  价钱优惠类型
///
///  - Discount: 折扣 例如: [-] [￥400]
///  - Reduce:   免除 例如: [￥400(删除线)] [￥0]
///  - None: 不显示
enum PriceHandleType: Int {
    case Discount
    case Reduce
    case None
}

///  其他服务类型
///
///  - Coupon:           优惠券
///  - EvaluationFiling: 测评建档
enum OtherServiceType {
    case Coupon
    case EvaluationFiling
}

///  用户信息类型
///
///  - StudentName:       学生姓名
///  - StudentSchoolName: 学生学校姓名
public enum userInfoType {
    case StudentName
    case StudentSchoolName
}


// MARK: - ClassSchedule
///  课程进度状态
///
///  - past:   已过去
///  - today:  今天
///  - future: 未上
public enum CourseStatus: String {
    case Past = "past"
    case Today = "today"
    case Future = "future"
}

