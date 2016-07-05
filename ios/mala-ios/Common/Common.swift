//
//  Common.swift
//  mala-ios
//
//  Created by Elors on 15/12/17.
//  Copyright © 2015年 Mala Online. All rights reserved.
//

import UIKit
import CoreLocation


// MARK: - Identifier
let Mala_Umeng_AppKey: String = "5680ebb367e58e4945002f59"
#if USE_PRD_SERVER
    let Mala_JPush_AppKey: String = "f22a395a332b87ef57a04b82"
#else
    let Mala_JPush_AppKey: String = "06c87b3317e17c7af30544ce"
#endif

// MARK: - Variables
/// 课时选择步增数
var MalaClassPeriod_StepValue: Double = 2
var MalaIsPaymentIn: Bool = false
var MalaIsForeground: Bool = true
/// 用户未支付订单数
var MalaUnpaidOrderCount: Int = 0
/// 用户待评价课程数
var MalaToCommentCount: Int = 0
/// 登陆后获取用户所在地理位置信息
var MalaLoginLocation: CLLocation? = nil
/// 当前加载闭包（用于请求失败或403时重试）
var MalaCurrentInitAction: (()->())?
/// 取消加载闭包（用于请求失败或403时返回）
var MalaCurrentCancelAction: (()->())?


// MARK: - NotificationName
let MalaNotification_OpenSchoolsCell = "com.malalaoshi.app.OpenSchoolsCell"
let MalaNotification_PushPhotoBrowser = "com.malalaoshi.app.PushPhotoBrowser"
let MalaNotification_PopFilterView = "com.malalaoshi.app.PopFilterView"
let MalaNotification_ConfirmFilterView = "com.malalaoshi.app.ConfirmFilterView"
let MalaNotification_CommitCondition = "com.malalaoshi.app.CommitCondition"
let MalaNotification_ChoosingGrade = "com.malalaoshi.app.ChoosingGrade"
let MalaNotification_ChoosingSchool = "com.malalaoshi.app.ChoosingSchool"
let MalaNotification_ClassScheduleDidTap = "com.malalaoshi.app.ClassScheduleDidTap"
let MalaNotification_ClassPeriodDidChange = "com.malalaoshi.app.ClassPeriodDidChange"
let MalaNotification_OpenTimeScheduleCell = "com.malalaoshi.app.OpenTimeScheduleCell"
let MalaNotification_PushTeacherDetailView = "com.malalaoshi.app.PushTeacherDetailView"
let MalaNotification_CancelOrderForm = "com.malalaoshi.app.CancelOrderForm"
let MalaNotification_PushToPayment = "com.malalaoshi.app.PushToPayment"
let MalaNotification_PushIntroduction = "com.malalaoshi.app.PushIntroduction"
let MalaNotification_ShowLearningReport = "com.malalaoshi.app.ShowLearningReport"
let MalaNotification_RefreshStudentName = "com.malalaoshi.app.RefreshStudentName"
let MalaNotification_PushProfileItemController = "com.malalaoshi.app.PushProfileItemController"


// MARK: - Error Detail
let MalaErrorDetail_InvalidPage = "Invalid page"


