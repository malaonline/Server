//
//  FavoriteViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/8/2.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

private let FavoriteViewCellReuseId = "FavoriteViewCellReuseId"
private let FavoriteViewLoadmoreCellReusedId = "FavoriteViewLoadmoreCellReusedId"

class FavoriteViewController: BaseTableViewController {

    private enum Section: Int {
        case Teacher
        case LoadMore
    }
    
    // MARK: - Property
    /// 收藏老师模型列表
    var models: [TeacherModel] = [] {
        didSet {
            dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                if self?.models.count == 0 {
                    self?.showDefaultView()
                }else {
                    self?.hideDefaultView()
                    self?.tableView.reloadData()
                }
            })
        }
    }
    /// 是否正在拉取数据
    var isFetching: Bool = false
    /// 当前显示页数
    var currentPageIndex = 1
    /// 所有老师数据总量
    var allOrderFormCount = 0
    
    
    // MARK: - Components
    /// 下拉刷新视图
    private lazy var refresher: UIRefreshControl = {
        let refresher = UIRefreshControl()
        refresher.addTarget(self, action: #selector(FavoriteViewController.loadFavoriteTeachers), forControlEvents: .ValueChanged)
        return refresher
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        configure()
        loadFavoriteTeachers()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    

    // MARK: - Private Method
    private func configure() {
        title = "我的收藏"
        defaultView.imageName = "collect_noData"
        defaultView.text = "您收藏的老师会出现在这里哦"
        defaultView.descText = "快去老师详情页收藏吧"
        
        tableView.backgroundColor = MalaColor_EDEDED_0
        tableView.separatorStyle = .None
        tableView.estimatedRowHeight = 200
        refreshControl = refresher
        
        tableView.registerClass(TeacherTableViewCell.self, forCellReuseIdentifier: FavoriteViewCellReuseId)
        tableView.registerClass(ThemeReloadView.self, forCellReuseIdentifier: FavoriteViewLoadmoreCellReusedId)
    }
    
    
    @objc private func loadFavoriteTeachers(page: Int = 1,  isLoadMore: Bool = false, finish: (()->())? = nil) {
        
        
        // 屏蔽[正在刷新]时的操作
        guard isFetching == false else {
            return
        }
        isFetching = true
        refreshControl?.beginRefreshing()
        
        if isLoadMore {
            currentPageIndex += 1
        }else {
            currentPageIndex = 1
        }
        
        getFavoriteTeachers(page, failureHandler: { [weak self] (reason, errorMessage) in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("FavoriteViewController - loadFavoriteTeachers Error \(errorMessage)")
            }
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                // 显示缺省值
                self?.refreshControl?.endRefreshing()
                self?.isFetching = false
            })
        }, completion: { [weak self] (teachers, count) in
            println("收藏列表 － \(teachers)")
            
            /// 记录数据量
            if count != 0 {
                self?.allOrderFormCount = count
            }
            ///  加载更多
            if isLoadMore {
                for teacher in teachers {
                    self?.models.append(teacher)
                }
                ///  如果不是加载更多，则刷新数据
            }else {
                self?.models = teachers
            }
            
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                finish?()
                self?.refreshControl?.endRefreshing()
                self?.isFetching = false
            })
        })
    }
    
    
    // MARK: - Delegate
    override func tableView(tableView: UITableView, willDisplayCell cell: UITableViewCell, forRowAtIndexPath indexPath: NSIndexPath) {
        
        switch indexPath.section {
            
        case Section.Teacher.rawValue:
            break
            
        case Section.LoadMore.rawValue:
            if let cell = cell as? ThemeReloadView {
                println("load more orderForm")
                
                if !cell.activityIndicator.isAnimating() {
                    cell.activityIndicator.startAnimating()
                }
                
                loadFavoriteTeachers(isLoadMore: true, finish: {
                    cell.activityIndicator.stopAnimating()
                })
            }
            
        default:
            break
        }
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let teacherId = (tableView.cellForRowAtIndexPath(indexPath) as! TeacherTableViewCell).model!.id
        let viewController = TeacherDetailsController()
        viewController.teacherID = teacherId
        viewController.hidesBottomBarWhenPushed = true
        navigationController?.pushViewController(viewController, animated: true)
    }
    
    
    // MARK: - DataSource
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 2
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        switch section {
            
        case Section.Teacher.rawValue:
            return models.count ?? 0
            
        case Section.LoadMore.rawValue:
            if allOrderFormCount == models.count {
                return 0
            }else {
                return models.isEmpty ? 0 : 1
            }
            
        default:
            return 0
        }
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        switch indexPath.section {
            
        case Section.Teacher.rawValue:
            let cell = tableView.dequeueReusableCellWithIdentifier(FavoriteViewCellReuseId, forIndexPath: indexPath) as! TeacherTableViewCell
            cell.model = models[indexPath.row]
            return cell
            
        case Section.LoadMore.rawValue:
            let cell = tableView.dequeueReusableCellWithIdentifier(FavoriteViewLoadmoreCellReusedId, forIndexPath: indexPath) as! ThemeReloadView
            return cell
            
        default:
            return UITableViewCell()
        }
    }
    
}