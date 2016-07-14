//
//  Extension+UIImageView.swift
//  mala-ios
//
//  Created by Elors on 1/7/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit
import Kingfisher

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
    
    
    func ma_setImage(URL: NSURL, placeholderImage: Image? = nil, progressBlock: DownloadProgressBlock? = nil, completionHandler: CompletionHandler? = nil) {
        
        self.kf_setImageWithURL(
            URL,
            placeholderImage: placeholderImage,
            optionsInfo: [.Transition(.Fade(0.25)), .TargetCache(ImageCache(name: URL.absoluteString.componentsSeparatedByString("?").first ?? URL.absoluteString))],
            progressBlock: progressBlock,
            completionHandler: completionHandler
        )
    }
}
