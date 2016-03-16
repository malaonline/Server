//
//  ClassScheduleViewCell.swift
//  mala-ios
//
//  Created by 王新宇 on 3/7/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class ClassScheduleViewCell: PDTSimpleCalendarViewCell {

    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        configure()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    // MARK: - Private Method
    private func configure() {
        circleTodayColor = UIColor.orangeColor()
        circleSelectedColor = MalaDetailsButtonBlueColor
    }
}
