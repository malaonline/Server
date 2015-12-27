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
    convenience init(title: String? = nil, imageName: String? = nil, target: AnyObject? = nil, action:Selector) {
        self.init()
        setTitle(title, forState: .Normal)
        if imageName != nil {
            setImage(UIImage(named: imageName!), forState: .Normal)
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
    convenience init(title: String, titleColor: UIColor? = nil, selectedTitleColor: UIColor? = nil, bgColor: UIColor? = nil, selectedBgColor: UIColor? = nil) {
        self.init()
        setTitle(title, forState: .Normal)
        titleLabel?.font = UIFont.systemFontOfSize(14)
        setTitleColor(titleColor, forState: .Normal)
        setTitleColor(selectedTitleColor, forState: .Selected)
        setBackgroundImage(UIImage.withColor(bgColor), forState: .Normal)
        setBackgroundImage(UIImage.withColor(selectedBgColor), forState: .Selected)
        sizeToFit()
    }
    
}