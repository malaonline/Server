//
//  MalaBaseReportCardCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/23.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class MalaBaseReportCardCell: MalaBaseCardCell {

    // MARK: - Components
    /// 标题标签
    lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "报告标题",
            fontSize: 16,
            textColor: MalaColor_5E5E5E_0
        )
        return label
    }()
    /// 分割线
    lazy var separatorLine: UIView = {
        let view = UIView.separator(MalaColor_EDEDED_0)
        return view
    }()
    /// 描述视图
    lazy var descView: UIView = {
        let view = UIView.separator(MalaColor_F8FAFD_0)
        view.layer.shadowOffset = CGSize(width: 0, height: MalaScreenOnePixel)
        view.layer.shadowColor = MalaColor_D7D7D7_0.CGColor
        view.layer.shadowOpacity = 1
        return view
    }()
    /// 曲别针图标
    private lazy var pinIcon: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "pin_icon"))
        return imageView
    }()
    /// 解读标签
    lazy var descTitleLabel: UILabel = {
        let label = UILabel(
            text: "解读：",
            fontSize: 12,
            textColor: MalaColor_363B4E_0
        )
        return label
    }()
    /// 解读详情标签
    lazy var descDetailLabel: UILabel = {
        let label = UILabel(
            text: "解读内容",
            fontSize: 10,
            textColor: MalaColor_5E5E5E_0
        )
        label.numberOfLines = 0
        return label
    }()
    
    
    // MARK: - Instance Method
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        
        // SubViews
        layoutView.addSubview(titleLabel)
        layoutView.addSubview(separatorLine)
        layoutView.addSubview(descView)
        layoutView.addSubview(pinIcon)
        descView.addSubview(descTitleLabel)
        descView.addSubview(descDetailLabel)
        
        
        // Autolayout
        titleLabel.snp_makeConstraints { (make) in
            make.height.equalTo(20)
            make.centerX.equalTo(layoutView.snp_centerX)
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.05)
        }
        separatorLine.snp_makeConstraints { (make) in
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.13)
            make.height.equalTo(MalaScreenOnePixel)
            make.centerX.equalTo(layoutView.snp_centerX)
            make.width.equalTo(layoutView.snp_width).multipliedBy(0.84)
        }
        descView.snp_makeConstraints { (make) in
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.74)
            make.left.equalTo(layoutView.snp_left).offset(12)
            make.right.equalTo(layoutView.snp_right).offset(-12)
            make.bottom.equalTo(layoutView.snp_bottom).multipliedBy(0.92)
        }
        pinIcon.snp_makeConstraints { (make) in
            make.height.equalTo(19)
            make.width.equalTo(19)
            make.centerY.equalTo(descView.snp_top)
            make.right.equalTo(descView.snp_right).offset(-11)
        }
        descTitleLabel.snp_makeConstraints { (make) in
            make.top.equalTo(descView.snp_bottom).multipliedBy(0.17)
            make.height.equalTo(12)
            make.left.equalTo(descView.snp_left).offset(16)
            make.right.equalTo(descView.snp_right).offset(-16)
        }
        descDetailLabel.snp_makeConstraints { (make) in
            make.top.equalTo(descView.snp_bottom).multipliedBy(0.32)
            make.left.equalTo(descView.snp_left).offset(16)
            make.right.equalTo(descView.snp_right).offset(-16)
            make.bottom.equalTo(descView.snp_bottom).multipliedBy(0.82)
        }
    }
    
    func hideDescription() {
        // descView.hidden = true
        // pinIcon.hidden = true
    }
}