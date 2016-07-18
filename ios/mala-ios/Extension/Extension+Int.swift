//
//  Extension+Int.swift
//  mala-ios
//
//  Created by 王新宇 on 3/7/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import Foundation

extension Int {
    public var money: String {
        get {
            #if USE_PRD_SERVER
                return String(format: "%@", String(Int(self)/100))
            #else
                return String(format: "%@", String(Double(self)/100))
            #endif
        }
    }
    
    public var moneyCNY: String {
        get {
            if self == 0 {
                return "￥0.01"
            }
            return String(format: "￥%.2f", Double(self)/100)
        }
    }
    
    public var moneyInt: Int {
        get {
            return Int(Double(self)/100)
        }
    }
}