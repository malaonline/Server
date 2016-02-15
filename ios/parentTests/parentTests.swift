//
//  parentTests.swift
//  parentTests
//
//  Created by 王新宇 on 1/28/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import XCTest

class ThemeAppDelegate: NSObject, UIApplicationDelegate {

}

class parentTests: XCTestCase {
    
    override func setUp() {
        super.setUp()
            UIApplication.sharedApplication().delegate = ThemeAppDelegate()
    }
}
