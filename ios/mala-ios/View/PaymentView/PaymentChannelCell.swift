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
    var channel: MalaPaymentChannel = .Other
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
        titleLabel.font = UIFont.systemFontOfSize(14)
        titleLabel.textColor = MalaColor_333333_0
        return titleLabel
    }()
    /// 支付方式描述
    private lazy var subTitleLabel: UILabel = {
        let subTitleLabel = UILabel()
        subTitleLabel.font = UIFont.systemFontOfSize(13)
        subTitleLabel.textColor = MalaColor_6C6C6C_0
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
    /// 分割线
    lazy var separatorLine: UIView = {
        let separatorLine = UIView.line()
        separatorLine.backgroundColor = MalaColor_E5E5E5_0
        return separatorLine
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
        self.separatorInset = UIEdgeInsets(top: 0, left: 12, bottom: 0, right: 12)
        
        // SubViews
        contentView.addSubview(iconView)
        contentView.addSubview(titleLabel)
        contentView.addSubview(subTitleLabel)
        contentView.addSubview(selectButton)
        contentView.addSubview(separatorLine)
        
        // Autolayout
        iconView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(contentView.snp_top).offset(16)
            make.left.equalTo(contentView.snp_left).offset(12)
            make.bottom.equalTo(contentView.snp_bottom).offset(-16)
            make.width.equalTo(iconView.snp_height)
        }
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(14)
            make.top.equalTo(iconView.snp_top)
            make.left.equalTo(iconView.snp_right).offset(12)
        }
        subTitleLabel.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(titleLabel.snp_left)
            make.bottom.equalTo(iconView.snp_bottom)
            make.height.equalTo(13)
        }
        selectButton.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(contentView.snp_centerY)
            make.right.equalTo(contentView.snp_right).offset(-12)
        }
        separatorLine.snp_makeConstraints { (make) in
            make.bottom.equalTo(contentView.snp_bottom)
            make.left.equalTo(contentView.snp_left).offset(12)
            make.right.equalTo(contentView.snp_right).offset(-12)
            make.height.equalTo(MalaScreenOnePixel)
        }
    }
    
    func hideSeparator() {
        self.separatorLine.hidden = true
    }
}