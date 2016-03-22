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
            
            // 根据[订单状态]和[课程是否被抢占标记]来判断支付结果
            dispatch_async(dispatch_get_main_queue()) { [weak self] in
                
                // 判断是否被抢买
                if order.is_timeslot_allocated == false {
                    self?.showHasBeenPreemptedAlert()
                    return
                }
            
                // 若订单状态为已付款则表示支付成功，否则支付失败
                if order.status == MalaOrderStatus.Paid.rawValue {
                    self?.showSuccessAlert()
                }else {
                    self?.showFailAlert()
                }
            }
            
            ThemeHUD.hideActivityIndicator()
        })
    }
    
    ///  课程被抢买弹窗
    func showHasBeenPreemptedAlert() {
        ThemeHUD.hideActivityIndicator()
        guard self.currentViewController != nil else {
            return
        }
        
        let alert = JSSAlertView().show(currentViewController!,
            title: "您想要购买的课程已被他人抢买，支付金额将原路退回",
            buttonText: "我知道了",
            iconImage: UIImage(named: "alert_CourseBeenSeized")
        )
        alert.addAction(popToCourseChoosingViewController)
    }
    
    ///  支付取消弹窗
    func showCancelAlert() {
        ThemeHUD.hideActivityIndicator()
        guard self.currentViewController != nil else {
            return
        }
        
        let _ = JSSAlertView().show(currentViewController!,
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
    
    ///  退回首页
    func popToRootViewController() {
        guard self.currentViewController != nil else {
            return
        }
        
        // 回调回App时若直接PopToRootViewController会出现TabBar莫名自动添加一个item的问题，暂时使用此方式解决问题。
        ThemeHUD.showActivityIndicator()
        delay(0.5) { () -> Void in
            self.currentViewController!.navigationController?.popToRootViewControllerAnimated(true)
            ThemeHUD.hideActivityIndicator()
        }
    }
    
    ///  退回到选课页面
    func popToCourseChoosingViewController() {
        guard self.currentViewController != nil else {
            return
        }
        currentViewController!.navigationController?.popViewControllerAnimated(true)
    }
}