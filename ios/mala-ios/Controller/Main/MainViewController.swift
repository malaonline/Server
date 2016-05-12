//
//  MainViewController.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015年 Mala Online. All rights reserved.
//

import UIKit

class MainViewController: UITabBarController, UITabBarControllerDelegate {

    // MARK: - Property
    private enum Tab: Int {
        
        case Teacher
        case Schedule
        case Profile
        
        var title: String {
            
            switch self {
            case .Teacher:
                return MalaCommonString_FindTeacher
            case .Schedule:
                return MalaCommonString_ClassSchedule
            case .Profile:
                return MalaCommonString_Profile
            }
        }
    }
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        configure()
        setupTabBar()
        loadUnpaindOrder()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    // MARK: - Private Method
    private func configure() {
        delegate = self
    }
    
    private func setupTabBar() {
        
        /// 首页
        let homeViewController = getNaviController(
            HomeViewController(),
            title: MalaCommonString_FindTeacher,
            imageName: "search_normal"
        )
        
        /// 课程表
        let viewController = ClassScheduleViewController()
        let classScheduleViewController = getNaviController(
            viewController,
            title: MalaCommonString_ClassSchedule,
            imageName: "schedule_normal"
        )
        
        /// 个人
        let profileViewController = getNaviController(
            ProfileViewController(style: .Grouped),
            title: MalaCommonString_Profile,
            imageName: "profile_normal"
        )
        
        let viewControllers: [UIViewController] = [homeViewController, classScheduleViewController, profileViewController]
        self.setViewControllers(viewControllers, animated: false)
    }
    
    /// 查询用户是否有未支付订单
    private func loadUnpaindOrder() {
                
        if !MalaUserDefaults.isLogined {
            return
        }
        
        getUnpaidOrderCount({ (reason, errorMessage) in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("MainViewController - loadUnpaindOrder Error \(errorMessage)")
            }
        }, completion: { (count) in
            println("未支付订单：\(count)")
            if count != 0 {
                MalaUnpaidOrderCount = count
                dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                    self?.popAlert()
                })
            }else {
                MalaUnpaidOrderCount = 0
            }
        })
    }
    
    private func popAlert() {
        let alert = JSSAlertView().show(self,
                                        title: "您有订单尚未支付",
                                        buttonText: "查看订单",
                                        iconImage: UIImage(named: "alert_PaymentSuccess")
        )
        alert.addAction(switchToProfile)
    }
    
    private func switchToProfile() {
        
        let viewController = OrderFormViewController()
        viewController.hidesBottomBarWhenPushed = true
        
        if let naviVC = self.viewControllers?[0] as? UINavigationController {
            naviVC.pushViewController(viewController, animated: true)
        }
    }
    
    
     ///  Convenience Function to Create SubViewControllers
     ///  And Add Into TabBarViewController
     ///
     ///  - parameter viewController: ViewController
     ///  - parameter title:          String for ViewController's Title
     ///  - parameter imageName:      String for ImageName
    private func getNaviController(viewController: UIViewController, title: String, imageName: String) -> UINavigationController {
        viewController.title = title
        viewController.tabBarItem.image = UIImage(named: imageName)
        let navigationController = MainNavigationController(rootViewController: viewController)
        return navigationController
    }
    
    
    // MARK: - Delegate
    func tabBarController(tabBarController: UITabBarController, shouldSelectViewController viewController: UIViewController) -> Bool {
        
        guard let navi = viewController as? UINavigationController else {
            return false
        }
        
        
        // 点击[我的]页面前需要登录校验
        if navi.topViewController is ProfileViewController /*||
           navi.topViewController is ClassScheduleViewController*/ {
            
            // 未登陆则进行登陆动作
            if !MalaUserDefaults.isLogined {
                
                self.presentViewController(
                    UINavigationController(rootViewController: LoginViewController()),
                    animated: true,
                    completion: { () -> Void in
                        
                })
                return false
            }
        }
        
        return true
    }
}