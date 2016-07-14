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
        // 使用图片绝对路径作为缓存键值
        let pureURL = URL.absoluteString.componentsSeparatedByString("?").first ?? URL.absoluteString
        let resource = Kingfisher.Resource(downloadURL: URL, cacheKey: pureURL)
        // 加载图片资源
        self.kf_setImageWithResource(
            resource,
            placeholderImage: placeholderImage,
            optionsInfo: [.Transition(.Fade(0.25)), .TargetCache(ImageCache(name: pureURL))],
            progressBlock: progressBlock,
            completionHandler: completionHandler
        )
    }
}
