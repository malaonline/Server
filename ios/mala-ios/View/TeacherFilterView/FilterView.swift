//
//  FilterCollectionView.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class FilterView: UIScrollView {
    
    // MARK: - Property
    /// 年级及科目数据源
    var grades: [GradeModel]? = nil {
        didSet {
            
        }
    }
    /// 风格数据源
    var tags: [BaseObjectModel]? = nil {
        didSet {
            
        }
    }
    /// 当前筛选条件记录模型
    lazy var filterObject: ConditionObject = {
        let object = ConditionObject()
        object.grade = GradeModel()
        object.subject = GradeModel()
        object.tag = GradeModel()
        return object
    }()
    
    
    // MARK: - Components
    /// 年级筛选面板
    private lazy var gradeView: GradeFilterView = {
        let gradeView = GradeFilterView(frame: CGRectZero, collectionViewLayout: CommonFlowLayout(type: .FilterView))
        return gradeView
    }()
    /// 科目筛选面板
    private lazy var subjectView: SubjectFilterView = {
        let subjectView = SubjectFilterView(frame: CGRectZero, collectionViewLayout: CommonFlowLayout(type: .FilterView))
        return subjectView
    }()
    /// 风格筛选面板
    private lazy var styleView: StyleFilterView = {
        let styleView = StyleFilterView(frame: CGRectZero, collectionViewLayout: CommonFlowLayout(type: .FilterView))
        return styleView
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        configuration()
        setupUserInterface()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configuration() {
        self.pagingEnabled = true
    }
    
    private func setupUserInterface() {
        // SubViews
        addSubview(gradeView)
        addSubview(subjectView)
        addSubview(styleView)
        
        // Autolayout
        gradeView.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_9)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_FontSize_12)
            make.width.equalTo(67)
            make.height.equalTo(27)
        }
    }
    func loadFilterCondition() {
        // 读取年级和科目数据
        var dataArray = NSArray(contentsOfFile: NSBundle.mainBundle().pathForResource("FilterCondition.plist", ofType: nil)!) as? [AnyObject]
        var dataDict: [GradeModel]? = []
        for object in dataArray! {
            if let dict = object as? [String: AnyObject] {
                let set = GradeModel(dict: dict)
                dataDict?.append(set)
            }
        }
        self.grades = dataDict
        
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
            
            dataDict = []
            dataArray = ResultModel(dict: dict).results
            for object in dataArray! {
                if let dict = object as? [String: AnyObject] {
                    let set = GradeModel(dict: dict)
                    dataDict?.append(set)
                }
            }
            self?.tagsCondition.subset = dataDict //TODO: 风格标签数据待处理
            self?.reloadData()
        }
        self.reloadData()
    }
}


// MARK: - Condition Object
class ConditionObject: NSObject {
    var grade: GradeModel = GradeModel()
    var subject: GradeModel = GradeModel()
    var tag: GradeModel = GradeModel()
    
    var gradeIndexPath = NSIndexPath(forItem: 0, inSection: 0)
    var subjectIndexPath = NSIndexPath(forItem: 0, inSection: 3)
    var tagIndexPath = NSIndexPath(forItem: 0, inSection: 4)
}
