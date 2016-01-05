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


// MARK: - Appearance TintColor
let MalaAppearanceTintColor = UIColor.redColor()
let MalaAppearanceTextColor = UIColor.whiteColor()


// MARK: - Common String
let MalaCommonString_Malalaoshi = "麻辣老师"
let MalaCommonString_Profile = "个人"
let MalaCommonString_PhoneNumber = "手机号"
let MalaCommonString_Cancel = "取消"
let MalaCommonString_VerifyCode = "验证码"
let MalaCommonString_GetVerifyCode = "获取验证码"


// MARK: - Common Proportion
let MalaProportion_HomeCellWidthWithScreenWidth: CGFloat = 0.47
let MalaProportion_HomeCellMarginWithScreenWidth: CGFloat = 0.02
let MalaProportion_HomeCellHeightWithWidth: CGFloat = 1.28


// MARK: - Device
let MalaScreenWidth = UIScreen.mainScreen().bounds.size.width
let MalaScreenHeight = UIScreen.mainScreen().bounds.size.height
// ScreenHeight Without StatusBar,NavigationBar,TabBar
let MalaContentHeight = UIScreen.mainScreen().bounds.size.height-20-44-48


// MARK: - Common TextAttribute
public func commonTextStyle() -> [String: AnyObject]? {
    let AttributeDictionary = NSMutableDictionary()
    AttributeDictionary[NSForegroundColorAttributeName] = UIColor.whiteColor()
    return AttributeDictionary.copy() as? [String : AnyObject]
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
