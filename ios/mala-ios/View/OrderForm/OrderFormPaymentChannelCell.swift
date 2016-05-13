//
//  OrderFormPaymentChannelCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/13.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class OrderFormPaymentChannelCell: UITableViewCell {

    // MARK: - Property
    /// 支付方式
    var channel: MalaPaymentChannel = .Other {
        didSet {
            switch channel {
            case .Alipay:
                payChannelLabel.text = "支付宝"
                break
            case .Wechat:
                payChannelLabel.text = "微信"
                break
            case .Other:
                payChannelLabel.text = "其他支付方式"
                break
            }
        }
    }
    
    
    // MARK: - Components
    /// cell标题
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "支付方式",
            fontSize: MalaLayout_FontSize_15,
            textColor: MalaColor_333333_0
        )
        return label
    }()
    /// 支付方式
    private lazy var payChannelLabel: UILabel = {
        let label = UILabel(
            text: "其他支付方式",
            fontSize: MalaLayout_FontSize_13,
            textColor: MalaColor_6C6C6C_0
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
        contentView.addSubview(titleLabel)
        contentView.addSubview(payChannelLabel)
        
        // Autolayout
        titleLabel.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_16)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_15)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_16)
        }
        payChannelLabel.snp_updateConstraints { (make) -> Void in
            make.centerY.equalTo(titleLabel.snp_centerY)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_13)
        }
    }
}