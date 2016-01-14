//
//  Extension+UIImage.swift
//  mala-ios
//
//  Created by Elors on 12/22/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

extension UIImage {
    
    ///  Create a UIImage From UIColor
    ///
    ///  - parameter color: UIImage's Color
    ///
    ///  - returns: UIImage
    class func withColor(color: UIColor = UIColor.whiteColor()) -> UIImage {
        
        let rect = CGRectMake(0, 0, 1, 1)
        UIGraphicsBeginImageContext(rect.size)
        let context = UIGraphicsGetCurrentContext()
        
        CGContextSetFillColorWithColor(context, color.CGColor)
        CGContextFillRect(context, rect)
        
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image
    }
}