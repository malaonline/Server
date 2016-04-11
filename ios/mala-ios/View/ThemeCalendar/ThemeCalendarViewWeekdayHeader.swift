//
//  ThemeCalendarViewWeekdayHeader.swift
//  mala-ios
//
//  Created by 王新宇 on 3/8/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

// 文字高度
internal let ThemeCalendarViewWeekdayHeaderSize: CGFloat = 14.0
// 视图高度
internal let ThemeCalendarViewWeekdayHeaderHeight: CGFloat = 42.0


public enum ThemeCalendarViewWeekdayTextType: Int {
    case VeryShort = 0
    case Short
    case StandAlone
}


public class ThemeCalendarViewWeekdayHeader: UIView {
    
    // MARK: - Property
    /// 自定义文字颜色
    public var textColor: UIColor = MalaColor_333333_0
    /// 自定义文字字体
    public var textFont: UIFont = UIFont.systemFontOfSize(ThemeCalendarViewWeekdayHeaderSize)
    /// 背景颜色
    public var headerBackgroundColor: UIColor = MalaColor_F2F2F2_0
    

    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
    }

    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Public Method
    init(calendar: NSCalendar, textType:ThemeCalendarViewWeekdayTextType) {
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
        default:
            weekdaySymbols = dateFormatter.standaloneWeekdaySymbols
            break
        }
        
        var adjustedSymbols = weekdaySymbols
        var index = 0
        repeat {
            let lastObject = adjustedSymbols.last ?? ""
            adjustedSymbols.removeLast()
            adjustedSymbols.insert(lastObject, atIndex: 0)
            index += 1
        }while index < (1 - calendar.firstWeekday + weekdaySymbols.count)
        
        
        if adjustedSymbols.count == 0 {
            return
        }
        
        var firstWeekdaySymbolLabel: UILabel?
        var weekdaySymbolLabelNameArray: [String] = []
        var weekdaySymbolLabelDict: [String: AnyObject] = [String: AnyObject]()
        
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
            
            weekdaySymbolLabelDict[labelName] = weekdaySymbolLabel
            
            self.addConstraints(
                NSLayoutConstraint.constraintsWithVisualFormat(
                    /*String("V:|[%@]|", labelName)*/"V:|[label]|",
                    options: NSLayoutFormatOptions(rawValue: 0),
                    metrics: nil,
                    views: ["label": weekdaySymbolLabel]
                )
            )
            
            if firstWeekdaySymbolLabel == nil {
                firstWeekdaySymbolLabel = weekdaySymbolLabel
            }else {
                self.addConstraint(
                    NSLayoutConstraint(
                        item: weekdaySymbolLabel,
                        attribute: .Width,
                        relatedBy: .Equal,
                        toItem: firstWeekdaySymbolLabel,
                        attribute: .Width,
                        multiplier: 1,
                        constant: 0
                    )
                )
            }
            index += 1
        }while index < adjustedSymbols.count
  
        // Autolayout
        let layoutString = String(format: "|[%@(>=0)]|", weekdaySymbolLabelNameArray.joinWithSeparator("]["))
        self.addConstraints(NSLayoutConstraint.constraintsWithVisualFormat(layoutString,
            options: .AlignAllCenterY,
            metrics: nil,
            views: (weekdaySymbolLabelDict))
        )
    }
}