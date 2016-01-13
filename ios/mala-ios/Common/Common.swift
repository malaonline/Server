//
//  Common.swift
//  mala-ios
//
//  Created by Elors on 15/12/17.
//  Copyright © 2015年 Mala Online. All rights reserved.
//

import UIKit

// MARK: - Identifier
let Mala_Umeng_AppKey: String = "5680ebb367e58e4945002f59"
var Mala_UserToken: String = "0"

// MARK: - NotificationName
let MalaNotification_OpenSchoolsCell = "MalaNotification_OpenSchoolsCell"


// MARK: - Appearance TintColor
let MalaAppearanceTintColor = UIColor.whiteColor()
let MalaAppearanceTextColor = UIColor(rgbHexValue: 0x6C6C6C, alpha: 1.0)
let MalaDetailsCellTitleColor = UIColor(rgbHexValue: 0x333333, alpha: 1.0)
let MalaDetailsCellLabelColor = UIColor(rgbHexValue: 0x636363, alpha: 1.0)
let MalaDetailsCellSubTitleColor = UIColor(rgbHexValue: 0x939393, alpha: 1.0)
let MalaDetailsButtonBlueColor = UIColor(rgbHexValue: 0x82B4D9, alpha: 1.0)
let MalaDetailsButtonBorderColor = UIColor(rgbHexValue: 0xE5E5E5, alpha: 1.0)
let MalaDetailsBottomViewColor = UIColor(rgbHexValue: 0xF6F6F6, alpha: 0.96)
let MalaDetailsPriceRedColor = UIColor(rgbHexValue: 0xE26254, alpha: 1.0)


// MARK: - Common String
let MalaCommonString_Malalaoshi = "麻辣老师"
let MalaCommonString_Profile = "个人"
let MalaCommonString_Title = "标题"
let MalaCommonString_PhoneNumber = "手机号"
let MalaCommonString_Cancel = "取消"
let MalaCommonString_VerifyCode = "验证码"
let MalaCommonString_GetVerifyCode = "获取验证码"


// MARK: - Common Proportion
let MalaProportion_HomeCellWidthWithScreenWidth: CGFloat = 0.47
let MalaProportion_HomeCellMarginWithScreenWidth: CGFloat = 0.02
let MalaProportion_HomeCellHeightWithWidth: CGFloat = 1.28
let MalaProportion_DetailPhotoHeightWidthWith: CGFloat = 0.75


// MARK: - Common layout
let MalaLayout_Margin_3: CGFloat = 3.0
let MalaLayout_Margin_4: CGFloat = 4.0
let MalaLayout_Margin_5: CGFloat = 5.0
let MalaLayout_Margin_6: CGFloat = 6.0
let MalaLayout_Margin_7: CGFloat = 7.0
let MalaLayout_Margin_8: CGFloat = 8.0
let MalaLayout_Margin_10: CGFloat = 10.0
let MalaLayout_Margin_12: CGFloat = 12.0
let MalaLayout_Margin_13: CGFloat = 13.0
let MalaLayout_Margin_14: CGFloat = 14.0
let MalaLayout_Margin_15: CGFloat = 15.0
let MalaLayout_Margin_16: CGFloat = 16.0

let MalaLayout_FontSize_10: CGFloat = 10.0
let MalaLayout_FontSize_12: CGFloat = 12.0
let MalaLayout_FontSize_14: CGFloat = 14.0
let MalaLayout_FontSize_15: CGFloat = 15.0
let MalaLayout_FontSize_16: CGFloat = 16.0

let MalaLayout_AvatarSize: CGFloat = 70.0
let MalaLayout_VipIconSize: CGFloat = 15.0
let MalaLayout_DetailHeaderLayerHeight: CGFloat = MalaLayout_DetailHeaderHeight - 6.0
let MalaLayout_DetailHeaderHeight: CGFloat = 146.0
let MalaLayout_DetailHeaderContentHeight: CGFloat = 60.0
let MalaLayout_DeatilHighScoreTableViewCellHeight: CGFloat = 33.0
let MalaLayout_DetailPhotoWidth: CGFloat = (MalaScreenWidth - (MalaLayout_Margin_12*2) - (MalaLayout_Margin_5*3))/3
let MalaLayout_DetailPhotoHeight: CGFloat = MalaLayout_DetailPhotoWidth * MalaProportion_DetailPhotoHeightWidthWith
let MalaLayout_DetailPriceTableViewCellHeight: CGFloat = 71.0
let MalaLayout_DetailSchoolsTableViewCellHeight: CGFloat = 110.0
let MalaLayout_DetailBottomViewHeight: CGFloat = 49.0


// MARK: - Device
let MalaScreenNaviHeight: CGFloat = 64.0
let MalaScreenWidth = UIScreen.mainScreen().bounds.size.width
let MalaScreenHeight = UIScreen.mainScreen().bounds.size.height
// ScreenHeight Without StatusBar,NavigationBar,TabBar
let MalaContentHeight = UIScreen.mainScreen().bounds.size.height-20-44-48
let MalaScreenOnePixel = 1/UIScreen.mainScreen().scale


// MARK: - Common TextAttribute
public func commonTextStyle() -> [String: AnyObject]? {
    let AttributeDictionary = NSMutableDictionary()
    AttributeDictionary[NSForegroundColorAttributeName] = MalaAppearanceTextColor
    return AttributeDictionary.copy() as? [String : AnyObject]
}


// MARK: - Method
public func makeStatusBarBlack() {
    UIApplication.sharedApplication().statusBarStyle = .Default
}
public func makeStatusBarWhite() {
    UIApplication.sharedApplication().statusBarStyle = .LightContent
}


// MARK: - TitleFilter
let MalaSubject = [
    1:"语文",
    2:"数学",
    3:"英语",
    4:"物理",
    5:"化学",
    6:"生物",
    7:"历史",
    8:"地理",
    9:"政治"
]
let MalaTeacherDetailsCellTitle = [
    1:"教授科目",
    2:"风格标签",
    3:"提分榜",
    4:"个人相册",
    5:"特殊成就",
    6:"教学环境",
    7:"会员服务",
    8:"级别",
    9:"价格表"
]