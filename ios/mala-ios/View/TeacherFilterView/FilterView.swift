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
    weak var container: TeacherFilterPopupWindow?
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
    var filterObject: ConditionObject = {
        let object = ConditionObject()
        object.subject = GradeModel()
        object.tags = []
        return object
    }() {
        didSet {
            
        }
    }
    /// 当前显示面板下标标记
    var currentIndex: Int = 1
    /// 二级选择标识
    var isSecondaryFilter: Bool = false
    
    
    // MARK: - Components
    /// 年级筛选面板
    private lazy var gradeView: GradeFilterView = {
        let gradeView = GradeFilterView(frame: CGRectZero,
            collectionViewLayout: CommonFlowLayout(type: .FilterView),
            didTapCallBack: { [weak self] (model) -> () in
                self?.filterObject.grade = model!
                
                // 如果为一次筛选
                if !(self?.isSecondaryFilter ?? false) {
                    self?.scrollToPanel(2, animated: true)
                    
                    // 根据所选年级，加载对应的科目
                    self?.subjects = model!.subjects.map({ (i: NSNumber) -> GradeModel in
                        let subject = GradeModel()
                        subject.id = i.integerValue
                        subject.name = MalaConfig.malaSubject()[i.integerValue]
                        return subject
                    })
                }else {
                    self?.commitCondition()
                }
        })
        return gradeView
    }()
    /// 科目筛选面板
    private lazy var subjectView: SubjectFilterView = {
        let subjectView = SubjectFilterView(frame: CGRectZero,
            collectionViewLayout: CommonFlowLayout(type: .SubjectView),
            didTapCallBack: { [weak self] (model) -> () in
                self?.filterObject.subject = model!
                
                // 如果为一次筛选
                if !(self?.isSecondaryFilter ?? false) {
                    self?.scrollToPanel(3, animated: true)
                }else {
                    self?.commitCondition()
                }
        })
        return subjectView
    }()
    /// 风格筛选面板
    private lazy var styleView: StyleFilterView = {
        let styleView = StyleFilterView(
            frame: CGRect(x: 0, y: 0, width: MalaLayout_FilterContentWidth, height: MalaLayout_FilterContentWidth),
            tags: []
        )
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
    
    ///  滚动到指定面板
    ///
    ///  - parameter page: 面板下标，起始为1	
    func scrollToPanel(page: Int, animated: Bool) {
        switch page {
        case 1:
            // 当前显示View为 年级筛选
            currentIndex = 1
            container?.tTitle = "筛选年级"
            container?.tIcon = "grade"
            container?.setButtonStatus(showClose: true, showCancel: false, showConfirm: false)
            setContentOffset(CGPoint(x: 0, y: 0), animated: animated)
        case 2:
            // 当前显示View为 科目筛选
            currentIndex = 2
            container?.tTitle = "筛选科目"
            container?.tIcon = "subject"
            container?.setButtonStatus(showClose: false, showCancel: true, showConfirm: false)
            setContentOffset(CGPoint(x: MalaLayout_FilterContentWidth, y: 0), animated: animated)
        case 3:
            // 当前显示View为 风格筛选
            currentIndex = 3
            container?.tTitle = "筛选风格"
            container?.tIcon = "style"
            container?.setButtonStatus(showClose: false, showCancel: true, showConfirm: true)
            setContentOffset(CGPoint(x: MalaLayout_FilterContentWidth*2, y: 0), animated: animated)
        default:
            break
        }
        container?.setPageControl(page-1)
    }
    
    
    // MARK: - Private Method
    private func configuration() {
        self.contentSize = CGSize(width: MalaLayout_FilterContentWidth*3, height: MalaLayout_FilterContentWidth-3)
        self.scrollEnabled = false
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
                self?.scrollToPanel((self?.currentIndex ?? 2) - 1, animated: true)
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
                MalaFilterIndexObject.tags = self?.styleView.selectedItems ?? []
                self?.filterObject.tags = tagsCondition ?? []
                self?.commitCondition()
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
    
    private func commitCondition() {
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_CommitCondition, object: self.filterObject)
        self.container?.close()
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
            subject.name = MalaConfig.malaSubject()[i.integerValue]
            return subject
        })
        self.subjectView.subjects = subjects
        
        // 获取风格标签
        MalaNetworking.sharedTools.loadTags{ [weak self] (result, error) -> () in
            if error != nil {
                println("TeacherFilterView - loadTags Request Error")
                return
            }
            guard let dict = result as? [String: AnyObject] else {
                println("TeacherFilterView - loadTags Format Error")
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
        dataArray = nil
    }
    
    deinit {
        // 移除观察者
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_PopFilterView, object: nil)
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_ConfirmFilterView, object: nil)
    }
}


// MARK: - Condition Object
class ConditionObject: NSObject {
    
    // MARK: - Property
    var grade: GradeModel = GradeModel()
    var subject: GradeModel = GradeModel()
    var tags: [BaseObjectModel] = []
    
    
    // MARK: - Public Method
    ///  根据筛选条件对象模型，返回条件字典
    ///
    ///  - returns: 条件字典
    func getParam() -> [String: AnyObject] {
        var param: [String: AnyObject] = [String: AnyObject]()
        
        // 过滤年级
        param["grade"] = grade.id
        // 过滤科目
        param["subject"] = subject.id
        // 过滤风格
        if tags.count != 0 {
            let tagsString: String = tags.reduce("", combine: { (string, model) -> String in
                let operation = (String(string) == "" ? "" : " ")
                return String(string)+operation+String(model.id)
            })
            param["tags"] = tagsString
        }
        return param
    }
    
    
    // MARK: - Override
    override var description: String {
        let tagsString = self.tags.map({ (object: BaseObjectModel) -> String in
            return object.name ?? ""
        })
        let string = String(
            format: "grade: %@, subject: %@ , tags: %@",
            self.grade.name ?? "",
            self.subject.name ?? "",
            tagsString.joinWithSeparator(" • ")
        )
        return string
    }
}

/// 筛选条件选择下标
public class filterSelectedIndexObject: NSObject {
    var gradeIndexPath = NSIndexPath()
    var subjectIndexPath = NSIndexPath()
    var tags: [String] = []
}