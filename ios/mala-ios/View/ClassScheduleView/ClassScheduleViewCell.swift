//
//  ClassScheduleViewCell.swift
//  mala-ios
//
//  Created by 王新宇 on 3/7/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

@objc public protocol ClassScheduleViewCellDelegate: NSObjectProtocol {
    ///  是否使用自定义颜色
    optional func classScheduleViewCell(cell: ClassScheduleViewCell, shouldUseCustomColorsForDate date: NSDate) -> Bool
    ///  根据Date对象返回圆形颜色
    optional func classScheduleViewCell(cell: ClassScheduleViewCell, circleColorForDate date: NSDate) -> UIColor?
    ///  根据Date对象返回文字颜色
    optional func classScheduleViewCell(cell: ClassScheduleViewCell, textColorForDate date: NSDate) -> UIColor?
}


public class ClassScheduleViewCell: UICollectionViewCell {

    /// 圆心直径
    private let ClassScheduleViewCellCircleSize: CGFloat = 30.0
    
    // MARK: - Property
    /// 代理
    public weak var delegate: ClassScheduleViewCellDelegate?
    /// 是否为今天标记
    public var isToday: Bool = false {
        didSet {
            setCircleColor(isToday: isToday, selected: self.selected)
        }
    }
    /// cell选择标记
    override public var selected: Bool {
        didSet {
            setCircleColor(isToday: self.isToday, selected: selected)
        }
    }
    /// Date对象
    public var date: NSDate?
    
    /// 图形默认日期颜色
    public var circleDefaultColor: UIColor = MalaColor_FFFFFF_9
    /// 图形日期为今天时的颜色
    public var circleTodayColor: UIColor = UIColor.orangeColor()
    /// cell被选中时的图形颜色
    public var circleSelectedColor: UIColor = MalaColor_82B4D9_0
    /// 文字默认颜色
    public var textDefaultColor: UIColor = MalaColor_333333_0
    /// 日期为今天时的文字颜色
    public var textTodayColor: UIColor = MalaColor_82B4D9_0
    /// cell被选中时的文字颜色
    public var textSelectedColor: UIColor = MalaColor_FFFFFF_9
    /// cell被冻结时的文字颜色
    public var textDisabledColor: UIColor = MalaColor_333333_0
    /// 文字默认字体
    public var textDefaultFont: UIFont = UIFont.systemFontOfSize(15)
    /// 分隔线颜色
    var separatorLineColor: UIColor = MalaColor_E5E5E5_0 {
        didSet {
            separatorLine.backgroundColor = separatorLineColor
        }
    }
    /// 日期格式化组件
    static let dateFormatter: NSDateFormatter = {
        let dateFormatter = NSDateFormatter()
        dateFormatter.dateFormat = "d"
        return dateFormatter
    }()
    /// 辅助性日期格式化组件
    static let accessibilityDateFormatter: NSDateFormatter = {
        let dateFormatter = NSDateFormatter()
        dateFormatter.dateStyle = .LongStyle
        dateFormatter.timeStyle = .NoStyle
        return dateFormatter
    }()

