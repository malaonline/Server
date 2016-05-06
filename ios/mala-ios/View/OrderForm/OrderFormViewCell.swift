//
//  OrderFormViewCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/6.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class OrderFormViewCell: UITableViewCell {
    
    // MARK: - Property
    /// 奖学金模型
    var model: OrderForm? {
        didSet {
            
        }
    }
    
    
    // MARK: - Components
    /// 顶部分隔线视图
    private lazy var separatorView: UIView = {
        let separatorView = UIView()
        return separatorView
    }()
    /// 主要布局容器
    private lazy var content: UIImageView = {
        let content = UIImageView(image: UIImage(named: "scholarship_unused"))
        return content
    }()
    /// 顶部布局容器
    private lazy var topLayoutView: UIView = {
        let topLayoutView = UIView()
        return topLayoutView
    }()
    /// 底部布局容器
    private lazy var bottomLayoutView: UIView = {
        let bottomLayoutView = UIView()
        return bottomLayoutView
    }()
    /// 货币符号标识
    private lazy var moneySymbol: UILabel = {
        let moneySymbol = UILabel()
        moneySymbol.text = "￥"
        moneySymbol.font = UIFont.systemFontOfSize(MalaLayout_FontSize_17)
        moneySymbol.textColor = MalaColor_E26254_0
        return moneySymbol
    }()
    /// 价格文本框
    private lazy var priceLabel: UILabel = {
        let priceLabel = UILabel()
        priceLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_37)
        //        priceLabel.font = UIFont(name: "Damascus", size: MalaLayout_FontSize_37)
        priceLabel.textColor = MalaColor_E26254_0
        return priceLabel
    }()
    /// 名称文本框
    private lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_16)
        titleLabel.textColor = MalaColor_333333_0
        return titleLabel
    }()
    /// 描述文本框
    private lazy var descLabel: UILabel = {
        let descLabel = UILabel()
        descLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        descLabel.textColor = MalaColor_6C6C6C_0
        descLabel.hidden = true
        return descLabel
    }()
    /// 使用状态文本框
    private lazy var statusLabel: UILabel = {
        let statusLabel = UILabel()
        statusLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        statusLabel.textColor = MalaColor_E26254_0
        return statusLabel
    }()
    /// 选中效果箭头
    lazy var selectedView: UIImageView = {
        let selectedView = UIImageView(image: UIImage(named: "scholarship_selected"))
        selectedView.hidden = true
        return selectedView
    }()
    /// “有效期”
    private lazy var validityTermSymbol: UILabel = {
        let validityTermSymbol = UILabel()
        validityTermSymbol.text = "有效期"
        validityTermSymbol.font = UIFont.systemFontOfSize(MalaLayout_FontSize_10)
        validityTermSymbol.textColor = UIColor.whiteColor()
        return validityTermSymbol
    }()
    /// 有效期文本框
    private lazy var validityTermLabel: UILabel = {
        let validityTermLabel = UILabel()
        validityTermLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_10)
        validityTermLabel.textColor = UIColor.whiteColor()
        return validityTermLabel
    }()
    /// 使用说明文本框
    private lazy var useDirectionLabel: UILabel = {
        let useDirectionLabel = UILabel()
        useDirectionLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_10)
        useDirectionLabel.textColor = UIColor.whiteColor()
        return useDirectionLabel
    }()
    
    
    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        contentView.backgroundColor = MalaColor_EDEDED_0
        
        // SubViews

        
        // Autolayout

    }

}
