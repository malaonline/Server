//
//  MalaConfig.swift
//  mala-ios
//
//  Created by 王新宇 on 2/24/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class MalaConfig {
    
    static let appGroupID: String = "group.malalaoshi.parent"
    
    class func callMeInSeconds() -> Int {
        return 60
    }
    
    
    class func paymentChannel() -> [String] {
        return ["wechat", "alipay"]
    }
    
    class func paymentChannelAmount() -> Int {
        return paymentChannel().count
    }
}
