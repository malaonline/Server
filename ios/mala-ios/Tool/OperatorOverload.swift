//
//  OperatorOverload.swift
//  mala-ios
//
//  Created by Elors on 12/23/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

///  Compare Tuple With NSIndexPath 
///
///  - parameter FirIndexPath: NSIndexPath Object (like {section:0, row:0})
///  - parameter SecIndexPath: Tuple (like (0,0))
///
///  - returns: Bool
func ==(FirIndexPath: NSIndexPath, SecIndexPath: (section: Int, row: Int)) -> Bool {
    return FirIndexPath.section == SecIndexPath.section && FirIndexPath.row == SecIndexPath.row
}

///  Random Number
///
///  - parameter range: range
///
///  - returns: Int
func randomInRange(range: Range<Int>) -> Int {
    let count = UInt32(range.endIndex - range.startIndex)
    return  Int(arc4random_uniform(count)) + range.startIndex
}

func ==<T>(lhs: Listener<T>, rhs: Listener<T>) -> Bool {
    return lhs.name == rhs.name
}