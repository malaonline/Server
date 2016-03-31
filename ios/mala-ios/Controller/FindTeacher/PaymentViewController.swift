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
    /// 弹栈闭包
    var popAction: (()->())?

    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        setupUserInterface()
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        
        // 创建订单
        createOrder()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        // 设置BarButtomItem间隔
        let spacer = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
        spacer.width = -MalaLayout_Margin_12
        
        // leftBarButtonItem
        let leftBarButtonItem = UIBarButtonItem(customView:
            UIButton(
                imageName: "leftArrow_normal",
                highlightImageName: "leftArrow_press",
                target: self,
                action: #selector(PaymentViewController.popSelf)
            )
        )
        navigationItem.leftBarButtonItems = [spacer, leftBarButtonItem]
        title = "支付"
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

    
    private func createOrder() {
        
        println("创建订单")
        ThemeHUD.showActivityIndicator()
        
        ///  创建订单
        createOrderWithForm(MalaOrderObject.jsonDictionary(), failureHandler: { [weak self] (reason, errorMessage) -> Void in
            
            ThemeHUD.hideActivityIndicator()
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("PaymentViewController - CreateOrder Error \(errorMessage)")
            }
            
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                self?.ShowTost("创建订单失败, 请重试！")
                self?.navigationController?.popViewControllerAnimated(true)
            })
            
        }, completion: { (order) -> Void in
                
            ThemeHUD.hideActivityIndicator()
            println("创建订单成功:\(order)")
            ServiceResponseOrder = order
        })

    }
    
    // MARK: - Event Response
    @objc private func popSelf() {
        self.navigationController?.popViewControllerAnimated(true)
    }
    
    
    // MARK: - Delegate
    func paymentDidConfirm() {
        
        // 获取支付信息
        getChargeToken()
    }
    
    func getChargeToken() {
        
        println("获取支付信息")
        MalaIsPaymentIn = true
        ThemeHUD.showActivityIndicator()
        
        ///  获取支付信息
        getChargeTokenWithChannel(MalaOrderObject.channel, orderID: ServiceResponseOrder.id, failureHandler: { (reason, errorMessage) -> Void in
            
            ThemeHUD.hideActivityIndicator()
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("PaymentViewController - getGharge Error \(errorMessage)")
            }
            
        }, completion: { [weak self] (charges) -> Void in
            
                println("获取支付信息:\(charges)")
                self?.createPayment(charges)
        })
    }
    
    func createPayment(charge: JSONDictionary) {
        MalaPaymentController = self
        
        ///  调用Ping++开始支付
        Pingpp.createPayment(charge,
            viewController: self,
            appURLScheme: getURLScheme(MalaOrderObject.channel)) { (result, error) -> Void in
                // 处理Ping++回调
                let handler = HandlePingppBehaviour()
                handler.handleResult(result, error: error, currentViewController: self)
        }
    }
    
    deinit {
        
        if MalaIsPaymentIn {
            popAction?()
        }
        
        println("Payment  ViewController  deinit")
    }
}