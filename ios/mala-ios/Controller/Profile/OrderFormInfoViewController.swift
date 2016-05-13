//
//  OrderFormInfoViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/13.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class OrderFormInfoViewController: BaseViewController, OrderFormOperatingViewDelegate {

    // MARK: - Property
    /// 订单详情模型
    var model: OrderForm? {
        didSet {
            
        }
    }
    
    
    // MARK: - Compontents
    /// 订单详情页面
    private lazy var tableView: OrderFormTableView = {
        let tableView = OrderFormTableView(frame: CGRectZero, style: .Grouped)
        return tableView
    }()
    /// 底部操作视图
    private lazy var confirmView: OrderFormOperatingView = {
        let view = OrderFormOperatingView()
        return view
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
//        ThemeHUD.showActivityIndicator()
        
        setupUserInterface()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Private method
    private func setupUserInterface() {
        // Style
        self.title = "订单详情"
        
        // SubViews
        view.addSubview(confirmView)
        view.addSubview(tableView)
        
        confirmView.delegate = self
        
        // Autolayout
        confirmView.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.view.snp_bottom)
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
            make.height.equalTo(47)
        }
        tableView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.view.snp_top)
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
            make.bottom.equalTo(confirmView.snp_top)
        }
    }
    
    
    // MARK: - Delegate
    ///  立即支付
    func OrderFormPayment() {
    
    }
    
    ///  再次购买
    func OrderFormBuyAgain() {
        
    }
    
    ///  取消订单
    func OrderFormCancel() {
        
    }
}