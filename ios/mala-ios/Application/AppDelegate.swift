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
    

    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
        
        // Setup Window
        window = UIWindow(frame: UIScreen.mainScreen().bounds)
        window?.backgroundColor = UIColor.whiteColor()
        let mainViewController = MainViewController()
        MalaMainViewController = mainViewController
        window?.rootViewController = mainViewController
        window?.makeKeyAndVisible()
        
        // 全局的外观自定义
        customAppearance()
        registerThirdParty()
        
        // 配置JPush
        #if DevDebug
            JPUSHService.setupWithOption(launchOptions, appKey: Mala_JPush_AppKey, channel: "AppStore", apsForProduction: false)
        #else
            JPUSHService.setupWithOption(launchOptions, appKey: Mala_JPush_AppKey, channel: "AppStore", apsForProduction: true)
        #endif
        
        let kUserNotificationBSA: UIUserNotificationType = [.Badge, .Sound, .Alert]
        JPUSHService.registerForRemoteNotificationTypes(kUserNotificationBSA.rawValue, categories: nil)
        
        if MalaUserDefaults.isLogined {
            
            // 记录启动通知类型
            if let
                notification = launchOptions?[UIApplicationLaunchOptionsRemoteNotificationKey] as? UILocalNotification,
                userInfo = notification.userInfo {
                    MalaRemoteNotificationHandler().handleRemoteNotification(userInfo)
            }
        }

        return true
    }

    
    // MARK: - Life Cycle
    func applicationWillResignActive(application: UIApplication) {
        
        println("Will Resign Active")
        
        // 发生支付行为跳回时，取消遮罩
        ThemeHUD.hideActivityIndicator()
        UIApplication.sharedApplication().applicationIconBadgeNumber = 0
    }

    func applicationDidEnterBackground(application: UIApplication) {
        
        println("Did Enter Background")
        
        MalaIsForeground = false
    }

    func applicationWillEnterForeground(application: UIApplication) {
        
        println("Will Enter Foreground")
        
        MalaIsForeground = true
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
    
    func application(application: UIApplication, didReceiveRemoteNotification userInfo: [NSObject : AnyObject], fetchCompletionHandler completionHandler: (UIBackgroundFetchResult) -> Void) {
        
        println("didReceiveRemoteNotification: - \(userInfo)")
        JPUSHService.handleRemoteNotification(userInfo)
        
        if MalaUserDefaults.isLogined {
            if MalaRemoteNotificationHandler().handleRemoteNotification(userInfo) {
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
        
//        // 友盟 - 发送启动通知(channelId 默认为 "App Store")
//        let UMConfigInstance = UMAnalyticsConfig();
//        UMConfigInstance.appKey = Mala_Umeng_AppKey;
//        UMConfigInstance.channelId = nil;
//        UMConfigInstance.bCrashReportEnabled = true;
//
//        MobClick.startWithConfigure(UMConfigInstance);

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
        
        // TabBar
        UITabBar.appearance().tintColor = MalaColor_82B4D9_0
    }
    
    
    // MARK: - Public Method
    
    /// 显示登陆页面
    func showLoginView() {
        
        let loginViewController = LoginViewController()
        loginViewController.closeAction = MalaCurrentCancelAction
        
        window?.rootViewController?.presentViewController(
            UINavigationController(rootViewController: loginViewController),
            animated: true,
            completion: { () -> Void in
                
        })
    }
    
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