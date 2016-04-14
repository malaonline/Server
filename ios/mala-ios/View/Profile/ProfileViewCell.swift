//
//  ProfileViewCell.swift
//  mala-ios
//
//  Created by 王新宇 on 3/10/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class ProfileViewCell: UITableViewCell {

    // MARK: - Property
    /// [个人中心]Cell数据模型
    var model: ProfileElementModel = ProfileElementModel() {
        didSet {
            self.titleLabel.text = model.title
            self.infoLabel.text = model.detail
        }
    }
    
    
    // MARK: - Components
    /// 标题label
    private lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        titleLabel.textColor = MalaColor_636363_0
        return titleLabel
    }()
    /// 信息label
    private lazy var infoLabel: UILabel = {
        let infoLabel = UILabel()
        infoLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        infoLabel.textColor = MalaColor_D4D4D4_0
        return infoLabel
    }()
    /// 分割线
    lazy var separatorLine: UIView = {
        let separatorLine = UIView.line()
        separatorLine.backgroundColor = MalaColor_C7C7CC_0
        return separatorLine
    }()
    

    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: .Value1, reuseIdentifier: reuseIdentifier)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    // MARK: - Private Metho
    private func setupUserInterface() {
        // Style
        self.accessoryType = .DisclosureIndicator
        self.selectionStyle = .None
        self.separatorInset = UIEdgeInsets(top: 0, left: 12, bottom: 0, right: 12)
        
        // SubViews
        contentView.addSubview(titleLabel)
        contentView.addSubview(infoLabel)
        contentView.addSubview(separatorLine)
        
        // Autolayout
        titleLabel.snp_makeConstraints { (make) in
            make.height.equalTo(MalaLayout_FontSize_14)
            make.centerY.equalTo(contentView.snp_centerY)
            make.left.equalTo(contentView.snp_left).offset(MalaLayout_Margin_13)
        }
        infoLabel.snp_makeConstraints { (make) in
            make.height.equalTo(MalaLayout_FontSize_13)
            make.centerY.equalTo(contentView.snp_centerY)
            make.right.equalTo(contentView.snp_right)
        }
        separatorLine.snp_makeConstraints { (make) in
            make.bottom.equalTo(contentView.snp_bottom)
            make.left.equalTo(contentView.snp_left).offset(MalaLayout_Margin_12)
            make.right.equalTo(contentView.snp_right).offset(MalaLayout_Margin_12)
            make.height.equalTo(MalaScreenOnePixel)
        }
    }
    
    func hideSeparator() {
        self.separatorLine.hidden = true
    }
}