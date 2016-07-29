//
//  PaymentViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 2/29/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class PaymentViewController: BaseViewController, PaymentBottomViewDelegate {
    
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
        
        configure()
        setupUserInterface()
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        sendScreenTrack(SAPaymentViewName)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        
    }
    
    
    // MARK: - Private Method
    private func configure() {
        // 冻结Pop手势识别
        self.navigationController?.interactivePopGestureRecognizer?.enabled = false
        /// 默认选中项
        MalaOrderObject.channel = .Alipay
    }
    
    private func setupUserInterface() {
        // Style
        title = "支付"
        view.backgroundColor = UIColor.whiteColor()
        paymentConfirmView.delegate = self
        
        // SubViews
        view.addSubview(paymentTableView)
        view.addSubview(paymentConfirmView)
        
        // Autolayout
        paymentConfirmView.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(47)
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
    
    private func cancelOrder() {
        
        println("取消订单")
        ThemeHUD.showActivityIndicator()
        
        cancelOrderWithId(ServiceResponseOrder.id, failureHandler: { (reason, errorMessage) in
            ThemeHUD.hideActivityIndicator()

            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("PaymentViewController - cancelOrder Error \(errorMessage)")
            }
        }, completion:{ [weak self] (result) in
            ThemeHUD.hideActivityIndicator()
            println("取消订单结果 - \(result)")
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                self?.navigationController?.popViewControllerAnimated(true)
            })
        })
    }

    
    // MARK: - Override
    override func popSelf() {
        
        MalaAlert.confirmOrCancel(
            title: "取消订单",
            message: "确认取消订单吗？",
            confirmTitle: "取消订单",
            cancelTitle: "继续支付",
            inViewController: self,
            withConfirmAction: { [weak self] () -> Void in
                self?.cancelOrder()
            }, cancelAction: { () -> Void in
        })
  
    }
    
    @objc private func forcePop() {
        cancelOrder()
        navigationController?.popViewControllerAnimated(true)
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
            
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                
                ThemeHUD.hideActivityIndicator()
                
                /// 验证返回值是否有效
                if let charge = charges {
                    /// 验证返回结果是否存在result(存在则表示失败)
                    if let _ = charge["result"] as? Bool {
                        /// 失败弹出提示
                        if let strongSelf = self {
                            let alert = JSSAlertView().show(strongSelf,
                                title: "部分课程时间已被占用，请重新选择上课时间",
                                buttonText: "重新选课",
                                iconImage: UIImage(named: "alert_PaymentFail")
                            )
                            alert.addAction(strongSelf.forcePop)
                        }
                        
                    }else {
                        /// 成功跳转支付
                        self?.createPayment(charge)
                    }
                }
                
            })
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