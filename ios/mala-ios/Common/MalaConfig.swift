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
    class func aboutAPPVersion() -> String {
        let version = String(NSBundle.mainBundle().infoDictionary!["CFBundleShortVersionString"]!)
        return String(format: "版本 V%@", version ?? "1.0")
    }
    ///  版权信息
    class func aboutCopyRightString() -> String {
        return "COPYRIGHT © 2014 - 2016\n北京麻辣在线网络科技有限公司版权所有"
    }
    ///  关于我们描述HTMLString
    class func aboutDescriptionHTMLString() -> String {
        return "        麻辣老师(MALALAOSHI.COM)成立于2015年6月，由众多资深教育人士和互联网顶尖人才组成，是专注于国内二三四线城市中小学K12课外辅导的O2O服务平台，以效果、费用、便捷为切入口，实现个性化教学和学生的个性发展，推动二三四线城市及偏远地区教育进步。\n\n        麻辣老师通过O2O的方式，以高效和精准的老师推荐，让中小学家长更加方便和经济地找到好老师，提升老师的收入，优化教、学、练、测、评五大环节, 提升教学与学习效率、创新服务模式，带给家长、老师及学生全新的学习体验。"
    }
    ///  奖学金使用规则String
    class func couponRulesDescriptionString() -> String {
        return "一.奖学金券是什么\n1.奖学金券是由麻辣老师发行，使用户在麻辣老师购买课程的过程中，作为抵扣现金的一种虚拟券。\n\n二.使用规则\n1.不同的奖学金券面值、有效期和使用限制不尽相同，使用前请认真核对。\n2.一个订单只能使用一张奖学金券。\n3.奖学金券作为一种优惠手段，无法获得对应的积分。\n4.一个订单中的奖学金券部分不能退款或折现，使用奖学金券购买的订单发生退款后不能返还奖学金券。\n5.如取消订单，订单中所使用的奖学金券可再次使用。\n6.奖学金券面值大于订单金额，差额不予退回；如奖学金券面值小于订单金额，需由用户支付差额；奖学金券不可兑现，且不开发票。\n\n三.特别提示\n1.用户应当出于合法、正当目的，以合理方式使用奖学金券。\n2.麻辣老师将不定期的通过版本更新的方式修改使用规则，请您及时升级为最新版本。\n3.如果用户对使用规则存在任何疑问或需要任何帮助，请及时与麻辣老师客服联系。联系电话：010-57733349\n4.最终解释权归北京麻辣在线网络科技有限公司所有。"
    }
    ///  已买课程说明String
    class func boughtDescriptionString() -> String {
        return "您在该时间段有课程未上完，我们将为您保留老师该段的上课时间直到您在该时间段课程全部结束后的12小时，在此期间，方便您继续购买。"
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
            level: 1,
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
    
    
    // MARK: - Static Data
    /// [学科id: 学科名称]
    class func malaSubject() -> [Int: String] {
        return [
            1: "数  学",
            2: "英  语",
            3: "语  文",
            4: "物  理",
            5: "化  学",
            6: "地  理",
            7: "历  史",
            8: "政  治",
            9: "生  物"
        ]
    }
    /// [学科名称: 学科id]
    class func malaSubjectName() -> [String: Int] {
        return [
            "数学": 1,
            "英语": 2,
            "语文": 3,
            "物理": 4,
            "化学": 5,
            "地理": 6,
            "历史": 7,
            "政治": 8,
            "生物": 9
        ]
    }
    /// [年级简称: 年级内部id]
    class func malaGradeShortName() -> [String: Int] {
        return [
            "一年级": 0,
            "二年级": 1,
            "三年级": 2,
            "四年级": 3,
            "五年级": 4,
            "六年级": 5,
            "初一": 0,
            "初二": 1,
            "初三": 2,
            "高一": 0,
            "高二": 1,
            "高三": 2,
        ]
    }
    /// [年级id: 年级全称]
    class func malaGradeName() -> [Int: String] {
        return [
            2: "小学一年级",
            3: "小学二年级",
            4: "小学三年级",
            5: "小学四年级",
            6: "小学五年级",
            7: "小学六年级",
            9: "初中一年级",
            10:"初中二年级",
            11:"初中三年级",
            13:"高中一年级",
            14:"高中二年级",
            15:"高中三年级",
        ]
    }
    /// 星期名称对应数组
    class func malaWeekdays() -> [String] {
        return [
            "周日",
            "周一",
            "周二",
            "周三",
            "周四",
            "周五",
            "周六"
        ]
    }
    /// 老师风格标签颜色数组
    class func malaTagColors() -> [UIColor] {
        return [
            UIColor(rgbHexValue: 0x8FBCDD, alpha: 1.0),
            UIColor(rgbHexValue: 0xF6A466, alpha: 1.0),
            UIColor(rgbHexValue: 0x9BC3E1, alpha: 1.0),
            UIColor(rgbHexValue: 0xAC7BD8, alpha: 1.0),
            UIColor(rgbHexValue: 0xA5B2E4, alpha: 1.0),
            UIColor(rgbHexValue: 0xF4BB5B, alpha: 1.0),
            UIColor(rgbHexValue: 0xA4C87F, alpha: 1.0),
            UIColor(rgbHexValue: 0xEDADD0, alpha: 1.0),
            UIColor(rgbHexValue: 0xABCB71, alpha: 1.0),
            UIColor(rgbHexValue: 0x67CFC8, alpha: 1.0),
            UIColor(rgbHexValue: 0xF58F8F, alpha: 1.0),
            UIColor(rgbHexValue: 0x9BC3E1, alpha: 1.0),
            UIColor(rgbHexValue: 0xE5BEED, alpha: 1.0)
        ]
    }
    /// 支付渠道对象列表
    class func malaPaymentChannels() -> [PaymentChannel] {
        return [
            PaymentChannel(imageName: "alipay_icon", title: "支付宝", subTitle: "支付宝安全支付", channel: .Alipay),
            PaymentChannel(imageName: "wechat_icon", title: "微信支付", subTitle: "微信快捷支付", channel: .Wechat)
        ]
    }
    
    
    // MARK: - Static Config
    ///  [个人中心]静态结构数据
    class func profileData() -> [[ProfileElementModel]] {        
        return [
            [
                ProfileElementModel(
                    id: 0,
                    controller: OrderFormViewController.self,
                    controllerTitle: "我的收藏",
                    type: nil,
                    iconName: "profile_collect",
                    newMessageIconName: "",
                    disabled: true
                ),
                ProfileElementModel(
                    id: 1,
                    controller: OrderFormViewController.self,
                    controllerTitle: "我的订单",
                    type: nil,
                    iconName: "profile_order",
                    newMessageIconName: "profile_unpaid"
                ),
                ProfileElementModel(
                    id: 2,
                    controller: CommentViewController.self,
                    controllerTitle: "我的评价",
                    type: nil,
                    iconName: "profile_comment",
                    newMessageIconName: "profile_uncomment"
                )
            ],
            [
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
    
    class func memberServiceData() -> [IntroductionModel] {
        return [
            IntroductionModel(
                title: "自习陪读",
                image: "selfStudy",
                subTitle: "享受专业老师免费陪读服务，随时解决学习问题"
            ),
            IntroductionModel(
                title: "学习报告",
                image: "learningReport",
                subTitle: "全面记录学生学习数据，方便家长、随时查看，充分了解学员知识点掌握情况"
            ),
            IntroductionModel(
                title: "心理辅导",
                image: "counseling",
                subTitle: "免费获得专业心理咨询师一对一心理辅导，促进学员身心健康成长"
            ),
            IntroductionModel(
                title: "特色讲座",
                image: "featuredLectures",
                subTitle: "特邀各领域专家进行多种特色讲座，营养健康、家庭教育、高效学习应有尽有"
            ),
            IntroductionModel(
                title: "考前串讲",
                image: "examOutlineLecture",
                subTitle: "专业解读考试趋势，剖析考试难点分享高分经验。还有命题专家进行中高考押题"
            ),
            IntroductionModel(
                title: "错题本",
                image: "correctedNotebook",
                subTitle: "针对每个学员记录并生成错题本，方便查找知识漏洞，并生成针对性练习"
            ),
            IntroductionModel(
                title: "SPPS测评",
                image: "SPPSTest",
                subTitle: "定期进行SPPS测评，充分了解学员学习情况"
            ),
            IntroductionModel(
                title: "敬请期待",
                image: "StayTuned",
                subTitle: "敬请期待..."
            )
        ]
    }
    
    class func chartsColor() -> [UIColor] {
        return [
            MalaColor_F8DB6B_0,
            MalaColor_6DC9CE_0,
            MalaColor_F9877C_0,
            MalaColor_69CC99_0,
            MalaColor_88BCDE_0,
            MalaColor_8BA3CA_0,
            MalaColor_F7AF63_0,
            MalaColor_BA9CDA_0,
            MalaColor_C09C8B_0,
        ]
    }
    
    class func homeworkDataChartsTitle() -> [String] {
        return [
            "实数", "函数初步", "多边形", "相似", "全等", "相似", "几何变换", "圆", "其它"
        ]
    }
    
    ///  ［作业数据分析页面］样本数据
    class func homeworkSampleData() -> [SingleHomeworkData] {
        return [
            SingleHomeworkData(id: 1, name: "实数", rate: NSNumber(double: 0.15)),
            SingleHomeworkData(id: 2, name: "函数初步", rate: NSNumber(double: 0.20)),
            SingleHomeworkData(id: 3, name: "多边形", rate: NSNumber(double: 0.13)),
            SingleHomeworkData(id: 4, name: "圆", rate: NSNumber(double: 0.06)),
            SingleHomeworkData(id: 5, name: "全等", rate: NSNumber(double: 0.2)),
            SingleHomeworkData(id: 6, name: "相似", rate: NSNumber(double: 0.06)),
            SingleHomeworkData(id: 7, name: "几何变换", rate: NSNumber(double: 0.12)),
            SingleHomeworkData(id: 8, name: "几何操作", rate: NSNumber(double: 0.08))
        ]
    }
    
    ///  ［题目数据分析页面］样本数据
    class func topicSampleData() -> [SingleTimeIntervalData] {
        return [
            SingleTimeIntervalData(totalItem: 175, errorItem: 77, year: 2016, month: 4, day: 16),
            SingleTimeIntervalData(totalItem: 185, errorItem: 63, year: 2016, month: 5, day: 1),
            SingleTimeIntervalData(totalItem: 192, errorItem: 46, year: 2016, month: 5, day: 16),
            SingleTimeIntervalData(totalItem: 225, errorItem: 43, year: 2016, month: 6, day: 1)
        ]
    }
    
    ///  ［知识点分析页面］样本数据
    class func knowledgeSampleData() -> [SingleTopicData] {
        return [
            SingleTopicData(id: 1, name: "实数", totalItem: 100, rightItem: 49),
            SingleTopicData(id: 2, name: "函数初步", totalItem: 90, rightItem: 20),
            SingleTopicData(id: 3, name: "圆", totalItem: 100, rightItem: 80),
            SingleTopicData(id: 4, name: "多边形", totalItem: 105, rightItem: 60),
            SingleTopicData(id: 5, name: "全等", totalItem: 100, rightItem: 30),
            SingleTopicData(id: 6, name: "相似", totalItem: 125, rightItem: 95),
            SingleTopicData(id: 7, name: "几何变换", totalItem: 80, rightItem: 40),
            SingleTopicData(id: 8, name: "几何操作", totalItem: 70, rightItem: 40),
        ]
    }
    
    ///  ［知识点分析页面］默认数据
    class func knowledgeDefaultData() -> [SingleTopicData] {
        return [
            SingleTopicData(id: 1, name: "实数", totalItem: 100, rightItem: 0),
            SingleTopicData(id: 2, name: "函数初步", totalItem: 100, rightItem: 0),
            SingleTopicData(id: 3, name: "圆", totalItem: 100, rightItem: 0),
            SingleTopicData(id: 4, name: "多边形", totalItem: 100, rightItem: 0),
            SingleTopicData(id: 5, name: "全等", totalItem: 100, rightItem: 0),
            SingleTopicData(id: 6, name: "相似", totalItem: 100, rightItem: 0),
            SingleTopicData(id: 7, name: "几何变换", totalItem: 100, rightItem: 0),
            SingleTopicData(id: 8, name: "几何操作", totalItem: 100, rightItem: 0),
        ]
    }
    
    ///  ［能力结构分析页面］样本数据
    class func abilitySampleData() -> [SingleAbilityData] {
        return [
            SingleAbilityData(key: "reason", value: 60),
            SingleAbilityData(key: "data", value: 30),
            SingleAbilityData(key: "spatial", value: 42),
            SingleAbilityData(key: "calc", value: 33),
            SingleAbilityData(key: "appl", value: 82),
            SingleAbilityData(key: "abstract", value: 66)
        ]
    }
    
    ///  ［提分点分析页面］样本数据
    class func scoreSampleData() -> [SingleTopicScoreData] {
        return [
            SingleTopicScoreData(id: 1, name: "实数", score: NSNumber(double: 0.47), aveScore: NSNumber(double: 0.72)),
            SingleTopicScoreData(id: 2, name: "函数初步", score: NSNumber(double: 0.23), aveScore: NSNumber(double: 0.64)),
            SingleTopicScoreData(id: 3, name: "圆", score: NSNumber(double: 0.80), aveScore: NSNumber(double: 0.70)),
            SingleTopicScoreData(id: 4, name: "多边形", score: NSNumber(double: 0.56), aveScore: NSNumber(double: 0.59)),
            SingleTopicScoreData(id: 5, name: "全等", score: NSNumber(double: 0.27), aveScore: NSNumber(double: 0.51)),
            SingleTopicScoreData(id: 6, name: "相似", score: NSNumber(double: 0.77), aveScore: NSNumber(double: 0.62)),
            SingleTopicScoreData(id: 7, name: "几何变换", score: NSNumber(double: 0.50), aveScore: NSNumber(double: 0.77)),
            SingleTopicScoreData(id: 8, name: "几何操作", score: NSNumber(double: 0.55), aveScore: NSNumber(double: 0.21))
        ]
    }
}