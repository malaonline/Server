//
//  HomeViewController.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

private let TeacherTableViewCellReusedId = "TeacherTableViewCellReusedId"

class HomeViewController: UIViewController {
    
    // MARK: - Property
    /// 筛选条件
    private var condition: ConditionObject?
    private var filterResultDidShow: Bool = false
    /// 当前显示页数
    var currentPageIndex = 1
    /// 所有老师数据总量
    var allTeacherCount = 0
    
    
    // MARK: - Components
    /// 老师信息tableView
    private lazy var tableView: TeacherTableView = {
        let tableView = TeacherTableView(frame: self.view.frame, style: .Plain)
        tableView.controller = self
        // 底部Tabbar留白
        tableView.contentInset = UIEdgeInsets(top: 6, left: 0, bottom: 48 + 6, right: 0)
        return tableView
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupNotification()
        setupUserInterface()
        getCurrentLocation()
        
        // 开启下拉刷新
        self.tableView.startPullToRefresh() //loadTeachers()
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        makeStatusBarBlack()
        filterResultDidShow = false
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Private Method
    private func setupNotification() {
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_CommitCondition,
            object: nil,
            queue: nil) { [weak self] (notification) -> Void in
                if !(self?.filterResultDidShow ?? false) {
                    self?.filterResultDidShow = true
                    self?.condition = notification.object as? ConditionObject
                    self?.resolveFilterCondition()
                }
        }
    }
    
    private func setupUserInterface() {
        // Style
        self.title = MalaCommonString_Malalaoshi
        self.tabBarItem.title = MalaCommonString_FindTeacher
        
        // 下拉刷新组件
        self.tableView.addPullToRefresh({ [weak self] in
            self?.loadTeachers()
            })
        
        // SubViews
        self.view.addSubview(tableView)
        
        // Autolayout
        tableView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.view.snp_top)
            make.left.equalTo(self.view.snp_left)
            make.bottom.equalTo(self.view.snp_bottom)
            make.right.equalTo(self.view.snp_right)
        }
        
        // 设置BarButtomItem间隔
        let spacer = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
        spacer.width = -16
        
        // leftBarButtonItem
        let leftBarButtonItem = UIBarButtonItem(customView:
            UIButton(
                title: "许昌",
                imageName: "location_normal",
                highlightImageName: "location_press",
                target: self,
                action: #selector(HomeViewController.profileButtonDidClick)
            )
        )
        navigationItem.leftBarButtonItems = [spacer, leftBarButtonItem]
        
        // rightBarButtonItem
        let rightBarButtonItem = UIBarButtonItem(customView:
            UIButton(
                imageName: "filter_normal",
                highlightImageName: "filter_press",
                target: self,
                action: #selector(HomeViewController.filterButtonDidClick)
            )
        )
        navigationItem.rightBarButtonItems = [spacer, rightBarButtonItem]
    }
    
    ///  获取当前地理位置信息
    private func getCurrentLocation() {
        proposeToAccess(.Location(.WhenInUse), agreed: {
            
            MalaLocationService.turnOn()
            }, rejected: {
                self.alertCanNotAccessLocation()
        })
    }
    
    func loadTeachers(filters: [String: AnyObject]? = nil, isLoadMore: Bool = false, finish: (()->())? = nil) {
        
        if isLoadMore {
            currentPageIndex += 1
        }else {
            currentPageIndex = 1
        }
        
        MalaNetworking.sharedTools.loadTeachers(filters, page: currentPageIndex) { [weak self] result, error in
            if error != nil {
                println("HomeViewController - loadTeachers Request Error")
                return
            }
            guard let dict = result as? [String: AnyObject] else {
                println("HomeViewController - loadTeachers Format Error")
                return
            }
            
            let resultModel = ResultModel(dict: dict)
            
            /// 记录数据量
            if let count = resultModel.count where count != 0 {
                self?.allTeacherCount = count.integerValue
            }
            
            /// 若请求数达到最大, 执行return
            if let detail = resultModel.detail where (detail as NSString).containsString(MalaErrorDetail_InvalidPage) {
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    finish?()
                })
                return
            }
            
            if isLoadMore {
                
                ///  加载更多
                
                if resultModel.results != nil {
                    for object in ResultModel(dict: dict).results! {
                        if let dict = object as? [String: AnyObject] {
                            self?.tableView.teachers.append(TeacherModel(dict: dict))
                        }
                    }
                }
            }else {
                
                ///  如果不是加载更多，则刷新数据
                self?.tableView.teachers = []
                /// 解析数据
                if resultModel.results != nil {
                    for object in ResultModel(dict: dict).results! {
                        if let dict = object as? [String: AnyObject] {
                            self?.tableView.teachers.append(TeacherModel(dict: dict))
                        }
                    }
                }
                self?.tableView.reloadData()
            }
            
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                finish?()
            })
        }
    }
    
    private func resolveFilterCondition() {
        let viewController = FilterResultController()
        viewController.filterCondition = self.condition
        navigationController?.pushViewController(viewController, animated: true)
    }
    
    
    // MARK: - Event Response
    @objc private func locationButtonDidClick() {
        //TODO:定位功能代码
    }

    @objc private func filterButtonDidClick() {
        TeacherFilterPopupWindow(contentView: FilterView(frame: CGRectZero)).show()
    }
    
    
    deinit {
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_CommitCondition, object: nil)
    }
    
    @objc private func profileButtonDidClick() {
        _ = JSSAlertView().show(self,
            title: "目前只支持许昌，其他地区正在拓展中...",
            buttonText: "我知道了",
            iconImage: UIImage(named: "alert_Position")
        )
    }
}