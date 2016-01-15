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
    convenience init(title: String? = nil, imageName: String? = nil, selectImageName: String? = nil, target: AnyObject? = nil, action:Selector) {
        self.init()
        titleLabel?.font = UIFont.systemFontOfSize(15)
        setTitle(title, forState: .Normal)
        setTitleColor(MalaAppearanceTextColor, forState: .Normal)
        if imageName != nil {
            setImage(UIImage(named: imageName!), forState: .Normal)
            setImage(UIImage(named: selectImageName!), forState: .Selected)
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
}