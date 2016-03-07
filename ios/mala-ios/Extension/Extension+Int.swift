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
            return String(format: "%@", String(Double(self)/100))
        }
    }
}