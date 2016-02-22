//
//  ScholarshipModel.swift
//  mala-ios
//
//  Created by 王新宇 on 2/19/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

// MARK: - Enum
///  奖学金状态
enum ScholarshipStatus: Int {
    /// 未使用
    case Unused
    /// 已使用
    case Used
    /// 已过期
    case Expired
}

class ScholarshipModel: NSObject {

    
    // MARK: - Property
    /// 标题
    var title: String?
    /// 金额
    var price: Int?
    /// 描述
    var desc: String?
    /// 状态
    var status: ScholarshipStatus?
    /// 有效期
    var validityTerm: String?
    /// 使用说明
    var useDirection: String?
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(title: String?, price: Int?, desc: String?, status: ScholarshipStatus?, validityTerm: String?, useDirection: String?) {
        self.init()
        self.title = title
        self.price = price
        self.desc = desc
        self.status = status
        self.validityTerm = validityTerm
        self.useDirection = useDirection
    }
}