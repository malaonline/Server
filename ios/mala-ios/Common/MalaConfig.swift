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
    
    class func defaultTeacherDetail() -> TeacherDetailModel {
        return TeacherDetailModel(id: 0, name: "老师姓名", avatar: "", gender: "m", teaching_age: 0, level: "一级", subject: "学科", grades: [], tags: [], photo_set: [], achievement_set: [], highscore_set: [], prices: [], minPrice: 0, maxPrice: 0)
    }
}
