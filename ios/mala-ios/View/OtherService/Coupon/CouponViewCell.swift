//
//  CouponViewCell.swift
//  mala-ios
//
//  Created by 王新宇 on 3/3/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CouponViewCell: UITableViewCell {
    
    // MARK: - Property
    /// 奖学金模型
    var model: CouponModel? {
        didSet {
            // 设置奖学金对象模型数据
            self.priceLabel.text = String(format: "%d", (model?.amount ?? 0))
            self.titleLabel.text = model?.name
            self.validityTermLabel.text = String(timeStamp: (model?.expired_at ?? 0))
            // 使用说明暂时写为常量
            self.useDirectionLabel.text = "仅在线支付使用"
            
            // 优惠券描述暂时不使用
            //self.descLabel.text = model?.desc
            
            // 奖学金使用状态
            if model?.status == .Used {
                // 已使用
                self.content.image = UIImage(named: "scholarship_used")
                self.selectedView.hidden = true
            }else if model?.status == .Unused {
                // 未使用
                self.statusLabel.text = "未使用"
                self.selectedView.hidden = true
            }else if model?.status == .Expired {
                // 已过期
                setStyleExpired()
                self.selectedView.hidden = true
            }
        }
    }
    /// 重写选中方法，实现选中效果
    override var selected: Bool {
        didSet {
            self.selectedView.hidden = !selected
            
            // 将选中项保存至公共模型中
            if selected {
                MalaCourseChoosingObject.coupon = self.model
            }
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
        moneySymbol.textColor = MalaTeacherCellLevelColor
        return moneySymbol
    }()
    /// 价格文本框
    private lazy var priceLabel: UILabel = {
        let priceLabel = UILabel()
        priceLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_37)
        priceLabel.textColor = MalaTeacherCellLevelColor
        //        priceLabel.backgroundColor = UIColor.lightGrayColor()
        return priceLabel
    }()
    /// 名称文本框
    private lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_16)
        titleLabel.textColor = MalaDetailsCellTitleColor
        return titleLabel
    }()
    /// 描述文本框
    private lazy var descLabel: UILabel = {
        let descLabel = UILabel()
        descLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        descLabel.textColor = MalaAppearanceTextColor
        descLabel.hidden = true
        return descLabel
    }()
    /// 使用状态文本框
    private lazy var statusLabel: UILabel = {
        let statusLabel = UILabel()
        statusLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        statusLabel.textColor = MalaTeacherCellLevelColor
        return statusLabel
    }()
    /// 选中效果箭头
    private lazy var selectedView: UIImageView = {
        let selectedView = UIImageView(image: UIImage(named: "scholarship_selected"))
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
        contentView.backgroundColor = MalaTeacherCellBackgroundColor
        
        // SubViews
        contentView.addSubview(separatorView)
        contentView.addSubview(content)
        content.addSubview(topLayoutView)
        content.addSubview(bottomLayoutView)
        
        topLayoutView.addSubview(moneySymbol)
        topLayoutView.addSubview(priceLabel)
        topLayoutView.addSubview(titleLabel)
        topLayoutView.addSubview(descLabel)
        topLayoutView.addSubview(statusLabel)
        topLayoutView.addSubview(selectedView)
        bottomLayoutView.addSubview(validityTermSymbol)
        bottomLayoutView.addSubview(validityTermLabel)
        bottomLayoutView.addSubview(useDirectionLabel)
        
        // Autolayout
        separatorView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_12)
            make.bottom.equalTo(content.snp_top)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_Margin_8)
        }
        content.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(separatorView.snp_bottom)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_12)
            make.bottom.equalTo(self.contentView.snp_bottom)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
        }
        topLayoutView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(content.snp_top)
            make.left.equalTo(content.snp_left)
            make.right.equalTo(content.snp_right)
            make.height.equalTo(content.snp_height).multipliedBy(0.7)
        }
        bottomLayoutView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(topLayoutView.snp_bottom)
            make.left.equalTo(content.snp_left)
            make.right.equalTo(content.snp_right)
            make.bottom.equalTo(content.snp_bottom)
        }
        
        moneySymbol.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(content.snp_left).offset(MalaLayout_Margin_20)
            make.bottom.equalTo(priceLabel.snp_bottom)
            make.height.equalTo(MalaLayout_FontSize_17)
        }
        priceLabel.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(moneySymbol.snp_right).offset(MalaLayout_Margin_3)
            make.height.equalTo(MalaLayout_FontSize_37)
            make.centerY.equalTo(topLayoutView.snp_centerY)
        }
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(priceLabel.snp_top)
            make.left.equalTo(priceLabel.snp_right).offset(MalaLayout_FontSize_12)
            make.height.equalTo(MalaLayout_FontSize_16)
        }
        descLabel.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(priceLabel.snp_bottom)
            make.left.equalTo(titleLabel.snp_left)
            make.height.equalTo(MalaLayout_FontSize_11)
        }
        statusLabel.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(MalaLayout_FontSize_12)
            make.right.equalTo(content.snp_right).offset(-MalaLayout_Margin_20)
            make.centerY.equalTo(topLayoutView.snp_centerY)
        }
        selectedView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(topLayoutView.snp_top)
            make.right.equalTo(topLayoutView.snp_right)
        }
        validityTermSymbol.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(bottomLayoutView.snp_left).offset(MalaLayout_Margin_20)
            make.centerY.equalTo(bottomLayoutView.snp_centerY)
            make.height.equalTo(MalaLayout_FontSize_10)
        }
        validityTermLabel.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(bottomLayoutView.snp_centerY)
            make.left.equalTo(validityTermSymbol.snp_right).offset(MalaLayout_Margin_10)
            make.height.equalTo(MalaLayout_FontSize_10)
        }
        useDirectionLabel.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(bottomLayoutView.snp_centerY)
            make.right.equalTo(bottomLayoutView.snp_right).offset(-MalaLayout_Margin_20)
            make.height.equalTo(MalaLayout_FontSize_10)
        }
    }
    
    private func setStyleExpired() {
        self.moneySymbol.textColor = MalaAppearanceTextColor
        self.priceLabel.textColor = MalaAppearanceTextColor
        self.titleLabel.textColor = MalaAppearanceTextColor
        self.descLabel.textColor = MalaAppearanceTextColor
        self.statusLabel.textColor = MalaAppearanceTextColor
        self.content.image = UIImage(named: "scholarship_expired")
        self.statusLabel.text = "已过期"
    }
}