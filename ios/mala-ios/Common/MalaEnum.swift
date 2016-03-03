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
enum PriceHandleType {
    case Discount
    case Reduce
}