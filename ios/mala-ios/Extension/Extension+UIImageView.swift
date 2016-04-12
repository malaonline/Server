//
//  Extension+UIImageView.swift
//  mala-ios
//
//  Created by Elors on 1/7/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

extension UIImageView {
    
    ///  Convenience to Create a UIImageView that prepare to display image
    ///
    ///  - returns: UIImageView
    class func placeHolder() -> UIImageView {
        let placeHolder = UIImageView()
        placeHolder.contentMode = .ScaleAspectFill
        placeHolder.clipsToBounds = true
        return placeHolder
    }
}
