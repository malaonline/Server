//
//  ThemeCalendarViewHeader.swift
//  mala-ios
//
//  Created by 王新宇 on 3/7/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let ThemeCalendarViewHeaderTextSize: CGFloat = 12.0

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
    var textColor: UIColor = MalaColor_939393_0
    /// 文字字体
    var textFont: UIFont = UIFont.systemFontOfSize(ThemeCalendarViewHeaderTextSize)
    /// 月份和日期之间的分割线颜色
    var separatorColor: UIColor = UIColor.lightGrayColor()
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
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.snp_left).offset(10)
            make.right.equalTo(self.snp_right).offset(10)
            make.centerY.equalTo(self.snp_centerY)
        }
        separatorView.snp_makeConstraints { (make) in
            make.left.equalTo(self.snp_left)
            make.right.equalTo(self.snp_right)
            make.bottom.equalTo(self.snp_bottom)
            make.height.equalTo(MalaScreenOnePixel)
        }
    }
}