//
//  PaymentViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 2/29/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class PaymentViewController: UIViewController, PaymentBottomViewDelegate {
    
    // MARK: - Property
    
    
    // MARK: - Components
    /// 支付信息TableView
    private lazy var paymentTableView: PaymentTableView = {
        let paymentTableView = PaymentTableView(frame: CGRectZero, style: .Grouped)
        return paymentTableView
    }()
    /// 支付页面底部视图
    private lazy var paymentConfirmView: PaymentBottomView = {
        let paymentConfirmView = PaymentBottomView()
        return paymentConfirmView
    }()

    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        setupUserInterface()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        view.backgroundColor = UIColor.whiteColor()
        paymentConfirmView.delegate = self
        
        // SubViews
        view.addSubview(paymentTableView)
        view.addSubview(paymentConfirmView)
        
        // Autolayout
        paymentConfirmView.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(60)
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
            make.bottom.equalTo(self.view.snp_bottom)
        }
        paymentTableView.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
            make.top.equalTo(self.view.snp_top)
            make.bottom.equalTo(self.view.snp_bottom)
        }
    }
    

    // MARK: - Delegate
    func paymentDidConfirm() {
        println("确认支付")
    }
}