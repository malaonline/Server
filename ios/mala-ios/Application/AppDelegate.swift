//
//  AppDelegate.swift
//  mala-ios
//
//  Created by Liang Sun on 11/6/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit
import IQKeyboardManagerSwift


@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    var deviceToken: NSData?
    var notRegisteredPush = true
    
    /// 通知类型键
    private let kNotificationType = "notificationtype"
    enum RemoteNotificationType: Int {
        case Changed = 1
        case Stoped = 2
        case Finished = 3
        case Starting = 4
    }
    /// 远程推送通知处理对象
    private var remoteNotificationTypeHandler: RemoteNotificationType? {
        willSet {
            if let type = newValue {
                
                switch type {
                    
                case .Changed:
                    switchTabBarControllerWithIndex(1)
                    
                case .Stoped:
                    switchTabBarControllerWithIndex(1)
                    
                case .Finished:
                    switchTabBarControllerWithIndex(1)
                    
                case .Starting:
                    switchTabBarControllerWithIndex(1)

                }
            }
        }
    }

    
    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
        
        // 全局的外观自定义
        customAppearance()
        registerThirdParty()
        
        // 配置JPush
        #if USE_PRD_SERVER
            let apsForProduction = false
        #else
            let apsForProduction = true
        #endif
        JPUSHService.setupWithOption(launchOptions, appKey: "273de02f3da48856d02acc3d", channel: "AppStore", apsForProduction: apsForProduction)
        let kUserNotificationBSA: UIUserNotificationType = [.Badge, .Sound, .Alert]
        JPUSHService.registerForRemoteNotificationTypes(kUserNotificationBSA.rawValue, categories: nil)
        
        if MalaUserDefaults.isLogined {
            
            // 记录启动通知类型
            if let
                notification = launchOptions?[UIApplicationLaunchOptionsRemoteNotificationKey] as? UILocalNotification,
                userInfo = notification.userInfo,
                type = Int(userInfo[kNotificationType] as? String ?? "0") {
                    remoteNotificationTypeHandler = RemoteNotificationType(rawValue: type)
            }
            
        }
        
        // Setup Window
        window = UIWindow(frame: UIScreen.mainScreen().bounds)
        window?.backgroundColor = UIColor.whiteColor()
        window?.rootViewController = MainViewController()
        window?.makeKeyAndVisible()

        return true
    }

    
    // MARK: - Life Cycle
    func applicationWillResignActive(application: UIApplication) {
        
        println("Will Resign Active")
        
        UIApplication.sharedApplication().applicationIconBadgeNumber = 0
    }

    func applicationDidEnterBackground(application: UIApplication) {
        
    }

    func applicationWillEnterForeground(application: UIApplication) {
        
    }

    func applicationDidBecomeActive(application: UIApplication) {
        
        println("Did Become Active")
        
        application.applicationIconBadgeNumber = 0
    }

    func applicationWillTerminate(application: UIApplication) {
        
    }
    
    
    // MARK: - APNs
    func registerThirdPartyPushWithDeciveToken(deviceToken: NSData, pusherID: String) {
        
        JPUSHService.registerDeviceToken(deviceToken)
        JPUSHService.setTags(Set(["iOS"]), alias: pusherID, callbackSelector:nil, object: nil)
    }
    
    func application(application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: NSData) {
        println("didRegisterForRemoteNotificationsWithDeviceToken - \(MalaUserDefaults.parentID.value)")
        if let parentID = MalaUserDefaults.parentID.value {
            if notRegisteredPush {
                notRegisteredPush = false
                registerThirdPartyPushWithDeciveToken(deviceToken, pusherID: String(parentID))
            }
        }
        
        // 纪录设备token，用于初次登录或注册有 pusherID 后，或“注销再登录”
        self.deviceToken = deviceToken
    }
    
    func application(application: UIApplication, didReceiveRemoteNotification userInfo: [NSObject : AnyObject]) {
        
    }
    
    func application(application: UIApplication, didReceiveRemoteNotification userInfo: [NSObject : AnyObject], fetchCompletionHandler completionHandler: (UIBackgroundFetchResult) -> Void) {
        println("didReceiveRemoteNotification: - \(userInfo)")
        JPUSHService.handleRemoteNotification(userInfo)
        
        if MalaUserDefaults.isLogined {
            if let type = Int(userInfo[kNotificationType] as? String ?? "0"), remoteNotificationType = RemoteNotificationType(rawValue: type) {
                remoteNotificationTypeHandler = remoteNotificationType
                completionHandler(UIBackgroundFetchResult.NewData)
            }
        }
        
    }
    
    func application(application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: NSError) {
        
        println(String(format: "did Fail To Register For Remote Notifications With Error: %@", error))
    }
    
    
    // MARK: - openURL
    func application(application: UIApplication, openURL url: NSURL, sourceApplication: String?, annotation: AnyObject) -> Bool {
        // 微信,支付宝 回调
        let canHandleURL = Pingpp.handleOpenURL(url) { (result, error) -> Void in
            // 处理Ping++回调
            let handler = HandlePingppBehaviour()
            handler.handleResult(result, error: error, currentViewController: MalaPaymentController)
        }
        return canHandleURL
    }
    
    func application(app: UIApplication, openURL url: NSURL, options: [String : AnyObject]) -> Bool {
        // 微信,支付宝 回调
        let canHandleURL = Pingpp.handleOpenURL(url) { (result, error) -> Void in
            // 处理Ping++回调
            let handler = HandlePingppBehaviour()
            handler.handleResult(result, error: error, currentViewController: MalaPaymentController)
        }
        return canHandleURL
    }
    
    
    // MARK: - SDK Configuration
    func registerThirdParty() {
        // 友盟 - 发送启动通知(channelId 默认为 "App Store")
        MobClick.startWithAppkey(Mala_Umeng_AppKey, reportPolicy: BATCH, channelId: nil)
        
        // Ping++ - 开启DEBUG模式log
        Pingpp.setDebugMode(true)
        
        // IQKeyboardManager - 开启键盘自动管理
        IQKeyboardManager.sharedManager().enable = true
    }
        
    
    // MARK: - UI
    /// 设置公共外观样式
    private func customAppearance() {
        
        // NavigationBar
        UINavigationBar.appearance().tintColor = MalaColor_6C6C6C_0
        UINavigationBar.appearance().setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forBarMetrics: .Default)
        UINavigationBar.appearance().shadowImage = UIImage()
        
        // TabBar
        UITabBar.appearance().tintColor = MalaColor_6C6C6C_0
    }
    
    
    // MARK: - Public Method
    ///  切换到首页
    func switchToStart() {
        window?.rootViewController = MainViewController()
    }
    
    ///  切换到TabBarController指定控制器
    ///
    ///  - parameter index: 指定控制器下标
    func switchTabBarControllerWithIndex(index: Int) {

        guard let tabbarController = window?.rootViewController as? MainViewController
            where index <= ((tabbarController.viewControllers?.count ?? 0)-1) && index >= 0 else {
            return
        }
        
        tabbarController.selectedIndex = index
    }
}