//
//  ClassScheduleViewWeekdayHeader.swift
//  mala-ios
//
//  Created by 王新宇 on 3/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class ClassScheduleViewWeekdayHeader: ThemeCalendarViewWeekdayHeader {

    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUserInterface()
    }

    override init(calendar: NSCalendar, textType:ThemeCalendarViewWeekdayTextType) {
        super.init(calendar: calendar, textType: textType)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private
    private func setupUserInterface() {
        // Style
//        textFont = UIFont.systemFontOfSize(14)
//        textColor = MalaDetailsCellTitleColor
//        headerBackgroundColor = MalaProfileBackgroundColor
        
        // SubViews
        
        
        // Autolayout
        
        
    }
}
