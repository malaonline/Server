//
//  HandlePingppBehaviour.swift
//  mala-ios
//
//  Created by 王新宇 on 3/4/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class HandlePingppBehaviour: NSObject {

    /// 最大重试次数
    let MaxRetry = 2
    /// 当前视图控制器
    weak var currentViewController: UIViewController? {
        didSet {
            println("视图控制器 - \(currentViewController)")
        }
    }
    
    ///  处理支付结果回调
    ///
    ///  - parameter result:                支付结果: success, fail, cancel, invalid
    ///  - parameter error:                 PingppError对象
    ///  - parameter currentViewController: 当前视图控制器
    func handleResult(result: String, error: PingppError?, currentViewController: UIViewController?) {
        
        guard currentViewController != nil else {
            println("HandlePingppBehaviour - 控制器为空")
            return
        }
        
        self.currentViewController = currentViewController
        
        if error == nil {
            println("PingppError is nil")
        }else {
            println("PingppError: code=\(error!.code), msg=\(error!.getMsg())")
        }
        
        switch result {
        case "success":
            // 支付成功后，向服务端验证支付结果
            validateOrderStatus()
            
        case "cancel":
            showCancelAlert()
            
        case "fail":
            showFailAlert()
            
        default:
            println("无法解析支付结果")
            break
        }
    }
    
    ///  获取服务端订单状态(支付结果)
    ///
    ///  - returns: 支付结果
    func validateOrderStatus() {
        
        // 获取订单信息
        getOrderInfo(ServiceResponseOrder.id, failureHandler: { (reason, errorMessage) -> Void in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("HandlePingppBehaviour - validateOrderStatus Error \(errorMessage)")
            }
        }, completion: { order -> Void in
            println("订单状态获取成功 \(order.status)")
            
            // 订单状态为已付款，则支付成功
            dispatch_async(dispatch_get_main_queue()) {
                if (order.status == MalaOrderStatus.Paid.rawValue) {
                    self.showSuccessAlert()
                }else {
                    self.showFailAlert()
                }
            }
            
            ThemeHUD.hideActivityIndicator()
        })
    }
    
    ///  支付取消弹窗
    func showCancelAlert() {
        ThemeHUD.hideActivityIndicator()
        guard self.currentViewController != nil else {
            return
        }
        
        _ = JSSAlertView().show(currentViewController!,
            title: "支付已取消",
            buttonText: "我知道了",
            iconImage: UIImage(named: "alert_PaymentFail")
        )
        // alert.addAction(popToRootViewController)
    }
    
    ///  支付成功弹窗
    func showSuccessAlert() {
        ThemeHUD.hideActivityIndicator()
        guard self.currentViewController != nil else {
            return
        }
        
        let alert = JSSAlertView().show(currentViewController!,
            title: "恭喜您已支付成功！",
            buttonText: "知道了",
            iconImage: UIImage(named: "alert_PaymentSuccess")
        )
        alert.addAction(popToRootViewController)
    }
    
    ///  支付失败弹窗
    func showFailAlert() {
        ThemeHUD.hideActivityIndicator()
        guard self.currentViewController != nil else {
            return
        }
        
        let alert = JSSAlertView().show(currentViewController!,
            title: "支付失败，请重试！",
            buttonText: "刷新",
            iconImage: UIImage(named: "alert_PaymentFail")
        )
        alert.addAction(popToRootViewController)
    }
    
    ///  退回到根视图
    func popToRootViewController() {
        guard self.currentViewController != nil else {
            return
        }
        currentViewController!.navigationController?.popToRootViewControllerAnimated(true)
    }
}