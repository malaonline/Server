//
//  MainViewController.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015年 Mala Online. All rights reserved.
//

import UIKit

class MainViewController: UITabBarController {

    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        // Setup UserInterface
        setupTabBar()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    // MARK: - Private Method
    private func setupTabBar() {
        addSubViewController(HomeViewController(style: .Plain), title: MalaCommonString_FindTeacher, imageName: "verifyCode")
        addSubViewController(UITableViewController(style: .Plain), title: MalaCommonString_ClassSchedule, imageName: "schedule_normal")
        addSubViewController(UITableViewController(style: .Plain), title: MalaCommonString_Profile, imageName: "profile_normal")
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
}