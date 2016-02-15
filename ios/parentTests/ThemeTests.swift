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
    
    func testDateArray() {
        let array = [
            ClassScheduleDayModel(id: 5, start: "08:00", end: "10:00", available: true),
            ClassScheduleDayModel(id: 5, start: "10:30", end: "12:30", available: true),
            ClassScheduleDayModel(id: 6, start: "08:00", end: "10:00", available: true),
            ClassScheduleDayModel(id: 6, start: "10:30", end: "12:30", available: true),
            ClassScheduleDayModel(id: 6, start: "15:30", end: "17:30", available: true),
        ]
        
        let resultArray = ThemeDate.dateArray(array, Period: 2)
        print(resultArray)
    }
}