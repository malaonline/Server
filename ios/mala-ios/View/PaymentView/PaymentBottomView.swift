//
//  PaymentBottomView.swift
//  mala-ios
//
//  Created by 王新宇 on 2/29/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

public protocol PaymentBottomViewDelegate: class {
    ///  确认交易
    func paymentDidConfirm()
}

class PaymentBottomView: UIView {
    
    weak var delegate: PaymentBottomViewDelegate?
    
    // MARK: - Components
    /// 确定按钮
    private lazy var confirmButton: UIButton = {
        let confirmButton = UIButton()
        confirmButton.backgroundColor = MalaTeacherCellLevelColor
        confirmButton.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_17)
        confirmButton.setTitle("支付", forState: .Normal)
        confirmButton.setTitleColor(UIColor.whiteColor(), forState: .Normal)
        confirmButton.layer.cornerRadius = 10
        confirmButton.layer.masksToBounds = true
        confirmButton.addTarget(self, action: "buttonDidTap", forControlEvents: .TouchUpInside)
        return confirmButton
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
        self.backgroundColor = UIColor.clearColor()
        
        // SubViews
        addSubview(confirmButton)
        
        // Autolayout
        confirmButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.snp_top).offset(MalaLayout_Margin_6)
            make.bottom.equalTo(self.snp_bottom).offset(-MalaLayout_Margin_6)
            make.left.equalTo(self.snp_left).offset(MalaLayout_Margin_12)
            make.right.equalTo(self.snp_right).offset(-MalaLayout_Margin_12)
        }
    }
    
    
    // MARK: - Event Response
    @objc func buttonDidTap() {
        delegate?.paymentDidConfirm()
    }
}