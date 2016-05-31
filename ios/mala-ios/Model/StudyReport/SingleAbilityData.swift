//
//  SingleAbilityData.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/31.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class SingleAbilityData: NSObject {
    
    // MARK: - Property
    /// 能力名称（简略）
    var name: String = ""
    /// 数值
    var value: Int = 0
    /// 能力
    var ability: MalaStudyReportAbility {
        get {
            return MalaStudyReportAbility(rawValue: name) ?? .unkown
        }
    }
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
}