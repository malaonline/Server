//
//  MainViewController.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015年 Mala Online. All rights reserved.
//

import UIKit

class MainViewController: UITabBarController, UITabBarControllerDelegate {

    
    // MARK: - Components
    /// 首页
    private lazy var homeViewController: MainNavigationController = {
        let naviVC = self.getNaviController(
            HomeViewController(),
            title: MalaCommonString_FindTeacher,
            imageName: "search_normal"
        )
        return naviVC
    }()
    /// 课程表
    private lazy var classScheduleViewController: MainNavigationController = {
        let naviVC = self.getNaviController(
            CourseTableViewController(style: .Grouped),
            title: MalaCommonString_ClassSchedule,
            imageName: "schedule_normal"
        )
        return naviVC
    }()
    /// 会员专享
    private lazy var memberPrivilegesViewController: MainNavigationController = {
        let naviVC  = self.getNaviController(
            MemberPrivilegesViewController(),
            title: MalaCommonString_MemberPrivileges,
            imageName: "serivce_normal"
        )
        return naviVC
    }()
    /// 个人
    private lazy var profileViewController: MainNavigationController = {
        let naviVC  = self.getNaviController(
            ProfileViewController(style: .Grouped),
            title: MalaCommonString_Profile,
            imageName: "profile_normal"
        )
        return naviVC
    }()
    
    
    // MARK: - Property
    private enum Tab: Int {
        
        case Teacher
        case Schedule
        case MemberPrivileges
        case Profile
        
        var title: String {
            
            switch self {
            case .Teacher:
                return MalaCommonString_FindTeacher
            case .Schedule:
                return MalaCommonString_ClassSchedule
            case .Profile:
                return MalaCommonString_Profile
            case .MemberPrivileges:
                return MalaCommonString_MemberPrivileges
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
        let viewControllers: [UIViewController] = [
            homeViewController,
            classScheduleViewController,
            memberPrivilegesViewController,
            profileViewController
        ]
        
        self.setViewControllers(viewControllers, animated: false)
    }
    
    /// 查询用户是否有未支付订单
    private func loadUnpaindOrder() {
                
        if !MalaUserDefaults.isLogined {
            return
        }
        
        getUserNewMessageCount({ (reason, errorMessage) in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("MainViewController - getUserNewMessageCount Error \(errorMessage)")
            }
        }, completion: { [weak self] (order, comment) in
            println("未支付订单数量：\(order), 待评价数量：\(comment)")
            
            MalaUnpaidOrderCount = order
            MalaToCommentCount = comment
            
            if order != 0 {
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    self?.popAlert()
                })
            }
            self?.profileViewController.showTabBadgePoint = (MalaUnpaidOrderCount > 0 || MalaToCommentCount > 0)
        })
    }
    
    /// 弹出未支付订单提示
    private func popAlert() {
        let alert = JSSAlertView().show(self,
                                        title: "您有订单尚未支付",
                                        buttonText: "查看订单",
                                        iconImage: UIImage(named: "alert_PaymentSuccess")
        )
        alert.addAction(switchToProfile)
    }
    
    
    /// 切换到个人信息页面
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
    private func getNaviController(viewController: UIViewController, title: String, imageName: String) -> MainNavigationController {
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