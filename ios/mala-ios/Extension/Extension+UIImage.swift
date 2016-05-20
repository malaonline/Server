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
    class func withColor(color: UIColor = UIColor.whiteColor(), bounds: CGRect = CGRectMake(0, 0, 1, 1)) -> UIImage {
        
        let rect = bounds
        UIGraphicsBeginImageContext(rect.size)
        let context = UIGraphicsGetCurrentContext()
        
        CGContextSetFillColorWithColor(context, color.CGColor)
        CGContextFillRect(context, rect)
        
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image
    }
    
    func largestCenteredSquareImage() -> UIImage {
        let scale = self.scale
        
        let originalWidth  = self.size.width * scale
        let originalHeight = self.size.height * scale
        
        let edge: CGFloat
        if originalWidth > originalHeight {
            edge = originalHeight
        } else {
            edge = originalWidth
        }
        
        let posX = (originalWidth  - edge) / 2.0
        let posY = (originalHeight - edge) / 2.0
        
        let cropSquare = CGRectMake(posX, posY, edge, edge)
        
        let imageRef = CGImageCreateWithImageInRect(self.CGImage, cropSquare)!
        
        return UIImage(CGImage: imageRef, scale: scale, orientation: self.imageOrientation)
    }
    
    func resizeToTargetSize(targetSize: CGSize) -> UIImage {
        let size = self.size
        
        let widthRatio  = targetSize.width  / self.size.width
        let heightRatio = targetSize.height / self.size.height
        
        let scale = UIScreen.mainScreen().scale
        let newSize: CGSize
        if(widthRatio > heightRatio) {
            newSize = CGSizeMake(scale * floor(size.width * heightRatio), scale * floor(size.height * heightRatio))
        } else {
            newSize = CGSizeMake(scale * floor(size.width * widthRatio), scale * floor(size.height * widthRatio))
        }
        
        let rect = CGRectMake(0, 0, floor(newSize.width), floor(newSize.height))
        
        //println("size: \(size), newSize: \(newSize), rect: \(rect)")
        
        UIGraphicsBeginImageContextWithOptions(newSize, false, 1.0)
        self.drawInRect(rect)
        let newImage = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        
        return newImage
    }
}