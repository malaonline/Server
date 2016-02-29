//
//  PaymentChannelCell.swift
//  mala-ios
//
//  Created by 王新宇 on 2/29/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class PaymentChannelCell: UITableViewCell {

    // MARK: - Property
    /// 支付方式
    var channel: MalaPaymentChannel = .Alipay
    /// 支付方式模型
    var model: PaymentChannel? {
        didSet {
            iconView.image = UIImage(named: (model?.imageName) ?? "")
            titleLabel.text = model?.title
            subTitleLabel.text = model?.subTitle
            channel = model?.channel ?? .Alipay
        }
    }
    override var selected: Bool {
        didSet {
            selectButton.selected = selected
        }
    }
    
    // MARK: - Components
    private lazy var iconView: UIImageView = {
        let iconView = UIImageView()
        return iconView
    }()
    /// 支付方式名称
    private lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        titleLabel.textColor = MalaDetailsCellTitleColor
        return titleLabel
    }()
    /// 支付方式描述
    private lazy var subTitleLabel: UILabel = {
        let subTitleLabel = UILabel()
        subTitleLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        subTitleLabel.textColor = MalaAppearanceTextColor
        return subTitleLabel
    }()
    /// 选择按钮
    private lazy var selectButton: UIButton = {
        let selectButton = UIButton()
        selectButton.setBackgroundImage(UIImage(named: "unselected"), forState: .Normal)
        selectButton.setBackgroundImage(UIImage(named: "selected"), forState: .Selected)
        // 冻结按钮交互功能，其只作为视觉显示效果使用
        selectButton.userInteractionEnabled = false
        return selectButton
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
        // Style
        
        // SubViews
        contentView.addSubview(iconView)
        contentView.addSubview(titleLabel)
        contentView.addSubview(subTitleLabel)
        contentView.addSubview(selectButton)
        
        // Autolayout
        iconView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(contentView.snp_top).offset(MalaLayout_Margin_16)
            make.left.equalTo(contentView.snp_left).offset(MalaLayout_Margin_12)
            make.bottom.equalTo(contentView.snp_bottom).offset(-MalaLayout_Margin_16)
            make.width.equalTo(iconView.snp_height)
        }
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(MalaLayout_FontSize_14)
            make.top.equalTo(iconView.snp_top)
            make.left.equalTo(iconView.snp_right).offset(MalaLayout_Margin_12)
        }
        subTitleLabel.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(titleLabel.snp_left)
            make.bottom.equalTo(iconView.snp_bottom)
            make.height.equalTo(MalaLayout_FontSize_13)
        }
        selectButton.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(contentView.snp_centerY)
            make.right.equalTo(contentView.snp_right).offset(-MalaLayout_Margin_12)
        }
    }
}