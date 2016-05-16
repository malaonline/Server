//
//  OrderFormOtherInfoCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/13.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class OrderFormOtherInfoCell: UITableViewCell {

    // MARK: - Property
    /// 订单编号
    var orderId: String = "" {
        didSet {
            
        }
    }
    /// 订单创建时间
    var createDate: NSTimeInterval? {
        didSet {
            
        }
    }
    /// 订单支付时间
    var paymentDate: NSTimeInterval? {
        didSet {
            
        }
    }
    
    
    // MARK: - Components
    /// 订单编号
    private lazy var titleString: UILabel = {
        let label = UILabel(
            text: "订单编号：",
            fontSize: MalaLayout_FontSize_12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "0000000001",
            fontSize: MalaLayout_FontSize_12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    /// 创建时间
    private lazy var createDateString: UILabel = {
        let label = UILabel(
            text: "创建时间：",
            fontSize: MalaLayout_FontSize_12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    private lazy var createDateLabel: UILabel = {
        let label = UILabel(
            text: "0000000002",
            fontSize: MalaLayout_FontSize_12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    /// 支付时间
    private lazy var paymentDateString: UILabel = {
        let label = UILabel(
            text: "支付时间：",
            fontSize: MalaLayout_FontSize_12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    private lazy var paymentDateLabel: UILabel = {
        let label = UILabel(
            text: "0000000003",
            fontSize: MalaLayout_FontSize_12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    
    
    // MARK: - Contructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        
        // SubViews
        contentView.addSubview(titleString)
        contentView.addSubview(titleLabel)
        contentView.addSubview(createDateString)
        contentView.addSubview(createDateLabel)
        contentView.addSubview(paymentDateString)
        contentView.addSubview(paymentDateLabel)
        
        // Autolayout
        titleString.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_16)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_12)
        }
        titleLabel.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(titleString.snp_top)
            make.left.equalTo(titleString.snp_right).offset(MalaLayout_Margin_10)
            make.height.equalTo(MalaLayout_FontSize_12)
        }
        createDateString.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(titleString.snp_bottom).offset(MalaLayout_Margin_10)
            make.left.equalTo(titleString.snp_left)
            make.height.equalTo(MalaLayout_FontSize_12)
        }
        createDateLabel.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(createDateString.snp_top)
            make.left.equalTo(createDateString.snp_right).offset(MalaLayout_Margin_10)
            make.height.equalTo(MalaLayout_FontSize_12)
        }
        paymentDateString.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(createDateString.snp_bottom).offset(MalaLayout_Margin_10)
            make.left.equalTo(titleString.snp_left)
            make.height.equalTo(MalaLayout_FontSize_12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_16)
        }
        paymentDateLabel.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(paymentDateString.snp_top)
            make.left.equalTo(paymentDateString.snp_right).offset(MalaLayout_Margin_10)
            make.height.equalTo(MalaLayout_FontSize_12)
        }
    }
}