//
//  Extension+UIColor.swift
//  mala-ios
//
//  Created by Elors on 15/12/20.
//  Copyright © 2015年 Mala Online. All rights reserved.
//

import UIKit

extension UIColor {

     ///  Convenience Function to Create UIColor With Hex RGBValue
     ///
     ///  - parameter rgbHexValue: Hex RGBValue
     ///  - parameter alpha:       Alpha
     ///
     ///  - returns: UIColor
    convenience init(rgbHexValue: UInt32 = 0xFFFFFF, alpha: Double = 1.0) {
        let red = CGFloat((rgbHexValue & 0xFF0000) >> 16)/256.0
        let green = CGFloat((rgbHexValue & 0xFF00) >> 8)/256.0
        let blue = CGFloat(rgbHexValue & 0xFF)/256.0
        self.init(red:red, green:green, blue:blue, alpha:CGFloat(alpha))
    }
}
