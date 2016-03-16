//
//  ClassScheduleViewCell.swift
//  mala-ios
//
//  Created by 王新宇 on 3/7/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class ClassScheduleViewCell: PDTSimpleCalendarViewCell {

    // MARK: - Property
    /// 分隔线颜色
    var separatorLineColor: UIColor = MalaColor_E5E5E5_0 {
        didSet {
            separatorLine.backgroundColor = separatorLineColor
        }
    }
    
    
    // MARK: - Components
    /// 分隔线
    private lazy var separatorLine: UIView = {
        let separatorLine = UIView()
        separatorLine.backgroundColor = MalaColor_E5E5E5_0
        return separatorLine
    }()

    
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
        circleTodayColor = UIColor.orangeColor()
        circleSelectedColor = MalaColor_82B4D9_0
    }
    
    private func setupUserInterface() {
        // Style 
        
        
        // SubViews
        contentView.addSubview(separatorLine)
        
        // Autolayout
        separatorLine.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(contentView.snp_top)
            make.centerX.equalTo(contentView.snp_centerX)
            make.width.equalTo(contentView.snp_width).offset(2)
            make.height.equalTo(MalaScreenOnePixel)
        }
        
    }
    
    private func hideSeparatorLine(hide: Bool) {
        separatorLine.hidden = hide
    }
    
    
    // MARK: - Override
    override func setDate(date: NSDate!, calendar: NSCalendar!) {
        super.setDate(date, calendar: calendar)
        
        hideSeparatorLine(dayLabel.text == "")
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        separatorLine.removeFromSuperview()
    }
}