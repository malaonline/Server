//
//  ThemeCalendarViewHeader.swift
//  mala-ios
//
//  Created by 王新宇 on 3/7/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let ThemeCalendarViewHeaderTextSize: CGFloat = 14.0

/// 用于显示年月日
public class ThemeCalendarViewHeader: UICollectionReusableView {
    
    // MARK: - Property
    /// 年份及月份Label
    var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.font = UIFont.systemFontOfSize(ThemeCalendarViewHeaderTextSize)
        titleLabel.textColor = MalaColor_939393_0
        titleLabel.backgroundColor = UIColor.clearColor()
        return titleLabel
    }()
    /// 文字颜色
    var textColor: UIColor = MalaColor_82B4D9_0 {
        didSet {
            titleLabel.textColor = textColor
        }
    }
    /// 文字字体
    var textFont: UIFont = UIFont.systemFontOfSize(ThemeCalendarViewHeaderTextSize) {
        didSet {
            titleLabel.font = textFont
        }
    }
    /// 月份和日期之间的分割线颜色
    var separatorColor: UIColor = UIColor.clearColor() {
        didSet {
            separatorView.backgroundColor = separatorColor
        }
    }
    /// 分割线
    private lazy var separatorView: UIView = {
        let separatorView = UIView()
        return separatorView
    }()
    
    
    // MARK: - Constructed
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
        self.titleLabel.font = self.textFont
        self.titleLabel.textColor = self.textColor
        self.separatorView.backgroundColor = self.separatorColor
        
        // SubViews
        addSubview(titleLabel)
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        addSubview(separatorView)
        separatorView.translatesAutoresizingMaskIntoConstraints = false
        
        
        // Autolayout
        let metricsDictionary: [String: AnyObject] = ["onePixel": MalaScreenOnePixel]
        let viewsDictionary: [String: AnyObject] = ["titleLabel": titleLabel, "separatorView": separatorView]
        
        self.addConstraints(
            NSLayoutConstraint.constraintsWithVisualFormat(
                "|-(==10)-[titleLabel]-(==10)-|",
                options: .DirectionLeadingToTrailing,
                metrics: nil,
                views: viewsDictionary
            )
        )
        self.addConstraints(
            NSLayoutConstraint.constraintsWithVisualFormat(
                "V:|[titleLabel]|",
                options: .DirectionLeadingToTrailing,
                metrics: nil,
                views: viewsDictionary
            )
        )
        
        self.addConstraints(
            NSLayoutConstraint.constraintsWithVisualFormat(
                "|[separatorView]|",
                options: .DirectionLeadingToTrailing,
                metrics: nil,
                views: viewsDictionary
            )
        )
        self.addConstraints(
            NSLayoutConstraint.constraintsWithVisualFormat(
                "V:[separatorView(==onePixel)]|",
                options: .DirectionLeadingToTrailing,
                metrics: metricsDictionary,
                views: viewsDictionary
            )
        )
    }
}