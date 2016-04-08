//
//  ThemeCalendarViewFlowLayout.swift
//  mala-ios
//
//  Created by 王新宇 on 3/7/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let ThemeCalendarViewFlowLayoutMinInterItemSpacing: CGFloat = 0.0
private let ThemeCalendarViewFlowLayoutMinLineSpacing: CGFloat = 0.0
private let ThemeCalendarViewFlowLayoutInsetTop: CGFloat = 5.0
private let ThemeCalendarViewFlowLayoutInsetLeft: CGFloat = 0.0
private let ThemeCalendarViewFlowLayoutInsetBottom: CGFloat = 5.0
private let ThemeCalendarViewFlowLayoutInsetRight: CGFloat = 0.0
private let ThemeCalendarViewFlowLayoutHeaderHeight: CGFloat = 32.0

public class ThemeCalendarViewFlowLayout: UICollectionViewFlowLayout {

    override init() {
        super.init()
        
        self.minimumInteritemSpacing = ThemeCalendarViewFlowLayoutMinInterItemSpacing
        self.minimumLineSpacing = ThemeCalendarViewFlowLayoutMinLineSpacing
        self.scrollDirection = .Vertical
        self.sectionInset = UIEdgeInsets(
            top: ThemeCalendarViewFlowLayoutInsetTop,
            left: ThemeCalendarViewFlowLayoutInsetLeft,
            bottom: ThemeCalendarViewFlowLayoutInsetBottom,
            right: ThemeCalendarViewFlowLayoutInsetRight
        )
        self.headerReferenceSize = CGSize(width: 0, height: ThemeCalendarViewFlowLayoutHeaderHeight)
        
        //Note: Item Size is defined using the delegate to take into account the width of the view and calculate size dynamically
    }

    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
