//
//  CommentViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/6/7.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

private let CommentViewCellReuseId = "CommentViewCellReuseId"

class CommentViewController: BaseTableViewController {

    // MARK: - Property
    /// 优惠券模型数组
    var models: [StudentCourseModel] = [] {
        didSet {
            dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                if self?.models.count == 0 {
                    self?.defaultView.hidden = false
                }else {
                    self?.defaultView.hidden = true
                    self?.tableView.reloadData()
                }
            })
        }
    }
    /// 是否正在拉取数据
    var isFetching: Bool = false
    
    
    // MARK: - Components
    /// 我的评价缺省面板
    private lazy var defaultView: UIView = {
        let view = MalaDefaultPanel()
        view.imageName = "comment_noData"
        view.text = "当前暂无评价"
        view.descText = "上完课后再来这里吧"
        view.hidden = true
        return view
    }()
    /// 下拉刷新视图
    private lazy var refresher: UIRefreshControl = {
        let refresher = UIRefreshControl()
        refresher.addTarget(self, action: #selector(CommentViewController.loadCourse), forControlEvents: .ValueChanged)
        return refresher
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupUserInterface()
        configure()
        loadCourse()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Private Method
    private func configure() {
        tableView.backgroundColor = MalaColor_EDEDED_0
        tableView.separatorStyle = .None
        tableView.contentInset = UIEdgeInsets(top: 6, left: 0, bottom: 6, right: 0)
        refreshControl = refresher
        tableView.registerClass(CommentViewCell.self, forCellReuseIdentifier: CommentViewCellReuseId)
    }
    
    private func setupUserInterface() {
        // SubViews
        tableView.addSubview(defaultView)
        
        // AutoLayout
        defaultView.snp_makeConstraints { (make) -> Void in
            make.size.equalTo(tableView.snp_size)
            make.center.equalTo(tableView.snp_center)
        }
    }
    
    ///  获取学生课程信息
    @objc private func loadCourse() {
        
        // 屏蔽[正在刷新]时的操作
        guard isFetching == false else {
            return
        }
        isFetching = true
        
        refreshControl?.beginRefreshing()
        
        
        ///  获取学生课程信息
        getStudentCourseTable(failureHandler: { [weak self] (reason, errorMessage) -> Void in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("CommentViewController - loadCourse Error \(errorMessage)")
            }
            // 显示缺省值
            self?.models = []
            self?.refreshControl?.endRefreshing()
            self?.isFetching = false
        }, completion: { [weak self] (courseList) -> Void in
            
            self?.refreshControl?.endRefreshing()
            self?.isFetching = false
            guard let courseList = courseList else {
                println("学生上课时间表为空！")
                return
            }
            self?.models = courseList
        })
    }
    
    
    // MARK: - Delegate
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 188
    }
    
    
    // MARK: - DataSource
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.models.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(CommentViewCellReuseId, forIndexPath: indexPath) as! CommentViewCell
        cell.selectionStyle = .None
        cell.model = self.models[indexPath.row]
        return cell
    }
}