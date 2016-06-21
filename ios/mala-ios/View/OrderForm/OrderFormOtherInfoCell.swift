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
    /// 订单详情模型
    var model: OrderForm? {
        didSet {
            
            /// 订单状态
            if let status = MalaOrderStatus(rawValue: (model?.status ?? "")) {
                switch status {
                // 已支付、已退款状态时，不显示支付时间
                case .Penging, .Canceled:
                    
                    paymentDateString.hidden = true
                    paymentDateLabel.hidden = true
                    
                    createDateString.snp_updateConstraints { (make) -> Void in
                        make.top.equalTo(titleString.snp_bottom).offset(10)
                        make.left.equalTo(titleString.snp_left)
                        make.height.equalTo(12)
                        make.bottom.equalTo(self.contentView.snp_bottom).offset(-16)
                    }
                    break
                    
                default:
                    break
                }
            }
            self.orderId = self.model?.order_id ?? "-"
            self.createDate = self.model?.createAt
            self.paymentDate = self.model?.paidAt
        }
    }
    
    /// 订单编号
    var orderId: String = "" {
        didSet {
            titleLabel.text = orderId
        }
    }
    /// 订单创建时间
    var createDate: NSTimeInterval? {
        didSet {
            if let createDate = createDate {
                createDateLabel.text = getDateTimeString(createDate)
            }
        }
    }
    /// 订单支付时间
    var paymentDate: NSTimeInterval? {
        didSet {
            if let paymentDate = paymentDate {
                paymentDateLabel.text = getDateTimeString(paymentDate)
            }
        }
    }
    
    
    // MARK: - Components
    /// 订单编号
    private lazy var titleString: UILabel = {
        let label = UILabel(
            text: "订单编号：",
            fontSize: 12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: 12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    /// 创建时间
    private lazy var createDateString: UILabel = {
        let label = UILabel(
            text: "创建时间：",
            fontSize: 12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    private lazy var createDateLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: 12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    /// 支付时间
    private lazy var paymentDateString: UILabel = {
        let label = UILabel(
            text: "支付时间：",
            fontSize: 12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    private lazy var paymentDateLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: 12,
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
            make.top.equalTo(self.contentView.snp_top).offset(16)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.height.equalTo(12)
        }
        titleLabel.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(titleString.snp_top)
            make.left.equalTo(titleString.snp_right).offset(10)
            make.height.equalTo(12)
        }
        createDateString.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(titleString.snp_bottom).offset(10)
            make.left.equalTo(titleString.snp_left)
            make.height.equalTo(12)
        }
        createDateLabel.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(createDateString.snp_top)
            make.left.equalTo(createDateString.snp_right).offset(10)
            make.height.equalTo(12)
        }
        paymentDateString.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(createDateString.snp_bottom).offset(10)
            make.left.equalTo(titleString.snp_left)
            make.height.equalTo(12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-16)
        }
        paymentDateLabel.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(paymentDateString.snp_top)
            make.left.equalTo(paymentDateString.snp_right).offset(10)
            make.height.equalTo(12)
        }
    }
}