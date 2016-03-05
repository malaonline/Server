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
                return "麻辣老师"
            case .Schedule:
                return "课程表"
            case .Profile:
                return "我的"
            }
        }
    }
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        // Setup UserInterface
        configure()
        setupTabBar()
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
        addSubViewController(HomeViewController(), title: MalaCommonString_FindTeacher, imageName: "verifyCode")
        addSubViewController(ClassScheduleViewController(), title: MalaCommonString_ClassSchedule, imageName: "schedule_normal")
        addSubViewController(ProfileViewController(style: .Plain), title: MalaCommonString_Profile, imageName: "profile_normal")
    }
    
     ///  Convenience Function to Create SubViewControllers
     ///  And Add Into TabBarViewController
     ///
     ///  - parameter viewController: ViewController
     ///  - parameter title:          String for ViewController's Title
     ///  - parameter imageName:      String for ImageName
    private func addSubViewController(viewController: UIViewController, title: String, imageName: String) {
        viewController.title = title
        viewController.tabBarItem.image = UIImage(named: imageName)
        let navigationController = MainNavigationController(rootViewController: viewController)
        addChildViewController(navigationController)
    }
    
    
    // MARK: - Delegate
    func tabBarController(tabBarController: UITabBarController, shouldSelectViewController viewController: UIViewController) -> Bool {
        
        guard let navi = viewController as? UINavigationController else {
            return false
        }
        
        
        // 点击[课程表]或[我的]页面
        if navi.topViewController is ProfileViewController ||
           navi.topViewController is ClassScheduleViewController {
            
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