//
//  CourseChoosingConfirmView.swift
//  mala-ios
//
//  Created by 王新宇 on 1/22/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CourseChoosingConfirmView: UIView {

    // MARK: - Property
    
    
    // MARK: - Components
    private lazy var topLine: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.blackColor()
        view.alpha = 0.4
        return view
    }()
    /// 价格说明标签
    private lazy var stringLabel: UILabel = {
        let stringLabel = UILabel()
        stringLabel.textColor = MalaDetailsCellTitleColor
        stringLabel.text = "还需支付:"
        return stringLabel
    }()
    /// 金额标签
    private lazy var priceLabel: UILabel = {
        let priceLabel = UILabel()
        priceLabel.textColor = UIColor.redColor()
        priceLabel.text = "￥0.00"
        return priceLabel
    }()
    /// 确定按钮
    private lazy var confirmButton: UIButton = {
        let confirmButton = UIButton()
        confirmButton.backgroundColor = MalaTeacherCellLevelColor
        confirmButton.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_16)
        confirmButton.setTitle("确定", forState: .Normal)
        confirmButton.setTitleColor(UIColor.whiteColor(), forState: .Normal)
        confirmButton.layer.cornerRadius = 5
        confirmButton.layer.masksToBounds = true
        return confirmButton
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUserInterface()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private method
    private func setupUserInterface() {
        // Style
        self.backgroundColor = UIColor.whiteColor()
        
        // SubViews
        addSubview(topLine)
        addSubview(stringLabel)
        addSubview(priceLabel)
        addSubview(confirmButton)
        
        // Autolayout
        topLine.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.snp_top)
            make.left.equalTo(self.snp_left)
            make.right.equalTo(self.snp_right)
            make.height.equalTo(MalaScreenOnePixel)
        })
        stringLabel.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.snp_left).offset(MalaLayout_Margin_12)
            make.centerY.equalTo(self.snp_centerY)
            make.height.equalTo(MalaLayout_FontSize_14)
        }
        priceLabel.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(stringLabel.snp_right)
            make.centerY.equalTo(self.snp_centerY)
            make.height.equalTo(MalaLayout_FontSize_14)
        }
        confirmButton.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(self.snp_right).offset(-MalaLayout_Margin_12)
            make.centerY.equalTo(self.snp_centerY)
            make.width.equalTo(144)
            make.height.equalTo(37)
        }
    }
}
