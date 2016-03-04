//
//  HandlePingppBehaviour.swift
//  mala-ios
//
//  Created by 王新宇 on 3/4/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class HandlePingppBehaviour: NSObject {

    
    func handleResult(result: String, error: PingppError?, currentViewController: UIViewController?) {
        
        guard currentViewController != nil else {
            println("HandlePingppBehaviour - 控制器为空")
            return
        }
        
        func popToRootViewController() {
            currentViewController?.navigationController?.popToRootViewControllerAnimated(true)
        }
        
        // result : success, fail, cancel, invalid
        if error == nil {
            println("PingppError is nil")
        }else {
            println("PingppError: code=\(error!.code), msg=\(error!.getMsg())")
        }
        
        
        switch result {
        case "success":
            let alert = JSSAlertView().show(currentViewController!,
                title: "恭喜您已支付成功！",
                buttonText: "知道了",
                iconImage: UIImage(named: "alert_PaymentSuccess")
            )
            alert.addAction(popToRootViewController)
            
        case "cancel":
            let alert = JSSAlertView().show(currentViewController!,
                title: "支付已取消",
                buttonText: "我知道了",
                iconImage: UIImage(named: "alert_PaymentFail")
            )
            alert.addAction(popToRootViewController)
            
            
        case "fail":
            let alert = JSSAlertView().show(currentViewController!,
                title: "支付失败，请重试！",
                buttonText: "刷新",
                iconImage: UIImage(named: "alert_PaymentFail")
            )
            alert.addAction(popToRootViewController)
            
        default:
            
            println("无法解析支付结果")
            break
        }
    }
}
