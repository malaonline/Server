//
//  Extension+UIView.swift
//  mala-ios
//
//  Created by Elors on 1/4/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

extension UIView{

    ///  convenience to create a separator line view
    ///
    ///  - returns: UIView
    class func separator() -> UIView {
        let separatorLine = UIView()
        separatorLine.backgroundColor = UIColor(rgbHexValue: 0xc8c8c8, alpha: 0.75)
        return separatorLine
    }
}
