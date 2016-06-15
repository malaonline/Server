//
//  CourseTableViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/6/14.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

private let CourseTableViewSectionHeaderViewReuseId = "CourseTableViewSectionHeaderViewReuseId"
private let CourseTableViewCellReuseId = "CourseTableViewCellReuseId"

public class CourseTableViewController: UITableViewController {

    // MARK: - Property
    /// 上课时间表数据模型
    var model: [[[StudentCourseModel]]]? {
        didSet {
            dispatch_async(dispatch_get_main_queue()) { [weak self] () -> Void in
                self?.tableView?.reloadData()
            }
        }
    }
    /// 当前月份
    private let currentMonth = NSDate().month()
    /// 是否为App启动后首次显示
    private var isFirstShow = true
    
    
    // MARK: - Components
    /// 保存按钮
    private lazy var saveButton: UIButton = {
        let saveButton = UIButton(
            title: "今天",
            titleColor: MalaColor_82B4D9_0,
            target: self,
            action: #selector(CourseTableViewController.scrollToToday)
        )
        saveButton.setTitleColor(MalaColor_E0E0E0_95, forState: .Disabled)
        return saveButton
    }()
    
    
    // MARK: - Life Cycle
    override public func viewDidLoad() {
        super.viewDidLoad()
        
        configure()
    }
    
    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override public func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        if isFirstShow {
            scrollToToday()
            isFirstShow = false
        }
        loadStudentCourseTable()
    }
    
    // MARK: - Private Method
    private func configure() {
        // tableView
        tableView.delegate = self
        tableView.dataSource = self
        tableView.separatorStyle = .None
        tableView.backgroundColor = UIColor.whiteColor()
        
        // Navigation
//        let spacerRight = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
//        spacerRight.width = -5
//        let rightBarButtonItem = UIBarButtonItem(customView: saveButton)
//        navigationItem.rightBarButtonItems = [rightBarButtonItem, spacerRight]
        
        // register
        tableView.registerClass(CourseTableViewCell.self, forCellReuseIdentifier: CourseTableViewCellReuseId)
        tableView.registerClass(CourseTableViewSectionHeader.self, forHeaderFooterViewReuseIdentifier: CourseTableViewSectionHeaderViewReuseId)
    }
    
    ///  获取学生可用时间表
    private func loadStudentCourseTable() {
        
        // 课表页面允许用户未登录时查看，此时仅作为日历展示
        if !MalaUserDefaults.isLogined {
            
            // 若在注销后存在课程数据残留，清除数据并刷新日历
            if model != nil {
                model = nil
                tableView?.reloadData()
            }
            return
        }
        
        // 发送网络请求
        getStudentCourseTable(failureHandler: { (reason, errorMessage) -> Void in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("ClassSecheduleViewController - loadStudentCourseTable Error \(errorMessage)")
            }
        }, completion: { [weak self] (courseList) -> Void in
            guard courseList != nil else {
                println("学生上课时间表为空！")
                return
            }
            self?.model = parseStudentCourseTable(courseList!)
        })
    }
    
    
    // MARK: - DataSource
    public override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return model?.count ?? 0
    }
    public override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return model?[section].count ?? 0
    }
    public override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(CourseTableViewCellReuseId, forIndexPath: indexPath) as! CourseTableViewCell
        cell.model = model?[indexPath.section][indexPath.row]
        return cell
    }
    
    
    // MARK: - Delegate
    public override func tableView(tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = tableView.dequeueReusableHeaderFooterViewWithIdentifier(CourseTableViewSectionHeaderViewReuseId) as! CourseTableViewSectionHeader
        headerView.timeInterval = model?[section][0][0].start
        return headerView
    }
    public override func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 140
    }
    public override func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 20
    }
    public override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return CGFloat((model?[indexPath.section][indexPath.row].count ?? 0) * 102)
    }
    public override func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }
    
    
    
    
    // MARK: - Event Response
    ///  滚动到近日首个未上课程
    @objc private func scrollToToday() {
        
    }
}