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
    
    ///  短信倒计时时间
    class func callMeInSeconds() -> Int {
        return 60
    }
    ///  支付方式
    class func paymentChannel() -> [String] {
        return ["wechat", "alipay"]
    }
    ///  支付方式数
    class func paymentChannelAmount() -> Int {
        return paymentChannel().count
    }
    ///  老师详情缺省模型
    class func defaultTeacherDetail() -> TeacherDetailModel {
        return TeacherDetailModel(
            id: 0,
            name: "老师姓名",
            avatar: "",
            gender: "m",
            teaching_age: 0,
            level: "一级",
            subject: "学科",
            grades: [],
            tags: [],
            photo_set: [],
            achievement_set: [],
            highscore_set: [],
            prices: [],
            minPrice: 0,
            maxPrice: 0
        )
    }
    ///  [个人中心]静态结构数据
    class func profileData() -> [[ProfileElementModel]] {
        return [
            [
                ProfileElementModel(id: 0, title: "学生姓名", detail: "", controller: InfoModifyViewController.self, controllerTitle: "更改名字"),
                ProfileElementModel(id: 1, title: "学校信息", detail: "", controller: InfoModifyViewController.self, controllerTitle: "所在学校"),
                ProfileElementModel(id: 2, title: "所在城市", detail: "", controller: InfoModifyViewController.self, controllerTitle: "所在城市"),
                ProfileElementModel(id: 3, title: "我的奖学金", detail: "", controller: CouponViewController.self, controllerTitle: "我的奖学金")
            ],
            [
                ProfileElementModel(id: 4, title: "关于麻辣老师", detail: "", controller: UIViewController.self, controllerTitle: "关于麻辣老师")
            ]
        ]
    }
}
