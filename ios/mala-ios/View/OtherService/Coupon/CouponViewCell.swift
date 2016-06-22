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
            
            guard let model = model else {
                return
            }
            
            // 设置奖学金对象模型数据
            priceLabel.text = model.amountString
            titleLabel.text = model.minPriceString
            validityTermLabel.text = model.expiredString
            selectedView.hidden = true
            
            // 冻结所有[当前选课条件]未满足要求的奖学金
            let currentPrice = MalaCourseChoosingObject.getPrice()
            println("冻结Coupon - \(model.minPrice) - \(currentPrice)")
            if model.minPrice > currentPrice {
                disabled = true
                return
            }
            
            // 设置奖学金状态
            if model.status == .Used {
                // 已使用
                setStyleUsed()
            }else if model.status == .Unused {
                // 未使用
                setStyleUnused()
            }else if model.status == .Expired {
                // 已过期
                setStyleExpired()
            }
        }
    }
    // 是否显示[选中指示器]标识
    var showSelectedIndicator: Bool = false {
        didSet {
            self.selectedView.hidden = !showSelectedIndicator
        }
    }
    /// 是否被冻结
    var disabled: Bool = false {
        didSet {
            if disabled {
                setStyleDisable()
            }
        }
    }
    
    
    // MARK: - Components
    /// 顶部分隔线视图
    private lazy var separatorView: UIView = {
        let view = UIView()
        return view
    }()
    /// 主要布局容器
    private lazy var content: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "coupon_valid"))
        return imageView
    }()
    /// 左侧布局容器
    private lazy var leftLayoutView: UIView = {
        let view = UIView()
        return view
    }()
    /// 右侧布局容器
    private lazy var rightLayoutView: UIView = {
        let view = UIView()
        return view
    }()
    /// 货币符号标签
    private lazy var moneySymbol: UILabel = {
        let label = UILabel(
            text: "￥",
            fontSize: 17,
            textColor: UIColor.whiteColor()
        )
        return label
    }()
    /// 价格文本框
    private lazy var priceLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: 37,
            textColor: UIColor.whiteColor()
        )
        // label.font = UIFont(name: "Damascus", size: 37)
        return label
    }()
    /// 标题标签
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: 17,
            textColor: MalaColor_6DB2E5_0
        )
        return label
    }()
    /// 有效期标签
    private lazy var validityTermLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: 13,
            textColor: MalaColor_999999_0
        )
        return label
    }()
    /// 状态标识图标
    private lazy var statusIcon: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "coupon_expired"))
        imageView.hidden = true
        return imageView
    }()
    /// 选中效果箭头
    lazy var selectedView: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "coupon_selected"))
        imageView.hidden = true
        return imageView
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
        contentView.addSubview(separatorView)
        contentView.addSubview(content)
        content.addSubview(leftLayoutView)
        content.addSubview(rightLayoutView)
        
        leftLayoutView.addSubview(moneySymbol)
        leftLayoutView.addSubview(priceLabel)
        rightLayoutView.addSubview(titleLabel)
        rightLayoutView.addSubview(selectedView)
        rightLayoutView.addSubview(validityTermLabel)
        rightLayoutView.addSubview(statusIcon)

        // Autolayout
        separatorView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.bottom.equalTo(content.snp_top)
            make.right.equalTo(self.contentView.snp_right).offset(-12)
            make.height.equalTo(8)
        }
        content.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(separatorView.snp_bottom)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.bottom.equalTo(self.contentView.snp_bottom)
            make.right.equalTo(self.contentView.snp_right).offset(-12)
        }
        leftLayoutView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(content.snp_top)
            make.bottom.equalTo(content.snp_bottom)
            make.left.equalTo(content.snp_left)
            make.width.equalTo(content.snp_width).multipliedBy(0.2865)
        }
        rightLayoutView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(content.snp_top)
            make.bottom.equalTo(content.snp_bottom)
            make.left.equalTo(leftLayoutView.snp_right)
            make.right.equalTo(content.snp_right)
        }
        moneySymbol.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(priceLabel.snp_left)
            make.bottom.equalTo(priceLabel.snp_bottom).offset(-4)
            make.height.equalTo(17)
        }
        priceLabel.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(37)
            make.centerX.equalTo(leftLayoutView.snp_centerX).offset(5)
            make.centerY.equalTo(leftLayoutView.snp_centerY)
        }
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(content.snp_top).offset(20)
            make.left.equalTo(rightLayoutView.snp_left).offset(20)
            make.height.equalTo(17)
        }
        validityTermLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(titleLabel.snp_bottom).offset(12)
            make.left.equalTo(rightLayoutView.snp_left).offset(20)
            make.height.equalTo(13)
        }
        statusIcon.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(rightLayoutView.snp_top).offset(6)
            make.right.equalTo(rightLayoutView.snp_right).offset(-6)
            make.width.equalTo(50)
            make.height.equalTo(50)
        }
        selectedView.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(rightLayoutView.snp_centerY)
            make.right.equalTo(rightLayoutView.snp_right).offset(-10)
        }
    }
    ///  不可用样式(当前选课条件不满足使用条件)
    private func setStyleDisable() {
        titleLabel.textColor = MalaColor_999999_0
        content.image = UIImage(named: "coupon_unvalid")
        statusIcon.hidden = true
    }
    ///  过期样式(不可用)
    private func setStyleExpired() {
        titleLabel.textColor = MalaColor_999999_0
        content.image = UIImage(named: "coupon_unvalid")
        statusIcon.hidden = false
        statusIcon.image = UIImage(named: "coupon_expired")
    }
    ///  已使用样式(不可用)
    private func setStyleUsed() {
        titleLabel.textColor = MalaColor_999999_0
        content.image = UIImage(named: "coupon_unvalid")
        statusIcon.hidden = false
        statusIcon.image = UIImage(named: "coupon_used")
    }
    ///  未使用样式(可用)
    private func setStyleUnused() {
        titleLabel.textColor = MalaColor_6DB2E5_0
        content.image = UIImage(named: "coupon_valid")
        statusIcon.hidden = true
    }
}