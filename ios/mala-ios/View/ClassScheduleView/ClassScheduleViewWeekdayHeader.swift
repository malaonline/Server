//
//  ClassScheduleViewWeekdayHeader.swift
//  mala-ios
//
//  Created by 王新宇 on 3/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit
import PDTSimpleCalendar

class ClassScheduleViewWeekdayHeader: PDTSimpleCalendarViewWeekdayHeader {

    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUserInterface()
    }
    
    override init(calendar: NSCalendar, weekdayTextType: PDTSimpleCalendarViewWeekdayTextType) {
        super.init(calendar: calendar, weekdayTextType: weekdayTextType)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private
    private func setupUserInterface() {
        
    }

}
