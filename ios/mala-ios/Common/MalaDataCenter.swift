//
//  MalaDataCenter.swift
//  mala-ios
//
//  Created by 王新宇 on 3/3/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

///  获取第一个可用的优惠券对象
///
///  - parameter coupons: 优惠券模型数组
func getFirstUnusedCoupon(coupons: [CouponModel]) -> CouponModel? {
    for coupon in coupons {
        if coupon.status == .Unused {
            return coupon
        }
    }
    return nil
}