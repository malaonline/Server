//
//  FilterResultController.swift
//  mala-ios
//
//  Created by 王新宇 on 1/20/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class FilterResultController: UIViewController {

    // MARK: - Property
    weak var filterCondition: ConditionObject? {
        didSet {
            self.filterBar.filterCondition = filterCondition
        }
    }
    
    
    // MARK: - Components
    private lazy var tableView: TeacherTableView = {
        let tableView = TeacherTableView(frame: self.view.frame, style: .Plain)
        tableView.controller = self
        return tableView
    }()
    private lazy var filterBar: FilterBar = {
        let filterBar = FilterBar(frame: CGRectZero)
        filterBar.backgroundColor = MalaTeacherCellBackgroundColor
        return filterBar
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
        self.view.backgroundColor = MalaTeacherCellBackgroundColor
        self.tableView.contentInset = UIEdgeInsets(top: 0, left: 0, bottom: 4, right: 0)
        
        // SubViews
        self.view.addSubview(filterBar)
        self.view.addSubview(tableView)
        
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
        
        // 设置BarButtomItem间隔
        let spacer = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
        spacer.width = -MalaLayout_Margin_5*2.3
        
        // leftBarButtonItem
        let leftBarButtonItem = UIBarButtonItem(customView:
            UIButton(
                imageName: "leftArrow_normal",
                highlightImageName: "leftArrow_press",
                target: self,
                action: "popSelf"
            )
        )
        navigationItem.leftBarButtonItems = [spacer, leftBarButtonItem]
    }
    
    @objc private func popSelf() {
        self.navigationController?.popViewControllerAnimated(true)
    }
}


class FilterBar: UIView {
    
    // MARK: - Property
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
            borderColor: MalaFilterHeaderBorderColor,
            target: self,
            action: "buttonDidTap:"
        )
        gradeButton.tag = 1
        return gradeButton
    }()
    private lazy var subjectButton: UIButton = {
        let subjectButton = UIButton(
            title: "科  目",
            borderColor: MalaFilterHeaderBorderColor,
            target: self,
            action: "buttonDidTap:"
        )
        subjectButton.tag = 2
        return subjectButton
    }()
    private lazy var styleButton: UIButton = {
        let styleButton = UIButton(
            title: "不  限",
            borderColor: MalaFilterHeaderBorderColor,
            target: self,
            action: "buttonDidTap:"
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
            make.top.equalTo(self.snp_top).offset(MalaLayout_Margin_9)
            make.left.equalTo(self.snp_left).offset(MalaLayout_Margin_12)
            make.width.equalTo(88)
            make.bottom.equalTo(self.snp_bottom).offset(-MalaLayout_Margin_5)
        }
        subjectButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.gradeButton.snp_top)
            make.left.equalTo(self.gradeButton.snp_right).offset(MalaLayout_Margin_7)
            make.width.equalTo(54)
            make.height.equalTo(gradeButton.snp_height)
        }
        styleButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.subjectButton.snp_top)
            make.left.equalTo(self.subjectButton.snp_right).offset(MalaLayout_Margin_7)
            make.right.equalTo(self.snp_right).offset(-MalaLayout_Margin_12)
            make.height.equalTo(self.subjectButton.snp_height)
        }
    }
    
    private func setupNotification() {
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_CommitCondition,
            object: nil,
            queue: nil) { [weak self] (notification) -> Void in
                self?.filterCondition = notification.object as? ConditionObject
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
                subject.name = MalaSubject[i.integerValue]
                return subject
            })
        
        let alertView = ThemeAlert(contentView: filterView)
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
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_CommitCondition, object: nil)
    }
}