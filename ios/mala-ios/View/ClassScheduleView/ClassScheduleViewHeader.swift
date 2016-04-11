//
//  ClassScheduleViewHeader.swift
//  mala-ios
//
//  Created by 王新宇 on 3/7/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class ClassScheduleViewHeader: ThemeCalendarViewHeader {
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        configure()
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    // MARK: - Private Method
    private func configure() {
        backgroundColor = UIColor.lightGrayColor()
        separatorColor = UIColor.clearColor()
        textFont = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        textColor = MalaColor_333333_0
        titleLabel.textAlignment = .Center
    }
    
    private func setupUserInterface() {
        
    }
}
