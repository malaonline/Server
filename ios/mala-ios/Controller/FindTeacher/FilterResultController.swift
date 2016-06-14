//
//  FilterResultController.swift
//  mala-ios
//
//  Created by 王新宇 on 1/20/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class FilterResultController: BaseViewController {

    // MARK: - Property
    weak var filterCondition: ConditionObject? {
        didSet {
            self.filterBar.filterCondition = filterCondition
            loadTeachers(filterCondition?.getParam())
        }
    }
    /// 当前显示页数
    var currentPageIndex = 1
    /// 所有老师数据总量
    var allTeacherCount = 0
    
    
    // MARK: - Components
    /// 老师列表
    private lazy var tableView: TeacherTableView = {
        let tableView = TeacherTableView(frame: self.view.frame, style: .Plain)
        tableView.controller = self
        return tableView
    }()
    /// 筛选条件面板
    private lazy var filterBar: FilterBar = {
        let filterBar = FilterBar(frame: CGRectZero)
        filterBar.backgroundColor = MalaColor_EDEDED_0
        filterBar.controller = self
        return filterBar
    }()
    /// 无筛选结果缺省面板
    private lazy var defaultView: UIView = {
        let defaultView = MalaDefaultPanel()
        defaultView.imageName = "filter_no_result"
        defaultView.text = "请重新设定筛选条件！"
        defaultView.hidden = true
        return defaultView
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setupUserInterface()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        makeStatusBarBlack()
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // style
        self.title = MalaCommonString_FilterResult
        self.view.backgroundColor = MalaColor_EDEDED_0
        self.tableView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: 4, right: 0)
        
        // SubViews
        self.view.addSubview(filterBar)
        self.view.addSubview(tableView)
        self.view.addSubview(defaultView)
        
        // AutoLayout
        filterBar.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.view.snp_top)
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
            make.height.equalTo(MalaLayout_FilterBarHeight)
        }
        tableView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.filterBar.snp_bottom)
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
            make.bottom.equalTo(self.view.snp_bottom)
        }
        defaultView.snp_makeConstraints { (make) -> Void in
            make.size.equalTo(tableView.snp_size)
            make.center.equalTo(tableView.snp_center)
        }
    }
    
    private func showDefatultViewWhenModelIsEmpty() {
        if tableView.teachers.count == 0 {
            defaultView.hidden = false
        }else {
            defaultView.hidden = true
        }
    }
    
    ///  根据筛选条件字典，请求老师列表
    ///
    ///  - parameter filters: 筛选条件字典
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
            
            dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                self?.showDefatultViewWhenModelIsEmpty()
                finish?()
            })
        }
    }
    

    deinit {
        println("FilterResultController - Deinit")
        // 重置选择条件模型
        MalaFilterIndexObject = filterSelectedIndexObject()
    }
}


class FilterBar: UIView {
    
    // MARK: - Property
    /// 父控制器
    weak var controller: FilterResultController?
    /// 筛选条件
    var filterCondition: ConditionObject? {
        didSet {
            self.gradeButton.setTitle(filterCondition?.grade.name, forState: .Normal)
            self.subjectButton.setTitle(filterCondition?.subject.name, forState: .Normal)
            let tags = filterCondition?.tags.map({ (object: BaseObjectModel) -> String in
                return object.name ?? ""
            })
            let tagsButtonTitle = (tags ?? ["不限"]).joinWithSeparator(" • ")
            self.styleButton.setTitle(tagsButtonTitle == "" ? "不限" : tagsButtonTitle, forState: .Normal)
        }
    }
    
    
    // MARK: - Components
    private lazy var gradeButton: UIButton = {
        let gradeButton = UIButton(
            title: "小学一年级",
            borderColor: MalaColor_8FBCDD_0,
            target: self,
            action: #selector(FilterBar.buttonDidTap(_:))
        )
        gradeButton.tag = 1
        return gradeButton
    }()
    private lazy var subjectButton: UIButton = {
        let subjectButton = UIButton(
            title: "科  目",
            borderColor: MalaColor_8FBCDD_0,
            target: self,
            action: #selector(FilterBar.buttonDidTap(_:))
        )
        subjectButton.tag = 2
        return subjectButton
    }()
    private lazy var styleButton: UIButton = {
        let styleButton = UIButton(
            title: "不  限",
            borderColor: MalaColor_8FBCDD_0,
            target: self,
            action: #selector(FilterBar.buttonDidTap(_:))
        )
        styleButton.titleLabel?.lineBreakMode = .ByTruncatingTail
        styleButton.titleEdgeInsets = UIEdgeInsets(top: 0, left: 13, bottom: 0, right: 13)
        styleButton.tag = 3
        return styleButton
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUserInterface()
        setupNotification()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
 
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        
        // SubViews
        self.addSubview(gradeButton)
        self.addSubview(subjectButton)
        self.addSubview(styleButton)
        
        // Autolayout
        gradeButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.snp_top).offset(9)
            make.left.equalTo(self.snp_left).offset(12)
            make.width.equalTo(88)
            make.bottom.equalTo(self.snp_bottom).offset(-5)
        }
        subjectButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.gradeButton.snp_top)
            make.left.equalTo(self.gradeButton.snp_right).offset(7)
            make.width.equalTo(54)
            make.height.equalTo(gradeButton.snp_height)
        }
        styleButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.subjectButton.snp_top)
            make.left.equalTo(self.subjectButton.snp_right).offset(7)
            make.right.equalTo(self.snp_right).offset(-12)
            make.height.equalTo(self.subjectButton.snp_height)
        }
    }
    
    private func setupNotification() {
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_CommitCondition,
            object: nil,
            queue: nil) { [weak self] (notification) -> Void in
                self?.filterCondition = notification.object as? ConditionObject
                self?.controller?.filterCondition = self?.filterCondition
        }
    }
    
    
    // MARK: - Event Response
    @objc private func buttonDidTap(sender: UIButton) {
        
        let filterView = FilterView(frame: CGRectZero)
        filterView.filterObject = self.filterCondition ?? ConditionObject()
        filterView.isSecondaryFilter = true
        filterView.subjects = self.filterCondition?.grade.subjects.map({ (i: NSNumber) -> GradeModel in
                let subject = GradeModel()
                subject.id = i.integerValue
                subject.name = MalaConfig.malaSubject()[i.integerValue]
                return subject
            })
        
        let alertView = TeacherFilterPopupWindow(contentView: filterView)
        alertView.closeWhenTap = true
        
        switch sender.tag {
        case 1:
            filterView.scrollToPanel(1, animated: false)
            filterView.container?.setButtonStatus(showClose: false, showCancel: false, showConfirm: false)
        case 2:
            filterView.scrollToPanel(2, animated: false)
            filterView.container?.setButtonStatus(showClose: false, showCancel: false, showConfirm: false)
        case 3:
            filterView.scrollToPanel(3, animated: false)
            filterView.container?.setButtonStatus(showClose: false, showCancel: false, showConfirm: true)
        default:
            break
        }
        
        alertView.show()
    }
    
    deinit {
        
        println("FilterBar - Deinit")
        
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_CommitCondition, object: nil)
    }
}