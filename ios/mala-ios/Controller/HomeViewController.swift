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
    private var condition: ConditionObject?
    private var filterResultDidShow: Bool = false
    
    
    // MARK: - Components
    private lazy var tableView: TeacherTableView = {
        let tableView = TeacherTableView(frame: self.view.frame, style: .Plain)
        tableView.controller = self
        return tableView
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupNotification()
        setupUserInterface()
        loadTeachers()
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
        // self.tableView.tableHeaderView = FilterHeaderView(frame: CGRect(x: 0, y: 0, width: 0, height: 26))
        
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
        spacer.width = -MalaLayout_Margin_5*2.3
        
        // leftBarButtonItem
        let leftBarButtonItem = UIBarButtonItem(customView:
            UIButton(
                title: "洛阳",
                imageName: "location_normal",
                highlightImageName: "location_press",
                target: self,
                action: "locationButtonDidClick"
            )
        )
        navigationItem.leftBarButtonItems = [spacer, leftBarButtonItem]
        
        // rightBarButtonItem
        let rightBarButtonItem = UIBarButtonItem(customView:
            UIButton(
                imageName: "filter_normal",
                highlightImageName: "filter_press",
                target: self,
                action: "filterButtonDidClick"
            )
        )
        navigationItem.rightBarButtonItems = [spacer, rightBarButtonItem]
    }
    
    private func loadTeachers(filters: [String: AnyObject]? = nil) {
        
        // 开启下拉刷新
        self.tableView.startPullToRefresh()
        
        NetworkTool.sharedTools.loadTeachers(filters) { [weak self] result, error in
            if error != nil {
                debugPrint("HomeViewController - loadTeachers Request Error")
                return
            }
            guard let dict = result as? [String: AnyObject] else {
                debugPrint("HomeViewController - loadTeachers Format Error")
                return
            }
            
            // 结束下拉刷新
            self?.tableView.startPullToRefresh()
            
            self?.tableView.teachers = []
            let resultModel = ResultModel(dict: dict)
            if resultModel.results != nil {
                for object in ResultModel(dict: dict).results! {
                    if let dict = object as? [String: AnyObject] {
                        self?.tableView.teachers!.append(TeacherModel(dict: dict))
                    }
                }
            }
            self?.tableView.reloadData()
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
        ThemeAlert(contentView: FilterView(frame: CGRectZero)).show()
    }
    
    
    deinit {
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_CommitCondition, object: nil)
    }
    
//    @objc private func profileButtonDidClick() {
//        self.navigationController?.presentViewController(
//            UINavigationController(rootViewController: LoginViewController()),
//            animated: true,
//            completion: { () -> Void in
//            
//        })
//    }
}
