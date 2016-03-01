//
//  PaymentChannel.swift
//  mala-ios
//
//  Created by 王新宇 on 2/29/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class PaymentChannel: NSObject {

    // MARK: - Property
    var imageName: String?
    var title: String?
    var subTitle: String?
    var channel: MalaPaymentChannel?
    
    
    // MARK: - Constructed
    override init() {
        super.init()
    }
    
    convenience init(imageName: String, title: String, subTitle: String, channel: MalaPaymentChannel) {
        self.init()
        self.imageName = imageName
        self.title = title
        self.subTitle = subTitle
        self.channel = channel
    }
    
    
    // MARK: - Description
    override var description: String {
        let keys = ["imageName", "title", "subTitle", "channel"]
        return dictionaryWithValuesForKeys(keys).description
    }
}