    // MARK: - Components
    /// 日期文字Label
    private lazy var dayLabel: UILabel = {
        let dayLabel = UILabel()
        dayLabel.font = self.textDefaultFont
        dayLabel.textAlignment = .Center
        return dayLabel
    }()
    /// 分隔线
    private lazy var separatorLine: UIView = {
        let separatorLine = UIView()
        separatorLine.backgroundColor = MalaColor_E5E5E5_0
        return separatorLine
    }()

    
    // MARK: - Instance Method
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        configure()
        setupUserInterface()
    }

    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    // MARK: - Private Method
    ///  配置Cel
    private func configure() {
        
    }
    
    ///  设置UI
    private func setupUserInterface() {
        // Style 
        dayLabel.translatesAutoresizingMaskIntoConstraints = false
        dayLabel.backgroundColor = UIColor.clearColor()
        dayLabel.layer.cornerRadius = ClassScheduleViewCellCircleSize/2
        dayLabel.layer.masksToBounds = true
        
        // SubViews
        contentView.addSubview(dayLabel)
        contentView.addSubview(separatorLine)
        
        // Autolayout
        dayLabel.snp_makeConstraints { (make) -> Void in
            make.center.equalTo(contentView.snp_center)
            make.height.equalTo(ClassScheduleViewCellCircleSize)
            make.width.equalTo(ClassScheduleViewCellCircleSize)
        }
        separatorLine.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(contentView.snp_top)
            make.centerX.equalTo(contentView.snp_centerX)
            make.width.equalTo(contentView.snp_width).offset(2)
            make.height.equalTo(MalaScreenOnePixel)
        }
        
        setCircleColor(isToday: false, selected: false)
    }
    
    ///  设置圆形颜色
    ///
    ///  - parameter today:    是否为今天
    ///  - parameter selected: 是否选中
    private func setCircleColor(isToday today: Bool, selected: Bool) {
        
        var circleColor = today ? circleTodayColor : circleDefaultColor
        var labelColor = today ? textTodayColor : textDefaultColor
        
        if (self.date != nil) && (delegate?.classScheduleViewCell?(self, shouldUseCustomColorsForDate: self.date!) == true) {
            if let textColor = delegate?.classScheduleViewCell?(self, textColorForDate: self.date!) {
                labelColor = textColor
            }
            if let shapeColor = delegate?.classScheduleViewCell?(self, circleColorForDate: self.date!) {
                circleColor = shapeColor
            }
        }
        
        if today {
            setTodayUI()
            labelColor = textTodayColor
        }
        
        if selected {
            circleColor = circleSelectedColor
            labelColor = textSelectedColor
        }
        
        self.dayLabel.backgroundColor = circleColor
        self.dayLabel.textColor = labelColor
    }
    
    ///  设置Cell样式为今天
    private func setTodayUI() {
        self.dayLabel.layer.borderColor = textTodayColor.CGColor
        self.dayLabel.layer.borderWidth = MalaScreenOnePixel
        self.dayLabel.textColor = textTodayColor
    }
    
    
    // MARK: - Class Method
    class func formatDate(date: NSDate, withCalendar calendar: NSCalendar) -> String {
        let dateFormatter = self.dateFormatter
        return ClassScheduleViewCell.stringFromDate(date, withDateFormatter: dateFormatter, withCalendar: calendar)
    }
    
    class func formatAccessibilityDate(date: NSDate, withCalendar calendar: NSCalendar) -> String {
        let dateFormatter = self.accessibilityDateFormatter
        return ClassScheduleViewCell.stringFromDate(date, withDateFormatter: dateFormatter, withCalendar: calendar)
    }
    
    
    class func stringFromDate(date: NSDate, withDateFormatter dateFormatter: NSDateFormatter, withCalendar calendar: NSCalendar) -> String {
        if !dateFormatter.calendar.isEqual(calendar) {
            dateFormatter.calendar = calendar
        }
        return dateFormatter.stringFromDate(date)
    }
    
    
    // MARK: - Public Method
    ///  设置日期
    ///
    ///  - parameter date:      日期对象
    ///  - parameter calendar:  日历对象
    public func setDate(date date: NSDate?, calendar: NSCalendar?) {
        var day = ""
        var accessibilityDay = ""
        
        if date != nil && calendar != nil {
            self.date = date
            day = ClassScheduleViewCell.formatDate(date!, withCalendar: calendar!)
            accessibilityDay = ClassScheduleViewCell.formatAccessibilityDate(date!, withCalendar: calendar!)
        }
        
        dayLabel.text = day
        dayLabel.accessibilityLabel = accessibilityDay
        
        ///  若未显示日期文字，则隐藏分割线
        separatorLine.hidden = (dayLabel.text == "")
    }
    
    ///  刷新颜色
    public func refreshCellColors() {
        setCircleColor(isToday: self.isToday, selected: self.selected)
    }
        
    ///  Cell重用准备
    override public func prepareForReuse() {
        super.prepareForReuse()
        date = nil
        isToday = false
        dayLabel.text = ""
        dayLabel.backgroundColor = circleDefaultColor
        dayLabel.textColor = textDefaultColor
        dayLabel.layer.borderColor = UIColor.clearColor().CGColor
        dayLabel.layer.borderWidth = 0
    }
}