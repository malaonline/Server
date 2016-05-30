//
//  OrderFormOperatingView.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/13.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

public protocol OrderFormOperatingViewDelegate: class {
    ///  立即支付
    func OrderFormPayment()
    ///  再次购买
    func OrderFormBuyAgain()
    ///  取消订单
    func OrderFormCancel()
}


class OrderFormOperatingView: UIView {
    
    // MARK: - Property
    /// 需支付金额
    var price: Int = 0 {
        didSet{
            self.priceLabel.text = price.moneyCNY
        }
    }
    /// 订单状态
    var orderStatus: MalaOrderStatus = .Canceled {
        didSet {
            dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                self?.changeDisplayMode()
            })
        }
    }
    weak var delegate: OrderFormOperatingViewDelegate?
    
    
    // MARK: - Components
    private lazy var topLine: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.blackColor()
        view.alpha = 0.4
        return view
    }()
    /// 价格说明标签
    private lazy var stringLabel: UILabel = {
        let stringLabel = UILabel()
        stringLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        stringLabel.textColor = MalaColor_333333_0
        stringLabel.text = "合计:"
        return stringLabel
    }()
    /// 金额标签
    private lazy var priceLabel: UILabel = {
        let priceLabel = UILabel()
        priceLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        priceLabel.textColor = MalaColor_E26254_0
        priceLabel.textAlignment = .Left
        priceLabel.text = "￥0.00"
        return priceLabel
    }()
    /// 确定按钮（确认支付、再次购买、重新购买）
    private lazy var confirmButton: UIButton = {
        let button = UIButton()
        
        button.layer.borderColor = MalaColor_E26254_0.CGColor
        button.layer.borderWidth = MalaScreenOnePixel
        button.layer.cornerRadius = 3
        button.layer.masksToBounds = true
        
        button.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        button.setTitle("再次购买", forState: .Normal)
        button.setTitleColor(MalaColor_E26254_0, forState: .Normal)
        button.addTarget(self, action: #selector(OrderFormOperatingView.buyAgain), forControlEvents: .TouchUpInside)
        return button
    }()
    /// 取消按钮（取消订单）
    private lazy var cancelButton: UIButton = {
        let button = UIButton()
        
        button.layer.borderColor = MalaColor_939393_0.CGColor
        button.layer.borderWidth = MalaScreenOnePixel
        button.layer.cornerRadius = 3
        button.layer.masksToBounds = true
        
        button.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        button.setTitle("取消订单", forState: .Normal)
        button.setTitleColor(MalaColor_939393_0, forState: .Normal)
        button.addTarget(self, action: #selector(OrderFormOperatingView.cancelOrderForm), forControlEvents: .TouchUpInside)
        return button
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private method
    private func setupUserInterface() {
        // Style
        self.backgroundColor = MalaColor_FFFFFF_9
        
        // SubViews
        addSubview(topLine)
        addSubview(stringLabel)
        addSubview(priceLabel)
        addSubview(cancelButton)
        addSubview(confirmButton)
        
        // Autolayout
        topLine.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.snp_top)
            make.left.equalTo(self.snp_left)
            make.right.equalTo(self.snp_right)
            make.height.equalTo(MalaScreenOnePixel)
        })
        stringLabel.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.snp_left).offset(MalaLayout_Margin_12)
            make.centerY.equalTo(self.snp_centerY)
            make.height.equalTo(MalaLayout_FontSize_14)
        }
        priceLabel.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(stringLabel.snp_right)
            make.width.equalTo(100)
            make.bottom.equalTo(stringLabel.snp_bottom)
            make.height.equalTo(MalaLayout_FontSize_14)
        }
        confirmButton.snp_makeConstraints { (make) in
            make.right.equalTo(self.snp_right).offset(-MalaLayout_Margin_12)
            make.centerY.equalTo(self.snp_centerY)
            make.width.equalTo(confirmButton.snp_height).multipliedBy(2.78)
            make.height.equalTo(self.snp_height).multipliedBy(0.55)
        }
        cancelButton.snp_makeConstraints { (make) in
            make.right.equalTo(confirmButton.snp_left).offset(-MalaLayout_Margin_10)
            make.centerY.equalTo(confirmButton.snp_centerY)
            make.width.equalTo(confirmButton.snp_height).multipliedBy(2.78)
            make.height.equalTo(self.snp_height).multipliedBy(0.55)
        }
    }
    
    /// 根据当前订单状态，渲染对应UI样式
    private func changeDisplayMode() {
        
        // 解除绑定事件
        cancelButton.removeTarget(self, action: #selector(OrderFormOperatingView.cancelOrderForm), forControlEvents: .TouchUpInside)
        confirmButton.removeTarget(self, action: #selector(OrderFormOperatingView.pay), forControlEvents: .TouchUpInside)
        confirmButton.removeTarget(self, action: #selector(OrderFormOperatingView.buyAgain), forControlEvents: .TouchUpInside)
        
        // 渲染UI样式
        switch orderStatus {
        case .Penging:
            
            // 待付款
            cancelButton.hidden = false
            confirmButton.hidden = false
            
            confirmButton.setTitleColor(MalaColor_E26254_0, forState: .Normal)
            
            confirmButton.setTitle("立即支付", forState: .Normal)
            confirmButton.setBackgroundImage(UIImage.withColor(MalaColor_E26254_0), forState: .Normal)
            confirmButton.setTitleColor(UIColor.whiteColor(), forState: .Normal)
            
            cancelButton.addTarget(self, action: #selector(OrderFormOperatingView.cancelOrderForm), forControlEvents: .TouchUpInside)
            confirmButton.addTarget(self, action: #selector(OrderFormOperatingView.pay), forControlEvents: .TouchUpInside)
            break
            
        case .Paid:
            
            // 已付款
            cancelButton.hidden = true
            confirmButton.hidden = false
            
            confirmButton.setTitle("再次购买", forState: .Normal)
            confirmButton.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Normal)
            confirmButton.setTitleColor(MalaColor_E26254_0, forState: .Normal)
            
            confirmButton.addTarget(self, action: #selector(OrderFormOperatingView.buyAgain), forControlEvents: .TouchUpInside)
            break
            
        case .Canceled:
            
            // 已取消
            cancelButton.hidden = true
            confirmButton.hidden = false
            
            confirmButton.setTitle("重新购买", forState: .Normal)
            confirmButton.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Normal)
            confirmButton.setTitleColor(MalaColor_E26254_0, forState: .Normal)
            
            confirmButton.addTarget(self, action: #selector(OrderFormOperatingView.buyAgain), forControlEvents: .TouchUpInside)
            break
            
        case .Refund:
            
            // 已退款
            cancelButton.hidden = true
            confirmButton.hidden = true
            break
            
        case .Confirm:
            //TODO: 确认订单页完善
            // 确认订单
            confirmButton.snp_updateConstraints { (make) in
                make.width.equalTo(confirmButton.snp_height).multipliedBy(3.89)
                make.height.equalTo(self.snp_height).multipliedBy(0.755)
            }
            confirmButton.setTitle("提交订单", forState: .Normal)
            confirmButton.setTitleColor(UIColor.whiteColor(), forState: .Normal)
            
            break
        }
    }
    
    
    // MARK: - Event Response
    /// 立即支付（确认订单页－提交订单）
    @objc func pay() {
        delegate?.OrderFormPayment()
    }
    /// 再次购买
    @objc func buyAgain() {
        delegate?.OrderFormBuyAgain()
    }
    /// 取消订单
    @objc func cancelOrderForm() {
        delegate?.OrderFormCancel()
    }
}