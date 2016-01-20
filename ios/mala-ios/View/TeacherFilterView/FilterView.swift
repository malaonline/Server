//
//  FilterCollectionView.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class FilterView: UIScrollView, UIScrollViewDelegate {
    
    // MARK: - Property
    /// 父容器
    weak var container: ThemeAlert?
    /// 年级及下属科目数据源
    var grades: [GradeModel]? = nil {
        didSet {
            self.gradeView.grades = grades
        }
    }
    /// 当前显示科目数据源
    var subjects: [GradeModel]? = nil {
        didSet {
            self.subjectView.subjects = subjects
        }
    }
    /// 风格数据源
    var tags: [BaseObjectModel]? = nil {
        didSet {
            self.styleView.tagsModel = tags
        }
    }
    /// 当前筛选条件记录模型
    lazy var filterObject: ConditionObject = {
        let object = ConditionObject()
        object.subject = GradeModel()
        object.tags = []
        return object
    }()
    /// 当前显示面板下标标记
    var currentIndex: Int = 1
    
    
    // MARK: - Components
    /// 年级筛选面板
    private lazy var gradeView: GradeFilterView = {
        let gradeView = GradeFilterView(frame: CGRectZero,
            collectionViewLayout: CommonFlowLayout(type: .FilterView),
            didTapCallBack: { (model) -> () in
                self.filterObject.grade = model! //TODO: 注意测试此处（包括下方两处）强行解包，目前个人认为此处强行解包不会出现问题
                self.scrollToPanel(2)
                // 根据所选年级，加载对应的科目
                self.subjects = model!.subjects.map({ (i: NSNumber) -> GradeModel in
                    let subject = GradeModel()
                    subject.id = i.integerValue
                    subject.name = MalaSubject[i.integerValue]
                    return subject
                })
        })
        return gradeView
    }()
    /// 科目筛选面板
    private lazy var subjectView: SubjectFilterView = {
        let subjectView = SubjectFilterView(frame: CGRectZero,
            collectionViewLayout: CommonFlowLayout(type: .SubjectView),
            didTapCallBack: { (model) -> () in
                self.filterObject.subject = model!
                self.scrollToPanel(3)
        })
        return subjectView
    }()
    /// 风格筛选面板
    private lazy var styleView: StyleFilterView = {
        let styleView = StyleFilterView(frame: CGRect(x: 0, y: 0, width: MalaLayout_FilterContentWidth, height: MalaLayout_FilterContentWidth), tags: [])
        return styleView
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        configuration()
        registerNotification()
        setupUserInterface()
        loadFilterCondition()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configuration() {
        self.contentSize = CGSize(width: 0, height: MalaLayout_FilterContentWidth-3)
        self.delegate = self
        self.bounces = false
        self.showsHorizontalScrollIndicator = false
    }
    
    private func registerNotification() {
        // pop页面通知处理
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_PopFilterView,
            object: nil,
            queue: nil
            ) { [weak self] (notification) -> Void in
                // pop当前面板
                self?.scrollToPanel((self?.currentIndex ?? 2) - 1)
        }
        
        // 确认按钮点击通知处理
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_ConfirmFilterView,
            object: nil,
            queue: nil) { [weak self] (notification) -> Void in
                // 提交筛选条件
                // 将选中字符串数组遍历为对象数组
                let tagsCondition = self?.styleView.selectedItems.map({ (string: String) -> BaseObjectModel in
                    var tagObject = BaseObjectModel()
                    for object in self?.tags ?? [] {
                        if object.name == string {
                            tagObject = object
                        }
                    }
                    return tagObject
                })
                self?.filterObject.tags = tagsCondition ?? []
        }
    }
    
    private func setupUserInterface() {
        // SubViews
        addSubview(gradeView)
        addSubview(subjectView)
        addSubview(styleView)
        
        // Autolayout
        gradeView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.snp_top)
            make.left.equalTo(self.snp_left)
            make.width.equalTo(self.snp_width)
            make.height.equalTo(self.snp_height)
        }
        subjectView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.snp_top)
            make.left.equalTo(self.snp_left).offset(MalaLayout_FilterContentWidth)
            make.width.equalTo(self.snp_width)
            make.height.equalTo(self.snp_height)
        }
        styleView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.snp_top)
            make.left.equalTo(subjectView.snp_left).offset(MalaLayout_FilterContentWidth)
            make.width.equalTo(self.snp_width)
            make.height.equalTo(self.snp_height)
        }
        self.backgroundColor = UIColor.lightGrayColor()
    }
    
    ///  滚动到指定面板
    ///
    ///  - parameter page: 面板下标，起始为1
    private func scrollToPanel(page: Int) {
        switch page {
        case 1:
            // 当前显示View为 年级筛选
            currentIndex = 1
            container?.tTitle = "筛选年级"
            container?.tIcon = "grade"
            container?.setButtonStatus(showClose: true, showCancel: false, showConfirm: false)
            setContentOffset(CGPoint(x: 0, y: 0), animated: true)
        case 2:
            // 当前显示View为 科目筛选
            currentIndex = 2
            container?.tTitle = "筛选科目"
            container?.tIcon = "subject"
            container?.setButtonStatus(showClose: false, showCancel: true, showConfirm: false)
            setContentOffset(CGPoint(x: MalaLayout_FilterContentWidth, y: 0), animated: true)
        case 3:
            // 当前显示View为 风格筛选
            currentIndex = 3
            container?.tTitle = "筛选风格"
            container?.tIcon = "style"
            container?.setButtonStatus(showClose: false, showCancel: true, showConfirm: true)
            setContentOffset(CGPoint(x: MalaLayout_FilterContentWidth*2, y: 0), animated: true)
        default:
            break
        }
    }
    
    private func loadFilterCondition() {
        // 读取年级和科目数据
        var dataArray = NSArray(contentsOfFile: NSBundle.mainBundle().pathForResource("FilterCondition.plist", ofType: nil)!) as? [AnyObject]
        var gradeDict: [GradeModel]? = []
        for object in dataArray! {
            if let dict = object as? [String: AnyObject] {
                let set = GradeModel(dict: dict)
                gradeDict?.append(set)
            }
        }
        self.grades = gradeDict
        
        // 设置默认科目数据
        let subjects = grades![2].subjects.map({ (i: NSNumber) -> GradeModel in
            let subject = GradeModel()
            subject.id = i.integerValue
            subject.name = MalaSubject[i.integerValue]
            return subject
        })
        self.subjectView.subjects = subjects
        
        // 获取风格标签
        NetworkTool.sharedTools.loadTags{ [weak self] (result, error) -> () in
            if error != nil {
                debugPrint("TeacherFilterView - loadTags Request Error")
                return
            }
            guard let dict = result as? [String: AnyObject] else {
                debugPrint("TeacherFilterView - loadTags Format Error")
                return
            }
            
            var tagsDict: [BaseObjectModel]? = []
            dataArray = ResultModel(dict: dict).results
            for object in dataArray! {
                if let dict = object as? [String: AnyObject] {
                    let set = BaseObjectModel(dict: dict)
                    tagsDict?.append(set)
                }
            }
            self?.tags = tagsDict
        }
    }
    
    deinit {
        // 移除观察者
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_PopFilterView, object: nil)
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_ConfirmFilterView, object: nil)
    }
}


// MARK: - Condition Object
class ConditionObject: NSObject {
    var grade: GradeModel = GradeModel()
    var subject: GradeModel = GradeModel()
    var tags: [BaseObjectModel] = []
}
