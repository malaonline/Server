//
//  AppDelegate.swift
//  mala-ios
//
//  Created by Liang Sun on 11/6/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

// MARK: - AppDelegate

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?


    func application(application: UIApplication, didFinishLaunchingWithOptions launchOptions: [NSObject: AnyObject]?) -> Bool {
        
        setupExterior()
        
        setupUmengConfig()
        
        // Setup Widow
        window = UIWindow(frame: UIScreen.mainScreen().bounds)
        window?.backgroundColor = UIColor.whiteColor()
        window?.rootViewController = defaultRootViewController
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

    /// Setup Common Exterior
    private func setupExterior() {
        
        // Setup Appearance
        UINavigationBar.appearance().tintColor = MalaAppearanceTextColor
        UINavigationBar.appearance().setBackgroundImage(UIImage.withColor(UIColor.redColor()), forBarMetrics: .Default)
        UITabBar.appearance().tintColor = MalaAppearanceTintColor
        
        // Setup Exterior
        UIApplication.sharedApplication().statusBarStyle = .LightContent
    }

}

// MARK: - SDK Configutarion
extension AppDelegate {

    private func setupUmengConfig() {
        
        // Start report (channelId is default to "App Store")
        MobClick.startWithAppkey(Mala_Umeng_AppKey, reportPolicy: BATCH, channelId: nil)
    }
}


// MARK: - RootViewController Switch
extension AppDelegate {
    
    // Setup RootViewController
    private var defaultRootViewController: UIViewController {
        
        // 1. Has Logged
        //        if HasLogged {
        //            return isNewVersion ? NewFeatureViewController() : WelcomeViewController()
        //        }
        
        // 2. not logged in
        //        return loginViewController()
        return MainViewController()
    }
    
}

