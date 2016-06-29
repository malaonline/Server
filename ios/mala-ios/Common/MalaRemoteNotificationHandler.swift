//
//  MalaRemoteNotificationHandler.swift
//  mala-ios
//
//  Created by 王新宇 on 3/24/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit


public class MalaRemoteNotificationHandler: NSObject {

    // MARK: - Property
    
    /// 通知类型键
    public let kNotificationType = "type"
    /// 附带参数键（暂时仅当type为2时，附带code为订单号）
    public let kNotificationCode = "code"
    
    ///  通知类型
    ///
    ///  - Changed:     调课完成 -> 课表
    ///  - Stoped:      退费成功 -> 订单详情
    ///  - Finished:    课程结束 -> 我的评价
    ///  - Starting:    上课通知 -> 课表
    ///  - Maturity:    奖学金到期 -> 我的奖学金
    ///  - Evaluation:  测评建档 -> 课表
    public enum RemoteNotificationType: Int {
        case Changed = 1
        case Refunds = 2
        case Finished = 3
        case Starting = 4
        case Maturity = 5
        case Evaluation = 6
    }
    
    /// 远程推送通知处理对象
    private var remoteNotificationTypeHandler: RemoteNotificationType? {
        willSet {
            println("远程推送通知处理对象 - \(newValue)")
            if let
                type = newValue,
                appDelegate = UIApplication.sharedApplication().delegate as? AppDelegate,
                apsInfo = notificationInfo["aps"] as? [NSObject : AnyObject],
                message = apsInfo["alert"] as? String {
                
                // 提示信息
                var title: String = ""
                // 提示确认闭包
                var action: ()->() = {
                    /// 课表页
                    appDelegate.switchTabBarControllerWithIndex(1)
                }
                
                ///  匹配信息类型
                switch type {
                    
                case .Changed:
                    title = "课程变动"
                    
                case .Refunds:
                    title = "退费成功"
                    action = {
                        /// 订单详情页
//                        if let viewController = getActivityViewController() where self.code != 0 {
//                            
//                            println("退费成功 - \(viewController) - \(self.code)")
//                            
//                            if let mainViewController = viewController as? MainViewController,
//                                naviVC = mainViewController.viewControllers?[0] as? UINavigationController {
//                                let orderFormViewController = OrderFormInfoViewController()
//                                orderFormViewController.id = self.code
//                                naviVC.pushViewController(orderFormViewController, animated: true)
//                            }else {
//                                let orderFormViewController = OrderFormInfoViewController()
//                                orderFormViewController.id = self.code
//                                viewController.navigationController?.pushViewController(orderFormViewController, animated: true)
//                            }
//                        }
                    }
            
                case .Finished:
                    title = "完课评价"
//                    action = {
//                        /// 我的评价
//                        if let viewController = getActivityViewController() {
//                            viewController.navigationController?.pushViewController(CommentViewController(), animated: true)
//                        }
//                    }
                    
                case .Starting:
                    title = "课前通知"
                    
                case .Maturity:
                    title = "奖学金即将到期"
                    action = {
                        /// 我的奖学金
                        if let viewController = getActivityViewController() {
                            viewController.navigationController?.pushViewController(CouponViewController(), animated: true)
                        }
                    }
                    
                case .Evaluation:
                    title = "测评建档"
                }
                
                // 若当前在前台，弹出提示
                if MalaIsForeground {
                    
                    // 获取当前控制器
                    if let viewController = getActivityViewController() {
                        
                        MalaAlert.confirmOrCancel(
                            title: title,
                            message: message,
                            confirmTitle: "去查看",
                            cancelTitle: "知道了",
                            inViewController: viewController,
                            withConfirmAction: action, cancelAction: {})
                    }
                }
                
            }
        }
    }
    
    /// 通知信息字典
    private var notificationInfo: [NSObject : AnyObject] = [NSObject : AnyObject]()
    /// 附带参数（订单号）
    private var code: Int = 0 {
        didSet {
            println("code - \(code)")
        }
    }
    
    
    // MARK: - Method
    ///  处理APNs
    ///
    ///  - parameter userInfo: 通知信息字典
    public func handleRemoteNotification(userInfo: [NSObject : AnyObject]) -> Bool {
        if let
            type = Int(userInfo[kNotificationType] as? String ?? "0"),
            remoteNotificationType = RemoteNotificationType(rawValue: type) {
            
            if let code = Int(userInfo[kNotificationCode] as? String ?? "0") {
                self.code = code
            }
            
            notificationInfo = userInfo
            remoteNotificationTypeHandler = remoteNotificationType
            
            return true
        }else {
            return false
        }
    }
}