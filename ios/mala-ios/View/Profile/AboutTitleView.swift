//
//  AboutTitleView.swift
//  mala-ios
//
//  Created by 王新宇 on 3/15/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class AboutTitleView: UIView {
    
    // MARK: - Property
    /// 标题文字
    var title: String = "" {
        didSet {
            titleLabel.text = title
            titleLabel.sizeToFit()
        }
    }
    

    // MARK: - Components
    /// 标题
    private var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        titleLabel.textColor = MalaAppearanceTextColor
        return titleLabel
    }()
    /// 左侧装饰线
    private var leftLine: UIImageView = {
        let leftLine = UIImageView(image: UIImage(named: "titleLeftLine"))
        return leftLine
    }()
    /// 右侧装饰线
    private var rightLine: UIImageView = {
        let rightLine = UIImageView(image: UIImage(named: "titleRightLine"))
        return rightLine
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private
    private func setupUserInterface() {
        // SubViews
        addSubview(titleLabel)
        addSubview(leftLine)
        addSubview(rightLine)
        
        // Autolayout
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(self.snp_centerX)
            make.top.equalTo(self.snp_top)
            make.bottom.equalTo(self.snp_bottom)
            make.height.equalTo(MalaLayout_FontSize_14)
        }
        leftLine.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(titleLabel.snp_centerY)
            make.left.equalTo(self.snp_left).offset(MalaLayout_Margin_10)
            make.right.equalTo(titleLabel.snp_left).offset(-MalaLayout_Margin_5)
        }
        rightLine.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(titleLabel.snp_centerY)
            make.left.equalTo(titleLabel.snp_right).offset(MalaLayout_Margin_5)
            make.right.equalTo(self.snp_right).offset(-MalaLayout_Margin_10)
        }
    }
}