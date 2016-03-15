//
//  Extension+UIButton.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

extension UIButton {
    
     ///  Convenience Function to Create UIButton
     ///  (Usually Use For UIBarButtonItem)
     ///
     ///  - parameter title:     String for Title
     ///  - parameter imageName: String for ImageName
     ///  - parameter target:    Object for Event's Target
     ///  - parameter action:    SEL for Event's Action
     ///
     ///  - returns: UIButton
    convenience init(title: String? = nil, imageName: String? = nil, highlightImageName: String? = nil, target: AnyObject? = nil, action:Selector) {
        self.init()
        titleLabel?.font = UIFont.systemFontOfSize(15)
        setTitle(title, forState: .Normal)
        setTitleColor(MalaAppearanceTextColor, forState: .Normal)
        if imageName != nil {
            setImage(UIImage(named: imageName!), forState: .Normal)
        }
        if highlightImageName != nil {
            setImage(UIImage(named: highlightImageName!), forState: .Highlighted)
        }
        addTarget(target, action: action, forControlEvents: .TouchUpInside)
        sizeToFit()
    }
    
    ///  Convenience Function to Create UIButton With TitleColor and BackgroundColor
    ///
    ///  - parameter title:              String for Title
    ///  - parameter titleColor:         UIColor for TitleColor in NormalState
    ///  - parameter selectedTitleColor: UIColor for TitleColor in SelectedState
    ///  - parameter bgColor:            UIColor for BackgroundColor in NormalState
    ///  - parameter selectedBgColor:    UIColor for BackgroundColor in SelectedState
    ///
    ///  - returns: UIButton
    convenience init(title: String, titleColor: UIColor? = nil, selectedTitleColor: UIColor? = nil, bgColor: UIColor = UIColor.whiteColor(), selectedBgColor: UIColor = UIColor.whiteColor()) {
        self.init()
        setTitle(title, forState: .Normal)
        titleLabel?.font = UIFont.systemFontOfSize(14)
        setTitleColor(titleColor, forState: .Normal)
        setTitleColor(selectedTitleColor, forState: .Selected)
        setBackgroundImage(UIImage.withColor(bgColor), forState: .Normal)
        setBackgroundImage(UIImage.withColor(selectedBgColor), forState: .Selected)
        sizeToFit()
    }
    
    ///  Convenience to Create UIButton With Title, TitleColor and BackgroundColor
    ///  FontSize is Default to 16
    ///  
    ///  - parameter title:           String for Title
    ///  - parameter titleColor:      UIColor for TitleColor
    ///  - parameter backgroundColor: UIColor for BackgroundColor
    ///
    ///  - returns: UIButton
    convenience init(title: String, titleColor: UIColor? = nil, backgroundColor: UIColor? = nil) {
        self.init()
        setTitle(title, forState: .Normal)
        titleLabel?.font = UIFont.systemFontOfSize(16)
        setTitleColor(titleColor, forState: .Normal)
        self.backgroundColor = backgroundColor
        sizeToFit()
    }
    
    func exchangeImageAndLabel(padding: CGFloat) {
        self.imageEdgeInsets = UIEdgeInsets(top: 0, left: titleLabel!.frame.width + padding, bottom: 0, right: -titleLabel!.frame.width + padding)
        self.titleEdgeInsets = UIEdgeInsets(top: 0, left: -imageView!.frame.width, bottom: 0, right: imageView!.frame.width)
    }
    
    ///  便利构造函数
    ///
    ///  - parameter title:       标题
    ///  - parameter borderColor: Normal状态边框颜色，Highlighted状态背景颜色
    ///
    ///  - returns: UIButton对象
    convenience init(title: String, borderColor: UIColor, target: AnyObject?, action: Selector) {
        self.init()
        // 文字及其状态颜色
        self.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        self.setTitle(title, forState: .Normal)
        self.setTitleColor(borderColor, forState: .Normal)
        self.setTitleColor(UIColor.whiteColor(), forState: .Highlighted)
        self.setTitleColor(UIColor.whiteColor(), forState: .Selected)
        // 背景状态颜色
        self.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Normal)
        self.setBackgroundImage(UIImage.withColor(borderColor), forState: .Highlighted)
        self.setBackgroundImage(UIImage.withColor(borderColor), forState: .Selected)
        // 圆角和边框
        self.layer.cornerRadius = 5
        self.layer.masksToBounds = true
        self.layer.borderColor = borderColor.CGColor
        self.layer.borderWidth = MalaScreenOnePixel
        self.addTarget(target, action: action, forControlEvents: .TouchUpInside)
    }
    
    ///  便利构造函数
    ///
    ///  - parameter title:      标题文字
    ///  - parameter titleColor: 标题文字颜色
    ///  - parameter target:     点击事件Handler
    ///  - parameter action:     点击事件Action
    ///
    ///  - returns: UIButton对象
    convenience init(title: String, titleColor: UIColor, target: AnyObject?, action: Selector) {
        self.init()
        // 文字及其状态颜色
        self.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        self.setTitle(title, forState: .Normal)
        self.setTitleColor(titleColor, forState: .Normal)
        self.setTitleColor(titleColor, forState: .Highlighted)
        self.addTarget(target, action: action, forControlEvents: .TouchUpInside)
        self.sizeToFit()
    }
}