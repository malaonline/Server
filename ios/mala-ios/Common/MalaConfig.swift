//
//  MalaConfig.swift
//  mala-ios
//
//  Created by 王新宇 on 2/24/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

public class MalaConfig {
    
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
    ///  头像最大大小
    class func avatarMaxSize() -> CGSize {
        return CGSize(width: 414, height: 414)
    }
    ///  头像压缩质量
    class func avatarCompressionQuality() -> CGFloat {
        return 0.7
    }
    ///  头像大小
    class func editProfileAvatarSize() -> CGFloat {
        return 100
    }
    ///  app版本号
    class func aboutAPPVersion() -> CGFloat {
        return 1.0
    }
    ///  版权信息
    class func aboutCopyRightString() -> String {
        return "COPYRIGHT © 2014 - 2016\n北京麻辣在线网络科技有限公司版权所有"
    }
    ///  关于我们描述HTMLString
    class func aboutDescriptionHTMLString() -> String {
        return "        麻辣老师(MALALAOSHI.COM)成立于2015年6月，由众多资深教育人士和互联网顶尖人才组成，是专注于国内二三四线城市中小学K12课外辅导的O2O服务平台，以效果、费用、便捷为切入口，实现个性化教学和学生的个性发展，推动二三四线城市及偏进地区教育进步。\n\n        麻辣老师通过O2O的方式，以高效和精准的老师推荐，让中小学家长更加方便和经济地找到好老师，提升老师的收入，优化教、学、练、测、评五大环节, 提升教学与学习效率、创新服务模式，带给家长、老师及学生全新的学习体验。"
    }
    
    // MARK: - UI Config
    class func colorF2F2F2() -> UIColor {
        return UIColor(rgbHexValue: 0xF2F2F2, alpha: 1.0)
    }
    
    class func setupClassSchedule(inout viewController: ClassScheduleViewController) {

        // 日历控制器
        viewController.weekdayHeaderEnabled = true
        viewController.weekdayTextType = .VeryShort
        viewController.overlayTextColor = MalaDetailsCellTitleColor
        viewController.overlayBackgroundColor = MalaProfileBackgroundColor
        viewController.weekdayHeader = ClassScheduleViewWeekdayHeader(calendar: viewController.calendar, weekdayTextType: viewController.weekdayTextType)

        ///  星期数视图
        PDTSimpleCalendarViewWeekdayHeader.appearance().textColor = MalaDetailsCellTitleColor
        PDTSimpleCalendarViewWeekdayHeader.appearance().textFont = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        PDTSimpleCalendarViewWeekdayHeader.appearance().headerBackgroundColor = MalaProfileBackgroundColor
        
        ///  月份头视图
        PDTSimpleCalendarViewHeader.appearance().textColor = MalaDetailsButtonBlueColor
        PDTSimpleCalendarViewHeader.appearance().textFont = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        PDTSimpleCalendarViewHeader.appearance().separatorColor = UIColor.clearColor()
        
        ///  日期视图
        PDTSimpleCalendarViewCell.appearance().circleDefaultColor = UIColor.whiteColor()
        PDTSimpleCalendarViewCell.appearance().circleSelectedColor = UIColor.orangeColor()
        PDTSimpleCalendarViewCell.appearance().circleTodayColor = UIColor.whiteColor()
        PDTSimpleCalendarViewCell.appearance().textDefaultColor = UIColor.blackColor()
        PDTSimpleCalendarViewCell.appearance().textSelectedColor = UIColor.whiteColor()
        PDTSimpleCalendarViewCell.appearance().textTodayColor = MalaDetailsButtonBlueColor
        PDTSimpleCalendarViewCell.appearance().textDisabledColor = UIColor.whiteColor()
        PDTSimpleCalendarViewCell.appearance().textDefaultFont = UIFont.systemFontOfSize(MalaLayout_FontSize_15)
    }
    
    
    // MARK: - Default Data
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
                ProfileElementModel(
                    id: 0,
                    title: "学生姓名",
                    detail: MalaUserDefaults.studentName.value ?? "",
                    controller: InfoModifyViewController.self,
                    controllerTitle: "更改名字",
                    type: .StudentName
                ),
                ProfileElementModel(
                    id: 1,
                    title: "学校信息",
                    detail: MalaUserDefaults.schoolName.value ?? "",
                    controller: InfoModifyViewController.self,
                    controllerTitle: "所在学校",
                    type: .StudentSchoolName
                ),
                /*ProfileElementModel(
                    id: 2, 
                    title: "所在城市",
                    detail: "", 
                    controller: InfoModifyViewController.self,
                    controllerTitle: "所在城市",
                    type: nil
                ),*/
                ProfileElementModel(
                    id: 3,
                    title: "我的奖学金",
                    detail: "",
                    controller: CouponViewController.self,
                    controllerTitle: "我的奖学金",
                    type: nil
                )
            ],
            [
                ProfileElementModel(
                    id: 4,
                    title: "关于麻辣老师",
                    detail: "",
                    controller: AboutViewController.self,
                    controllerTitle: "关于麻辣老师",
                    type: nil
                )
            ]
        ]
    }
}