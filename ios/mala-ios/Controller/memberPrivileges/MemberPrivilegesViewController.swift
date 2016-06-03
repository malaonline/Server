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
    /// 学科学习报告模型
    var report: SubjectReport = SubjectReport() {
        didSet {
            MalaSubjectReport = report
        }
    }
    /// 总练习数
    var totalNum: Int = 0 {
        didSet {
            MalaReportTotalNum = totalNum
        }
    }
    /// 练习正确数
    var rightNum: Int = 0 {
        didSet {
            MalaReportRightNum = rightNum
        }
    }
    /// 学习报告状态
    var reportStatus: MalaLearningReportStatus = .LoggingIn {
        didSet {
            dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                self?.tableView.reloadData()
            })
            
            // 若当前学习报告状态正确，获取学科学习报告数据
            if reportStatus == .MathSigned {
                loadSubjectReport()
            }
        }
    }
    /// 是否已Push新控制器标示（屏蔽pop到本页面时的数据刷新动作）
    var isPushed: Bool = false

    
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
        if !isPushed {
            loadStudyReportOverview()
        }
        isPushed = false
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
                viewController.title = "会员专享"
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
    
    // 获取学生学习报告总结
    private func loadStudyReportOverview() {
        
        self.reportStatus = .LoggingIn
        
        // 未登录状态
        if !MalaUserDefaults.isLogined {
            self.reportStatus = .UnLogged
            return
        }
        
        getStudyReportOverview({ [weak self] (reason, errorMessage) in
            
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("MemberPrivilegesViewController - loadStudyReportOverview Error \(errorMessage)")
            }
            
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                self?.reportStatus = .UnSigned
                self?.ShowTost("当前无法访问题目数据，请稍后再试")
            })
            
        }, completion: { [weak self] (results) in
            
            println("学习报告：\(results)")
            
            // 默认登录未报名状态
            var status: MalaLearningReportStatus = .UnSigned
            
            // 遍历学科报名情况
            for singleReport in results {
                
                // 已报名支持学习报告的科目
                if singleReport.subject_id == 1 && singleReport.supported && singleReport.purchased {
                    self?.totalNum = singleReport.total_nums
                    self?.rightNum = singleReport.right_nums
                    status = .MathSigned
                    break
                }
                
                // 报名非数学状态
                if singleReport.supported == true && singleReport.purchased == false {
                    status = .UnSignedMath
                }
            }
            
            self?.reportStatus = status
        })
    }
    
    // 获取学科学习报告
    private func loadSubjectReport() {
        getSubjectReport(1, failureHandler: { [weak self] (reason, errorMessage) in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("MemberPrivilegesViewController - loadSubjectReport Error \(errorMessage)")
            }
            
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                self?.ShowTost("当前无法访问题目数据，请稍后再试")
            })
        }, completion: { [weak self] (report) in
            println("学科学习报告：\(report)")
            self?.report = report
        })
    }
    
    
    // MARK: - Event Response
    /// 登录
    @objc private func login() {
                
        let loginViewController = LoginViewController()
        loginViewController.popAction = { [weak self] in
            self?.loadStudyReportOverview()
        }

        self.presentViewController(
            UINavigationController(rootViewController: loginViewController),
            animated: true,
            completion: { () -> Void in
                
        })
        isPushed = true
    }
    /// 显示学习报告样本
    @objc private func showReportDemo() {
        let viewController = LearningReportViewController()
        viewController.hidesBottomBarWhenPushed = true
        self.navigationController?.pushViewController(viewController, animated: true)
        isPushed = true
    }
    /// 显示我的学习报告
    @objc private func showMyReport() {
        let viewController = LearningReportViewController()
        viewController.sample = false
        viewController.hidesBottomBarWhenPushed = true
        self.navigationController?.pushViewController(viewController, animated: true)
        isPushed = true
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
            
            println("\(self.totalNum)-\(self.rightNum)")
            
            cell.totalNum = self.totalNum
            cell.rightNum = self.rightNum
            cell.reportStatus = self.reportStatus
            
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