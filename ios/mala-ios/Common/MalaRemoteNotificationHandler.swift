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
            if let type = newValue, appDelegate = UIApplication.sharedApplication().delegate as? AppDelegate {

                switch type {
                    
                case .Changed:
                    appDelegate.switchTabBarControllerWithIndex(1)
                    
                case .Stoped:
                    appDelegate.switchTabBarControllerWithIndex(1)
                    
                case .Finished:
                    appDelegate.switchTabBarControllerWithIndex(1)
                    
                case .Starting:
                    appDelegate.switchTabBarControllerWithIndex(1)
                    
                }
            }
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
                
            remoteNotificationTypeHandler = remoteNotificationType
                
            return true
        }else {
            return false
        }
    }
}