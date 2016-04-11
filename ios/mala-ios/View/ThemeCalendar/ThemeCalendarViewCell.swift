//
//  ThemeCalendarViewCell.swift
//  ThemeCalendar
//
//  Created by 王新宇 on 3/7/16.
//  Copyright © 2016 Elors. All rights reserved.
//

import UIKit
import SnapKit

/// 日历圆形单位(天)直径
internal let calendarCircleSiz: CGFloat = 32.0

public protocol ThemeCalendarViewCellDelegate: NSObjectProtocol {

    ///  通知代理判断是否使用自定义颜色渲染Cell
    ///
    ///  - parameter cell: 当前Cell
    ///  - parameter date: cell关联的日期
    ///
    ///  - returns: false则使用缺省颜色
    func cellShouldUseCustomColorsForDate(cell: ThemeCalendarViewCell, date: NSDate) -> Bool
    
    func cellTextColorForDate(cell: ThemeCalendarViewCell, date: NSDate?) -> UIColor
    
    func cellCircleColorForDate(cell: ThemeCalendarViewCell, date: NSDate?) -> UIColor
}


/// 日历中显示一天
public class ThemeCalendarViewCell: UICollectionViewCell {
    
    // MARK: - Property
    /// 代理对象
    public weak var delegate: ThemeCalendarViewCellDelegate?
    /// 是否为今天标识
    public var isToday: Bool = false {
        didSet {
            self.setCircleColor(isToday, selected: self.selected)
        }
    }
    /// 重写父类Selected属性
    override public var selected: Bool {
        didSet {
            self.setCircleColor(self.isToday, selected: selected)
        }
    }
    /// 圆形区域默认颜色
    public var circleDefaultColor: UIColor? = UIColor.whiteColor()
    /// 关联日期为今天时的圆形区域显示颜色
    public var circleTodayColor: UIColor? = UIColor.whiteColor()
    /// 圆形区域选中样式颜色
    public var circleSelectedColor: UIColor? = UIColor.orangeColor()
    /// 日期文字默认颜色
    public var textDefaultColor: UIColor? = UIColor.blackColor()
    /// 关联日期为今天时的日期文字颜色
    public var textTodayColor: UIColor? = MalaColor_82B4D9_0
    /// 日期文字选中颜色
    public var textSelectedColor: UIColor? = UIColor.whiteColor()
    /// 日期文字取消选中颜色
    public var textDisabledColor: UIColor? = UIColor.whiteColor()
    /// 日期label控件
    internal var dayLabel: UILabel = {
        let dayLabel = UILabel()
        dayLabel.font = UIFont.systemFontOfSize(14) //textDefaultFont
        dayLabel.textAlignment = .Center
        return dayLabel
    }()
    /// 当前Cell关联日期
    internal var date: NSDate?
    /// 日期文字默认字体
    internal lazy var textDefaultFont: UIFont = {
        let textDefaultFont = UIFont.systemFontOfSize(14)
        return textDefaultFont
    }()
    /// 日期格式化组件 - 此处仅需显示日
    static let dateFormatter: NSDateFormatter = {
        let dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "d"
        return dateFormatter
    }()
    /// 日期格式化组件
    static let accessibilityDateFormatter: NSDateFormatter = {
        let accessibilityDateFormatter = NSDateFormatter()
        accessibilityDateFormatter.dateStyle = .LongStyle
        accessibilityDateFormatter.timeStyle = .NoStyle
        return accessibilityDateFormatter
    }()
    
    
    // MARK: - Class Method
    class func formatDateWithCalendar(date: NSDate, calendar: NSCalendar?) -> String {
        let dateFormatter = self.dateFormatter
        return ThemeCalendarViewCell.StringFromDate(date, formatter: dateFormatter, calendar: calendar)
    }
    
    class func formatAccessibilityDate(date: NSDate, calendar: NSCalendar) -> String {
        let dateFormatter = self.accessibilityDateFormatter
        return ThemeCalendarViewCell.StringFromDate(date, formatter: dateFormatter, calendar: calendar)
    }
    
    class func StringFromDate(date: NSDate, formatter: NSDateFormatter, calendar: NSCalendar?) -> String {
        if formatter.calendar == calendar {
            formatter.calendar = calendar
        }
        return formatter.stringFromDate(date)
    }
    

    
    // MARK: - Instance Method
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupUserInterface()
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        dayLabel.translatesAutoresizingMaskIntoConstraints = false
        dayLabel.backgroundColor = UIColor.clearColor()
        dayLabel.layer.cornerRadius = calendarCircleSiz/2
        dayLabel.layer.masksToBounds = true
        
        // SubViews
        contentView.addSubview(dayLabel)
        
        // Autolayout
        dayLabel.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(calendarCircleSiz)
            make.width.equalTo(calendarCircleSiz)
            make.centerX.equalTo(self.contentView.snp_centerX)
            make.centerY.equalTo(self.contentView.snp_centerY)
        }
        
        self.setCircleColor(false, selected: false)
    }
    
    
    // MARK: - Internal Method
    internal func setCircleColor(today: Bool, selected: Bool) {
        var circleColor = today ? self.circleTodayColor : self.circleDefaultColor
        var labelColor = today ? self.textTodayColor : self.textDefaultColor
        
        if date != nil {
            labelColor = self.delegate?.cellTextColorForDate(self, date: self.date)
            circleColor = self.delegate?.cellCircleColorForDate(self, date: self.date)
        }
        
        if selected {
            circleColor = self.circleSelectedColor
            labelColor = self.textSelectedColor
        }
        
        dayLabel.backgroundColor = circleColor
        dayLabel.textColor = labelColor
    }
    
    
    // MARK: - Public Method
    public func setDate(date: NSDate?, calendar: NSCalendar?) {
        var day = ""
        var accessibilityDay = ""
        if (date != nil) && (calendar != nil) {
            self.date = date
            day = ThemeCalendarViewCell.formatDateWithCalendar(date!, calendar: calendar!)
            accessibilityDay = ThemeCalendarViewCell.formatAccessibilityDate(date!, calendar: calendar!)
        }
        dayLabel.text = day
        dayLabel.accessibilityLabel = accessibilityDay
    }
    
    public func refreshCellColors() {
        self.setCircleColor(self.isToday, selected: self.selected)
    }
}
