//
//  ThemeCalendarViewWeekdayHeader.swift
//  mala-ios
//
//  Created by 王新宇 on 3/8/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let ThemeCalendarViewWeekdayHeaderSize: CGFloat = 12.0
private let ThemeCalendarViewWeekdayHeaderHeight: CGFloat = 20.0

public enum ThemeCalendarViewWeekdayTextType: Int {
    case VeryShort = 0
    case Short
    case StandAlone
}

public class ThemeCalendarViewWeekdayHeader: UIView {
    
    // MARK: - Property
    public var textColor: UIColor = UIColor.blackColor()
    public var textFont: UIFont = UIFont.systemFontOfSize(ThemeCalendarViewWeekdayHeaderSize)
    public var headerBackgroundColor: UIColor = UIColor.whiteColor()
    

    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        configure()
    }

    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Public Method
    convenience init(calendar: NSCalendar, textType:ThemeCalendarViewWeekdayTextType) {
        self.init()
        
        // 外观样式
        backgroundColor = headerBackgroundColor
        
        // 日期数据格式化组件
        let dateFormatter = NSDateFormatter()
        dateFormatter.calendar = calendar
        var weekdaySymbols: [String] = []
        
        ///  根据Type显示星期数
        switch textType {
        case .VeryShort:
            weekdaySymbols = dateFormatter.veryShortWeekdaySymbols
            break
        case .Short:
            weekdaySymbols = dateFormatter.shortWeekdaySymbols
            break
        case .StandAlone:
            weekdaySymbols = dateFormatter.standaloneWeekdaySymbols
            break
        }
        
        var adjustedSymbols = weekdaySymbols
        var index = 0
        repeat {
            let lastObject = adjustedSymbols.last ?? ""
            adjustedSymbols.removeLast()
            adjustedSymbols.insert(lastObject, atIndex: 0)
            index++
        }while index < (1 - calendar.firstWeekday + weekdaySymbols.count)
        
        if adjustedSymbols.count == 0 {
            return
        }
        
        var firstWeekdaySymbolLabel: UILabel?
        var weekdaySymbolLabelNameArray: [String] = []
        var weekdaySymbolLabelDict: NSDictionary = NSDictionary()
        index = 0
        repeat {
            let labelName = String(format: "weekdaySymbolLabel%d", index)
            weekdaySymbolLabelNameArray.append(labelName)
            
            let weekdaySymbolLabel = UILabel()
            weekdaySymbolLabel.font = textFont
            weekdaySymbolLabel.text = adjustedSymbols[index].uppercaseString
            weekdaySymbolLabel.textColor = textColor
            weekdaySymbolLabel.textAlignment = .Center
            weekdaySymbolLabel.backgroundColor = UIColor.clearColor()
            weekdaySymbolLabel.translatesAutoresizingMaskIntoConstraints = false
            self.addSubview(weekdaySymbolLabel)
            
            weekdaySymbolLabelDict.setValue(weekdaySymbolLabel, forKey: labelName)
            weekdaySymbolLabel.snp_makeConstraints { (make) -> Void in
                make.left.equalTo(self.snp_left)
                make.right.equalTo(self.snp_right)
                make.centerY.equalTo(self.snp_centerY)
            }
            
            if firstWeekdaySymbolLabel == nil {
                firstWeekdaySymbolLabel = weekdaySymbolLabel
            }else {
                weekdaySymbolLabel.snp_updateConstraints { (make) -> Void in
                    make.width.equalTo(firstWeekdaySymbolLabel!.snp_width)
                }
            }
            index++
        }while index < adjustedSymbols.count
  
        // Autolayout
        let layoutString = String(format: "|[%@(>=0)]|", weekdaySymbolLabelNameArray.joinWithSeparator("]["))
        self.addConstraints(NSLayoutConstraint.constraintsWithVisualFormat(layoutString,
            options: .AlignAllCenterY,
            metrics: nil,
            views: (weekdaySymbolLabelDict as! [String: AnyObject]))
        )
    }
    
    
    // MARK: - Private Method
    private func configure() {
        
        
        
    }
}