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

public class CourseTableViewController: UIViewController, UITableViewDataSource, UITableViewDelegate {

    // MARK: - Property
    /// 上课时间表数据模型
    var model: [[[StudentCourseModel]]]? {
        didSet {
            dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                ThemeHUD.hideActivityIndicator()
                if self?.model?.count == 0 {
                    self?.defaultView.hidden = false
                }else {
                    self?.defaultView.hidden = true
                    self?.tableView.reloadData()
                }
            })
        }
    }
    /// 当前显示年月（用于TitleView显示）
    var currentDate: NSTimeInterval? {
        didSet {
            if currentDate != oldValue {
                titleLabel.text = getDateTimeString(currentDate ?? 0, format: "yyyy年M月")
                titleLabel.sizeToFit()
            }
        }
    }
    /// 当前月份
    private let currentMonth = NSDate().month()
    /// 是否为App启动后首次显示
    private var isFirstShow = true
    
    
    // MARK: - Components
    private lazy var tableView: UITableView = {
        let tableView = UITableView(frame: CGRectZero, style: .Grouped)
        return tableView
    }()
    /// 我的课表缺省面板
    private lazy var defaultView: UIView = {
        let view = MalaDefaultPanel()
        view.imageName = "course_noData"
        view.text = "暂时还没有课程哦"
        view.buttonTitle = "去报名"
        view.addTarget(self, action: #selector(CourseTableViewController.switchToFindTeacher))
        view.hidden = true
        return view
    }()
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
    /// 导航栏TitleView
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "课表",
            fontSize: 16,
            textColor: MalaColor_000000_0
        )
        return label
    }()
    
    
    // MARK: - Life Cycle
    override public func viewDidLoad() {
        super.viewDidLoad()
        
        configure()
        setupUserInterface()
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
        
        // register
        tableView.registerClass(CourseTableViewCell.self, forCellReuseIdentifier: CourseTableViewCellReuseId)
        tableView.registerClass(CourseTableViewSectionHeader.self, forHeaderFooterViewReuseIdentifier: CourseTableViewSectionHeaderViewReuseId)
    }
    
    private func setupUserInterface() {
        // Style
        navigationItem.titleView = UIView()
        navigationItem.titleView?.addSubview(titleLabel)
        
        // SubViews
        view.addSubview(tableView)
        tableView.addSubview(defaultView)
        
        // AutoLayout
        tableView.snp_makeConstraints { (make) -> Void in
            make.center.equalTo(view.snp_center)
            make.size.equalTo(view.snp_size)
        }
        defaultView.snp_makeConstraints { (make) -> Void in
            make.size.equalTo(tableView.snp_size)
            make.center.equalTo(tableView.snp_center)
        }
        if let titleView = navigationItem.titleView {
            titleLabel.snp_makeConstraints { (make) -> Void in
                make.center.equalTo(titleView.snp_center)
            }
        }
    }
    
    ///  获取学生可用时间表
    private func loadStudentCourseTable() {
        
        // 课表页面允许用户未登录时查看，此时仅作为日历展示
        if !MalaUserDefaults.isLogined {
            
            // 若在注销后存在课程数据残留，清除数据并刷新日历
            if model != nil {
                model = nil
                tableView.reloadData()
            }
            return
        }
        ThemeHUD.showActivityIndicator()
        
        // 发送网络请求
        getStudentCourseTable(failureHandler: { (reason, errorMessage) -> Void in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("ClassSecheduleViewController - loadStudentCourseTable Error \(errorMessage)")
            }
            ThemeHUD.showActivityIndicator()
        }, completion: { [weak self] (courseList) -> Void in
            guard courseList != nil else {
                println("学生上课时间表为空！")
                ThemeHUD.hideActivityIndicator()
                return
            }
            self?.model = parseStudentCourseTable(courseList!)
        })
    }
    
    
    // MARK: - DataSource
    public func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return model?.count ?? 0
    }
    public func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return model?[section].count ?? 0
    }
    public func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(CourseTableViewCellReuseId, forIndexPath: indexPath) as! CourseTableViewCell
        cell.model = model?[indexPath.section][indexPath.row]
        return cell
    }
    
    
    // MARK: - Delegate
    public func tableView(tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = tableView.dequeueReusableHeaderFooterViewWithIdentifier(CourseTableViewSectionHeaderViewReuseId) as! CourseTableViewSectionHeader
        headerView.timeInterval = model?[section][0][0].end
        return headerView
    }
    public func tableView(tableView: UITableView, willDisplayCell cell: UITableViewCell, forRowAtIndexPath indexPath: NSIndexPath) {
        // 实时调整当前第一个显示的Cell日期为导航栏标题日期
        currentDate = (tableView.visibleCells.first as? CourseTableViewCell)?.model?[0].end
    }
    public func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 140
    }
    public func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 20
    }
    public func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return CGFloat((model?[indexPath.section][indexPath.row].count ?? 0) * 102)
    }
    public func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }
    
    
    // MARK: - Event Response
    ///  滚动到近日首个未上课程
    @objc private func scrollToToday() {
        
    }
    ///  跳转到挑选老师页面
    @objc private func switchToFindTeacher() {
        if let appDelegate = UIApplication.sharedApplication().delegate as? AppDelegate {
            appDelegate.window?.rootViewController = MainViewController()
            appDelegate.switchTabBarControllerWithIndex(0)
        }
    }
}