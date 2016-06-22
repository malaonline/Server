//
//  CouponModel.swift
//  mala-ios
//
//  Created by 王新宇 on 2/19/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CouponModel: NSObject {

    // MARK: - Property
    /// 编号
    var id: Int = 0
    /// 名称
    var name: String = ""
    /// 金额(包含小数位)
    var amount: Int = 0
    /// 有效期
    var expired_at: NSTimeInterval = 0
    /// 使用限制最小金额
    var minPrice: Int = 0
    /// 使用标记
    var used: Bool = true
    
    /// 状态
    var status: CouponStatus?
    /// 金额字符串(保留以为小数)
    var amountString: String {
        get {
            return String(format: "%d", Int(amount/100))
        }
    }
    /// 使用限制最小金额说明字符串
    var minPriceString: String {
        get {
            return "满"+String(format: "%d", Int(minPrice/100))+"元可用"
        }
    }
    /// 有效期说明字符串
    var expiredString: String {
        get {
            return "有效期至 "+getDateTimeString(expired_at, format: "yyyy-MM-dd")
        }
    }
    
    
    ///  根据[有效期]、[使用标记] 生成状态
    func setupStatus() {
        // 已使用
        if used {
            status = .Used
        // 已过期
        }else if couponIsExpired(expired_at) {
            status = .Expired
        // 未使用
        }else {
            status = .Unused
        }
    }
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(id: Int, name: String, amount: Int, expired_at: NSTimeInterval, minPrice: Int = 0, used: Bool) {
        self.init()
        self.id = id
        self.name = name
        self.amount = amount
        self.expired_at = expired_at
        self.minPrice = minPrice
        self.used = used
    }
    
    
    // MARK: - Description
    override var description: String {
        return "\nCouponModel(id: \(id), name: \(name), amount: \(amount), expired_at: \(String(timeStamp: expired_at)))" +
        ", used: \(used)), status: \(status)\n"
    }
}