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
        
        
        // Setup Window
        window = UIWindow(frame: UIScreen.mainScreen().bounds)
        window?.backgroundColor = UIColor.whiteColor()
        window?.rootViewController = MainViewController()
        window?.makeKeyAndVisible()
        
        return true
    }

    func applicationWillResignActive(application: UIApplication) {
        // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
        // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
    }

    func applicationDidEnterBackground(application: UIApplication) {
        // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later.
        // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
    }

    func applicationWillEnterForeground(application: UIApplication) {
        // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
    }

    func applicationDidBecomeActive(application: UIApplication) {
        // Restart any tasks that were paused (or not yet started) while the application was inactive. If the application was previously in the background, optionally refresh the user interface.
    }

    func applicationWillTerminate(application: UIApplication) {
        // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
    }
    
    
    // MARK: - APNs
    func application(application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: NSData) {
        JPUSHService.registerDeviceToken(deviceToken)
    }
    
    func application(application: UIApplication, didReceiveRemoteNotification userInfo: [NSObject : AnyObject], fetchCompletionHandler completionHandler: (UIBackgroundFetchResult) -> Void) {
        println("didReceiveRemoteNotification: - \(userInfo)")
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
    func switchToStart() {
        window?.rootViewController = MainViewController()
    }
}