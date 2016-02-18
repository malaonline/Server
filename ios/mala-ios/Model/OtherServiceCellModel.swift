//
//  OtherServiceCellModel.swift
//  mala-ios
//
//  Created by 王新宇 on 2/18/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class OtherServiceCellModel: NSObject {

    // MARK: - Enum
    ///  价钱折扣类型
    enum PriceHandleType {
        /// 折扣 例如: [-] [￥400]
        case Discount
        /// 减免 例如: [￥400(删除线)] [￥0]
        case Reduce
    }
    
    
    // MARK: - Property
    /// Cell标题
    var title: String?
    /// 价格
    var price: Int?
    /// 价格处理类型
    var priceHandleType: PriceHandleType = .Discount
    /// 跳转控制器类型
    var viewController: AnyClass?
    
    
    // MARK: - Constructed
    convenience init(title: String?, price: Int?, priceHandleType: PriceHandleType, viewController: AnyClass?) {
        self.init()
        self.title = title
        self.price = price
        self.priceHandleType = priceHandleType
        self.viewController = viewController
    }
}
