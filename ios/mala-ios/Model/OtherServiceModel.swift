//
//  OtherServiceModel.swift
//  mala-ios
//
//  Created by 王新宇 on 2/18/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class OtherServiceModel: NSObject {
    
    // MARK: - Property
    /// 标题
    var title: String?
    /// 服务类型
    var type: OtherServiceType = .Coupon
    /// 价格
    var price: Int?
    /// 价格处理类型
    var priceHandleType: PriceHandleType = .Discount
    /// 跳转控制器类型
    var viewController: AnyClass?
    
    
    // MARK: - Constructed
    convenience init(title: String?, type: OtherServiceType, price: Int?, priceHandleType: PriceHandleType, viewController: AnyClass?) {
        self.init()
        self.title = title
        self.type = type
        self.price = price
        self.priceHandleType = priceHandleType
        self.viewController = viewController
    }
}