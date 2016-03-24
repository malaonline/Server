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
    public let kNotificationType = "notificationtype"
    
    ///  通知类型
    ///
    ///  - Changed:  调课
    ///  - Stoped:   停课
    ///  - Finished: 完课
    ///  - Starting: 开课
    public enum RemoteNotificationType: Int {
        case Changed = 1
        case Stoped = 2
        case Finished = 3
        case Starting = 4
    }
    
    /// 远程推送通知处理对象
    private var remoteNotificationTypeHandler: RemoteNotificationType? {
        willSet {
            
            if let
                type = newValue,
                appDelegate = UIApplication.sharedApplication().delegate as? AppDelegate,
                apsInfo = notificationInfo["aps"] as? [NSObject : AnyObject],
                message = apsInfo["alert"] as? String {
                
                // 提示信息
                var title: String = ""
                // 提示框确认闭包
                let action: () -> () = {
                    appDelegate.switchTabBarControllerWithIndex(1)
                }
                
                ///  匹配信息类型
                switch type {
                    
                case .Changed:
                    title = "调课完成"
            
                case .Stoped:
                    title = "停课完成"
            
                case .Finished:
                    title = "完课评价"
            
                case .Starting:
                    title = "课前通知"
            
                }
                
                // 若当前在前台，弹出提示
                if MalaIsForeground {
                    
                    // 获取当前控制器
                    if let viewController = getActivityViewController() {
                        
                        MalaAlert.confirmOrCancel(
                            title: title,
                            message: message,
                            confirmTitle: "前往课表",
                            cancelTitle: "取消",
                            inViewController: viewController,
                            withConfirmAction: action, cancelAction: { () -> Void in
                        })
                    }
                }
            }
        }
    }
    
    /// 通知信息字典
    private var notificationInfo: [NSObject : AnyObject] = [NSObject : AnyObject]()
    
    
    // MARK: - Method
    ///  处理APNs
    ///
    ///  - parameter userInfo: 通知信息字典
    public func handleRemoteNotification(userInfo: [NSObject : AnyObject]) -> Bool {
        
        if let
            type = Int(userInfo[kNotificationType] as? String ?? "0"),
            remoteNotificationType = RemoteNotificationType(rawValue: type) {
            
            notificationInfo = userInfo
            remoteNotificationTypeHandler = remoteNotificationType
                
            return true
        }else {
            return false
        }
    }
}