//
//  FilterBar.swift
//  mala-ios
//
//  Created by 王新宇 on 16/6/29.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class FilterBar: UIView {
    
    // MARK: - Property
    /// 父控制器
    weak var controller: FilterResultController?
    /// 筛选条件
    var filterCondition: ConditionObject = MalaCondition {
        didSet {
            self.gradeButton.setTitle(filterCondition.grade.name, forState: .Normal)
            self.subjectButton.setTitle(filterCondition.subject.name, forState: .Normal)
            let tags = filterCondition.tags.map({ (object: BaseObjectModel) -> String in
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
        filterCondition = MalaCondition
        
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
                self?.filterCondition = MalaCondition
                self?.controller?.loadTeachersWithCommonCondition()
        }
    }
    
    
    // MARK: - Event Response
    @objc private func buttonDidTap(sender: UIButton) {
        
        let filterView = FilterView(frame: CGRectZero)
        filterView.isSecondaryFilter = true
        filterView.subjects = MalaCondition.grade.subjects.map({ (i: NSNumber) -> GradeModel in
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