// MARK: - Appearance TintColor
let MalaColor_WhiteColor = UIColor.whiteColor()
let MalaColor_6C6C6C_0 = UIColor(rgbHexValue: 0x6C6C6C, alpha: 1.0)
let MalaColor_EDEDED_0 = UIColor(rgbHexValue: 0xEDEDED, alpha: 1.0)
let MalaColor_DADADA_0 = UIColor(rgbHexValue: 0xDADADA, alpha: 1.0)
let MalaColor_E26254_0 = UIColor(rgbHexValue: 0xE26254, alpha: 1.0)
let MalaColor_333333_0 = UIColor(rgbHexValue: 0x333333, alpha: 1.0)
let MalaColor_333333_6 = UIColor(rgbHexValue: 0x333333, alpha: 0.6)
let MalaColor_636363_0 = UIColor(rgbHexValue: 0x636363, alpha: 1.0)
let MalaColor_939393_0 = UIColor(rgbHexValue: 0x939393, alpha: 1.0)
let MalaColor_E5E5E5_0 = UIColor(rgbHexValue: 0xE5E5E5, alpha: 1.0)
let MalaColor_E5E5E5_3 = UIColor(rgbHexValue: 0xE5E5E5, alpha: 0.3)
let MalaColor_F6F6F6_96 = UIColor(rgbHexValue: 0xF6F6F6, alpha: 0.96)
let MalaColor_E36A5D_0 = UIColor(rgbHexValue: 0xE36A5D, alpha: 1.0)
let MalaColor_CECECE_0 = UIColor(rgbHexValue: 0xCECECE, alpha: 1.0)
let MalaColor_8DBEDF_0 = UIColor(rgbHexValue: 0x8DBEDF, alpha: 1.0)
let MalaColor_E0E0E0_95 = UIColor(rgbHexValue: 0xE0E0E0, alpha: 0.95)
let MalaColor_88BCDE_95 = UIColor(rgbHexValue: 0x88BCDE, alpha: 0.95)
let MalaColor_88BCDE_0 = UIColor(rgbHexValue: 0x88BCDE, alpha: 1.0)
let MalaColor_88BCDE_5 = UIColor(rgbHexValue: 0x88BCDE, alpha: 0.5)
let MalaColor_9D9D9D_0 = UIColor(rgbHexValue: 0x9D9D9D, alpha: 1.0)
let MalaColor_8BBADC_0 = UIColor(rgbHexValue: 0x8BBADC, alpha: 1.0)
let MalaColor_8FBCDD_0 = UIColor(rgbHexValue: 0x8FBCDD, alpha: 1.0)
let MalaColor_C7DEEE_0 = UIColor(rgbHexValue: 0xC7DEEE, alpha: 1.0)
let MalaColor_82B4D9_0 = UIColor(rgbHexValue: 0x82B4D9, alpha: 1.0)
let MalaColor_D4D4D4_0 = UIColor(rgbHexValue: 0xD4D4D4, alpha: 1.0)
let MalaColor_F2F2F2_0 = UIColor(rgbHexValue: 0xF2F2F2, alpha: 1.0)
let MalaColor_FFFFFF_9 = UIColor(rgbHexValue: 0xFFFFFF, alpha: 0.9)
let MalaColor_4A4A4A_0 = UIColor(rgbHexValue: 0x4A4A4A, alpha: 1.0)
let MalaColor_DEE0E0_0 = UIColor(rgbHexValue: 0xDEE0E0, alpha: 1.0)
let MalaColor_B7B7B7_0 = UIColor(rgbHexValue: 0xB7B7B7, alpha: 1.0)
let MalaColor_A5C9E4_0 = UIColor(rgbHexValue: 0xA5C9E4, alpha: 1.0)
let MalaColor_D0D0D0_0 = UIColor(rgbHexValue: 0xD0D0D0, alpha: 1.0)
let MalaColor_F8F8F8_0 = UIColor(rgbHexValue: 0xF8F8F8, alpha: 1.0)
let MalaColor_BCD7EB_0 = UIColor(rgbHexValue: 0xBCD7EB, alpha: 1.0)
let MalaColor_BEBEBE_0 = UIColor(rgbHexValue: 0xBEBEBE, alpha: 1.0)
let MalaColor_C4C4C4_0 = UIColor(rgbHexValue: 0xC4C4C4, alpha: 1.0)
let MalaColor_000000_3 = UIColor(rgbHexValue: 0x000000, alpha: 0.3)
let MalaColor_000000_0 = UIColor(rgbHexValue: 0x000000, alpha: 1.0)
let MalaColor_ABD0E8_0 = UIColor(rgbHexValue: 0xABD0E8, alpha: 1.0)
let MalaColor_C7C7CC_0 = UIColor(rgbHexValue: 0xC7C7CC, alpha: 1.0)
let MalaColor_B1D0E8_0 = UIColor(rgbHexValue: 0xB1D0E8, alpha: 1.0)
let MalaColor_CFCFCF_0 = UIColor(rgbHexValue: 0xCFCFCF, alpha: 1.0)
let MalaColor_83B84F_0 = UIColor(rgbHexValue: 0x83B84F, alpha: 1.0)
let MalaColor_E36A5C_0 = UIColor(rgbHexValue: 0xE36A5C, alpha: 1.0)
let MalaColor_8DC1DE_0 = UIColor(rgbHexValue: 0x8DC1DE, alpha: 1.0)
let MalaColor_2AAADD_0 = UIColor(rgbHexValue: 0x2AAADD, alpha: 1.0)
let MalaColor_4DA3D9_0 = UIColor(rgbHexValue: 0x4DA3D9, alpha: 1.0)
let MalaColor_FDAF6B_0 = UIColor(rgbHexValue: 0xFDAF6B, alpha: 1.0)
let MalaColor_5E5E5E_0 = UIColor(rgbHexValue: 0x5E5E5E, alpha: 1.0)
let MalaColor_8DBEDE_0 = UIColor(rgbHexValue: 0x8DBEDE, alpha: 1.0)
let MalaColor_E8F2F8_0 = UIColor(rgbHexValue: 0xE8F2F8, alpha: 1.0)
let MalaColor_F8DB6B_0 = UIColor(rgbHexValue: 0xF8DB6B, alpha: 1.0)
let MalaColor_6DC9CE_0 = UIColor(rgbHexValue: 0x6DC9CE, alpha: 1.0)
let MalaColor_F9877C_0 = UIColor(rgbHexValue: 0xF9877C, alpha: 1.0)
let MalaColor_69CC99_0 = UIColor(rgbHexValue: 0x69CC99, alpha: 1.0)
let MalaColor_8BA3CA_0 = UIColor(rgbHexValue: 0x8BA3CA, alpha: 1.0)
let MalaColor_F7AF63_0 = UIColor(rgbHexValue: 0xF7AF63, alpha: 1.0)
let MalaColor_BA9CDA_0 = UIColor(rgbHexValue: 0xBA9CDA, alpha: 1.0)
let MalaColor_C09C8B_0 = UIColor(rgbHexValue: 0xC09C8B, alpha: 1.0)
let MalaColor_F8FAFD_0 = UIColor(rgbHexValue: 0xF8FAFD, alpha: 1.0)
let MalaColor_D7D7D7_0 = UIColor(rgbHexValue: 0xD7D7D7, alpha: 1.0)
let MalaColor_363B4E_0 = UIColor(rgbHexValue: 0x363B4E, alpha: 1.0)
let MalaColor_BBDDF6_0 = UIColor(rgbHexValue: 0xBBDDF6, alpha: 1.0)
let MalaColor_75CC97_0 = UIColor(rgbHexValue: 0x75CC97, alpha: 1.0)
let MalaColor_E6E9EC_0 = UIColor(rgbHexValue: 0xE6E9EC, alpha: 1.0)
let MalaColor_C9E4F8_0 = UIColor(rgbHexValue: 0xC9E4F8, alpha: 1.0)
let MalaColor_82C9F9_0 = UIColor(rgbHexValue: 0x82C9F9, alpha: 1.0)
let MalaColor_97A8BB_0 = UIColor(rgbHexValue: 0x97A8BB, alpha: 1.0)
let MalaColor_FFF0EE_0 = UIColor(rgbHexValue: 0xFFF0EE, alpha: 1.0)
let MalaColor_E6F1FC_0 = UIColor(rgbHexValue: 0xE6F1FC, alpha: 1.0)
let MalaColor_6DB2E5_0 = UIColor(rgbHexValue: 0x6DB2E5, alpha: 1.0)
let MalaColor_999999_0 = UIColor(rgbHexValue: 0x999999, alpha: 1.0)
let MalaColor_828282_0 = UIColor(rgbHexValue: 0x828282, alpha: 1.0)
let MalaColor_EEEEEE_0 = UIColor(rgbHexValue: 0xEEEEEE, alpha: 1.0)
let MalaColor_FA7A7A_0 = UIColor(rgbHexValue: 0xFA7A7A, alpha: 1.0)
let MalaColor_FDDC55_0 = UIColor(rgbHexValue: 0xFDDC55, alpha: 1.0)
let MalaColor_7FB4DC_0 = UIColor(rgbHexValue: 0x7FB4DC, alpha: 1.0)


