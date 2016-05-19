//
//  MemberPrivilegesViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/16.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

private let MemberPrivilegesLearningReportCellReuseID = "MemberPrivilegesLearningReportCellReuseID"
private let MemberPrivilegesMemberSerivceCellReuseID = "MemberPrivilegesMemberSerivceCellReuseID"

class MemberPrivilegesViewController: UITableViewController {

    // MARK: - Property
    
    
    // MARK: - Components
    
    
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configure()
        setupUserInterface()
        setupNotification()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    // MARK: - Private Method
    private func configure() {
        
        tableView.estimatedRowHeight = 230
        
        // register
        tableView.registerClass(LearningReportCell.self, forCellReuseIdentifier: MemberPrivilegesLearningReportCellReuseID)
        tableView.registerClass(MemberSerivceCell.self, forCellReuseIdentifier: MemberPrivilegesMemberSerivceCellReuseID)
    }
    
    private func setupUserInterface() {
        // Style
        tableView.backgroundColor = MalaColor_EDEDED_0
        tableView.separatorStyle = .None
    }
    
    private func setupNotification() {
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_PushIntroduction,
            object: nil,
            queue: nil
        ) { [weak self] (notification) -> Void in
            
            if let index = notification.object as? Int {
                // 跳转到简介页面
                let viewController = ThemeIntroductionView()
                viewController.hidesBottomBarWhenPushed = true
                viewController.index = index
                self?.navigationController?.pushViewController(viewController, animated: true)
            }
        }
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_ShowLearningReport,
            object: nil,
            queue: nil
        ) { [weak self] (notification) -> Void in
            
            /// 执行学习报告相关操作
            if let index = notification.object as? Int {
                
                switch index {
                case -1:
                    
                    // 登录
                    self?.login()
                    break
                    
                case 0:
                    
                    // 显示学习报告样本
                    self?.showReportDemo()
                    break
                    
                case 1:
                    
                    // 显示真实学习报告
                    self?.showMyReport()
                    break
                    
                default:
                    break
                }
            }
        }
    }
    
    
    // MARK: - Event Response
    /// 登录
    @objc private func login() {
        self.presentViewController(
            UINavigationController(rootViewController: LoginViewController()),
            animated: true,
            completion: { () -> Void in
                
        })
    }
    /// 显示学习报告样本
    @objc private func showReportDemo() {
        let viewController = LearningReportViewController()
        viewController.hidesBottomBarWhenPushed = true
        self.navigationController?.pushViewController(viewController, animated: true)
    }
    /// 显示我的学习报告
    @objc private func showMyReport() {
        let viewController = LearningReportViewController()
        viewController.hidesBottomBarWhenPushed = true
        self.navigationController?.pushViewController(viewController, animated: true)
    }
    
    
    // MARK: - DataSource
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 2
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        switch indexPath.row {
        case 0:
            /// 学习报告
            let cell = tableView.dequeueReusableCellWithIdentifier(MemberPrivilegesLearningReportCellReuseID, forIndexPath: indexPath) as! LearningReportCell
            
            return cell
            
            
        case 1:
            /// 会员专享
            let cell = tableView.dequeueReusableCellWithIdentifier(MemberPrivilegesMemberSerivceCellReuseID, forIndexPath: indexPath) as! MemberSerivceCell
            return cell
            
            
        default:
            return UITableViewCell()
        }
    }
    
    
    // MARK: - Delegate
    override func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }
    
    override func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0.01
    }
    
    override func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.01
    }
    
    deinit {
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_PushIntroduction, object: nil)
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_ShowLearningReport, object: nil)
    }
}