//
//  ThemeTests.swift
//  mala-ios
//
//  Created by 王新宇 on 1/28/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit
import XCTest

class ThemeTests: parentTests {
    
    func testExtensionString() {
        let string = "周日"
        let date = string.dateInThisWeek()
        XCTAssert(!(1<=date.weekday() && date.weekday()<=7), "'dateInThisWeek' method should in 1...7")
    }
}
