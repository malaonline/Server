//
//  HomeViewController.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

private let TeacherTableViewCellReusedId = "TeacherTableViewCellReusedId"

class HomeViewController: UITableViewController, DropViewDelegate {
    
    // MARK: - Property
    private lazy var teachers: [TeacherModel]? = TestFactory.TeacherList()
    
    
    // MARK: - Components
    private lazy var dropView: DropView = {
        let filterView = TeacherFilterView(frame: CGRectZero, collectionViewLayout: CommonFlowLayout(type: .FilterView))
        let dropView = DropView(frame: CGRect(x: 0, y: 64-MalaContentHeight, width: MalaScreenWidth, height: MalaContentHeight), viewController: self, contentView: filterView)
        dropView.delegate = self
        return dropView
    }()
    
    
    // MARK: - Consturcted
    override init(style: UITableViewStyle) {
        super.init(style: style)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // loadTeachers() //TODO:恢复真实网络数据请求
        setupUserInterface()
        tableView.registerClass(TeacherTableViewCell.self, forCellReuseIdentifier: TeacherTableViewCellReusedId)
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        makeStatusBarBlack()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Delegate
    override func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }

    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let teacherId = (tableView.cellForRowAtIndexPath(indexPath) as! TeacherTableViewCell).model!.id
        
        // Request Teacher Info
        NetworkTool.sharedTools.loadTeacherDetail(teacherId, finished: {[weak self] (result, error) -> () in
            if error != nil {
                debugPrint("HomeViewController - loadTeacherDetail Request Error")
                return
            }
            guard let dict = result as? [String: AnyObject] else {
                debugPrint("HomeViewController - loadTeacherDetail Format Error")
                return
            }
            
            let viewController = TeacherDetailsController()
            let model = TestFactory.TeacherDetailsModel() //TODO: Remove TestModel
            
            viewController.model = model   // TeacherDetailModel(dict: dict)
            viewController.hidesBottomBarWhenPushed = true
            self?.navigationController?.pushViewController(viewController, animated: true)
            })
    }
    
    func dropViewDidTapButtonForContentView(contentView: UIView) {
        // 获取筛选条件
        let filterObj: ConditionObject = (contentView as! TeacherFilterView).filterObject
        let filters: [String: AnyObject] = ["grade": filterObj.grade.id, "subject": filterObj.subject.id, "tags": filterObj.tag.id]
        loadTeachers(filters)
        dropView.dismiss()
    }
    
    
    // MARK: - DataSource
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.teachers?.count ?? 0
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(TeacherTableViewCellReusedId, forIndexPath: indexPath) as! TeacherTableViewCell
        cell.selectionStyle = .None
        cell.model = teachers![indexPath.row]
        return cell
    }
    
    
    // MARK: - private Method
    private func setupUserInterface() {
        tableView.backgroundColor = MalaTeacherCellBackgroundColor
        tableView.estimatedRowHeight = 200
        tableView.separatorStyle = .None
        
        // 设置BarButtomItem间隔
        let spacer = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
        spacer.width = -MalaLayout_Margin_5*2.3
        
        // leftBarButtonItem
        let leftBarButtonItem = UIBarButtonItem(customView:
            UIButton(
                title: "洛阳",
                imageName: "location_normal",
                selectImageName: "location_press",
                target: self,
                action: "locationButtonDidClick"
            )
        )
        navigationItem.leftBarButtonItems = [spacer, leftBarButtonItem]
        
        // rightBarButtonItem
        let rightBarButtonItem = UIBarButtonItem(customView:
            UIButton(
                imageName: "profile_normal",
                selectImageName: "location_press",
                target: self,
                action: "profileButtonDidClick"
            )
        )
        navigationItem.rightBarButtonItems = [spacer, rightBarButtonItem]
    }
    
    private func loadTeachers(filters: [String: AnyObject]? = nil) {
        NetworkTool.sharedTools.loadTeachers(filters) { result, error in
            if error != nil {
                debugPrint("HomeViewController - loadTeachers Request Error")
                return
            }
            guard let dict = result as? [String: AnyObject] else {
                debugPrint("HomeViewController - loadTeachers Format Error")
                return
            }
            
            self.teachers = []
            let resultModel = ResultModel(dict: dict)
            if resultModel.results != nil {
                for object in ResultModel(dict: dict).results! {
                    if let dict = object as? [String: AnyObject] {
                        self.teachers!.append(TeacherModel(dict: dict))
                    }
                }
            }
            self.tableView.reloadData()
        }
    }
    
    
    // MARK: - Event Response
    @objc private func locationButtonDidClick() {
        dropView.isShow ? dropView.dismiss() : dropView.show()
        
    }
    
    @objc private func profileButtonDidClick() {
        self.navigationController?.presentViewController(
            UINavigationController(rootViewController: LoginViewController()),
            animated: true,
            completion: { () -> Void in
            
        })
    }
}
