//
//  PaymentAmountCell.swift
//  mala-ios
//
//  Created by 王新宇 on 2/29/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class PaymentAmountCell: UITableViewCell {

    // MARK: - Property
    /// 金额
    private var amount: Int = 0 {
        didSet {
            amountLabel.text = String(format: "￥%.2f", Float(amount))
            self.amountLabel.sizeToFit()
        }
    }
    
    // MARK: - Components
    /// 应付金额label
    private lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_15)
        titleLabel.textColor = MalaDetailsCellTitleColor
        titleLabel.text = "应付金额"
        return titleLabel
    }()
    /// 金额标签
    private lazy var amountLabel: UILabel = {
        let amountLabel = UILabel()
        amountLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        amountLabel.textColor = MalaDetailsPriceRedColor
        amountLabel.text = "￥0.00"
        return amountLabel
    }()
    
    
    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func setSelected(selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)
        
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // SubViews
        contentView.addSubview(titleLabel)
        contentView.addSubview(amountLabel)
        
        // Autolayout
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(contentView.snp_top).offset(MalaLayout_Margin_16)
            make.left.equalTo(contentView.snp_left).offset(MalaLayout_Margin_12)
            make.bottom.equalTo(contentView.snp_bottom).offset(-MalaLayout_Margin_16)
        }
        amountLabel.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(contentView.snp_centerY)
            make.right.equalTo(contentView.snp_right).offset(-MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_15)
        }
        
        amount = MalaCourseChoosingObject.getAmount() ?? 0
    }
}
