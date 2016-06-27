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
                    self?.goTopButton.hidden = true
                }else {
                    self?.defaultView.hidden = true
                    self?.goTopButton.hidden = false
                    self?.tableView.reloadData()
                    self?.scrollToToday()
                }
            })
        }
    }
    /// 距当前时间最近的一节未上课程下标
    var recentlyCourseIndexPath: NSIndexPath?
    /// 当前显示年月（用于TitleView显示）
    var currentDate: NSTimeInterval? {
        didSet {
            if currentDate != oldValue {
                titleLabel.text = getDateTimeString(currentDate ?? 0, format: "yyyy年M月")
                titleLabel.sizeToFit()
            }
        }
    }
    
    
    // MARK: - Components
    /// "跳转最近的未上课程"按钮
    private lazy var goTopButton: UIButton = {
        let button = UIButton()
        button.setBackgroundImage(UIImage(named: "goTop"), forState: .Normal)
        button.addTarget(self, action: #selector(CourseTableViewController.scrollToToday), forControlEvents: .TouchUpInside)
        button.hidden = true
        return button
    }()
    lazy var tableView: UITableView = {
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
    /// 我的课表未登录面板
    private lazy var unLoginDefaultView: UIView = {
        let view = MalaDefaultPanel()
        view.imageName = "course_noData"
        view.text = "您还没有登录"
        view.buttonTitle = "去登录"
        view.addTarget(self, action: #selector(CourseTableViewController.switchToLoginView))
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
        view.addSubview(goTopButton)
        tableView.addSubview(defaultView)
        tableView.addSubview(unLoginDefaultView)
        
        // AutoLayout
        tableView.snp_makeConstraints { (make) -> Void in
            make.center.equalTo(view.snp_center)
            make.size.equalTo(view.snp_size)
        }
        goTopButton.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(view.snp_right).offset(-20)
            make.bottom.equalTo(view.snp_bottom).offset(-64)
            make.width.equalTo(58)
            make.height.equalTo(58)
        }
        defaultView.snp_makeConstraints { (make) -> Void in
            make.size.equalTo(tableView.snp_size)
            make.center.equalTo(tableView.snp_center)
        }
        unLoginDefaultView.snp_makeConstraints { (make) -> Void in
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
        
        // 用户登录后请求数据，否则显示默认页面
        if !MalaUserDefaults.isLogined {
            
            unLoginDefaultView.hidden = false
            
            // 若在注销后存在课程数据残留，清除数据并刷新日历
            if model != nil {
                model = nil
                tableView.reloadData()
            }
            return
        }else {
            unLoginDefaultView.hidden = true
        }
        ThemeHUD.showActivityIndicator()
        
        // 发送网络请求
        getStudentCourseTable(failureHandler: { (reason, errorMessage) -> Void in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("CourseTableViewController - loadStudentCourseTable Error \(errorMessage)")
            }
            ThemeHUD.hideActivityIndicator()
        }, completion: { [weak self] (courseList) -> Void in
            // 解析学生上课时间表
            let result = parseStudentCourseTable(courseList)
            self?.recentlyCourseIndexPath = result.recently
            self?.model = result.model
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
        // 当最近一节课程划出屏幕时，显示“回到最近课程”按钮
        if indexPath == recentlyCourseIndexPath {
            goTopButton.hidden = true
        }
    }
    public func tableView(tableView: UITableView, didEndDisplayingCell cell: UITableViewCell, forRowAtIndexPath indexPath: NSIndexPath) {
        // 当最近一节课程划出屏幕时，显示“回到最近课程”按钮
        if indexPath == recentlyCourseIndexPath {
            goTopButton.hidden = false
        }
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
        dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
            self?.tableView.scrollToRowAtIndexPath(self?.recentlyCourseIndexPath ?? NSIndexPath(forRow: 0, inSection: 0), atScrollPosition: .Top, animated: true)
        })
    }
    ///  跳转到挑选老师页面
    @objc private func switchToFindTeacher() {
        if let appDelegate = UIApplication.sharedApplication().delegate as? AppDelegate {
            appDelegate.window?.rootViewController = MainViewController()
            appDelegate.switchTabBarControllerWithIndex(0)
        }
    }
    ///  跳转到登陆页面
    @objc private func switchToLoginView() {
        
        let loginView = LoginViewController()
        loginView.popAction = loadStudentCourseTable
        
        self.presentViewController(
            UINavigationController(rootViewController: loginView),
            animated: true,
            completion: { () -> Void in
                
        })
    }
}