// MARK: - Common String
let MalaCommonString_Malalaoshi = "麻辣老师"
let MalaCommonString_FindTeacher = "找老师"
let MalaCommonString_ClassSchedule = "课表"
let MalaCommonString_MemberPrivileges = "会员专享"
let MalaCommonString_Profile = "我的"
let MalaCommonString_Title = "标题"
let MalaCommonString_PhoneNumber = "手机号"
let MalaCommonString_Cancel = "取消"
let MalaCommonString_VerifyCode = "验证码"
let MalaCommonString_FilterResult = "筛选结果"
let MalaCommonString_CourseChoosing = "课程购买"
let MalaCommonString_EvaluationFiling = "测评建档服务"
let MalaCommonString_CommentPlaceholder = "请写下对老师的感受吧，对他人的帮助很大哦~最多可输入200字"


// MARK: - Common layout
let MalaLayout_CardCellWidth: CGFloat = MalaScreenWidth - (12*2)
let MalaLayout_GradeSelectionWidth: CGFloat = (MalaLayout_CardCellWidth - 12)/2
let MalaLayout_AvatarSize: CGFloat = 75.0
let MalaLayout_VipIconSize: CGFloat = 15.0
let MalaLayout_DetailHeaderContentHeight: CGFloat = 150.0
let MalaLayout_DeatilHighScoreTableViewCellHeight: CGFloat = 33.0
let MalaLayout_DetailPhotoWidth: CGFloat = (MalaLayout_CardCellWidth - 10)/3
let MalaLayout_DetailPhotoHeight: CGFloat = MalaLayout_DetailPhotoWidth*0.75
let MalaLayout_DetailPriceTableViewCellHeight: CGFloat = 71.0
let MalaLayout_DetailSchoolsTableViewCellHeight: CGFloat = 107.0
let MalaLayout_DetailBottomViewHeight: CGFloat = 49.0
let MalaLayout_FilterWindowWidth: CGFloat = MalaScreenWidth*0.85
let MalaLayout_FilterWindowHeight: CGFloat = MalaLayout_FilterWindowWidth
let MalaLayout_FilterContentWidth: CGFloat = MalaLayout_FilterWindowWidth - 26*2
let MalaLayout_FilterItemWidth: CGFloat = MalaLayout_FilterContentWidth/2
let MalaLayout_FilterBarHeight: CGFloat = 40
let MalaLayout_OtherServiceCellHeight: CGFloat = 46
let MalaLayout_ProfileHeaderViewHeight: CGFloat = 190
let MalaLayout_ProfileModifyViewHeight: CGFloat = 48
let MalaLayout_AboutAPPLogoViewHeight: CGFloat = 62
let MalaLayout_CoursePopupWindowWidth: CGFloat = 272
let MalaLayout_CoursePopupWindowHeight: CGFloat = 300
let MalaLayout_CoursePopupWindowTitleViewHeight: CGFloat = 69
let MalaLayout_CourseContentWidth: CGFloat = MalaLayout_CoursePopupWindowWidth - 26*2
let MalaLayout_CommentPopupWindowHeight: CGFloat = 420
let MalaLayout_CommentPopupWindowWidth: CGFloat = 300
let MalaLayout_CouponRulesPopupWindowHeight: CGFloat = 500


// MARK: - Device
let MalaScreenNaviHeight: CGFloat = 64.0
let MalaScreenWidth = UIScreen.mainScreen().bounds.size.width
let MalaScreenHeight = UIScreen.mainScreen().bounds.size.height
let MalaScreenOnePixel = 1/UIScreen.mainScreen().scale
let MalaScreenScale = UIScreen.mainScreen().scale


// MARK: - Array
var MalaOtherService = [
    OtherServiceModel(title: "奖学金", type: .Coupon, price: 0, priceHandleType: .None, viewController: CouponViewController.self),
    OtherServiceModel(title: MalaCommonString_EvaluationFiling, type: .EvaluationFiling, price: 500, priceHandleType: .Reduce, viewController: EvaluationFilingServiceController.self)
]