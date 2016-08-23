//
//  IntroductionModel.swift
//  mala-ios
//
//  Created by 王新宇 on 2/18/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class IntroductionModel: NSObject {

    // MARK: - Property
    var title: String?
    var image: String?
    var subTitle: String?
    var sharePlatformType: SSDKPlatformType = SSDKPlatformType.TypeAny
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    init(dict: [String: AnyObject]) {
        super.init()
        setValuesForKeysWithDictionary(dict)
    }
    
    convenience init(title: String?, image: String?, subTitle: String?, platformType: SSDKPlatformType = .TypeAny) {
        self.init()
        self.title = title
        self.image = image
        self.subTitle = subTitle
        self.sharePlatformType = platformType
    }
}
