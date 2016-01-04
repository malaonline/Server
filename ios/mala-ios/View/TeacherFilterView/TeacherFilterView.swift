//
//  TeacherFilterView.swift
//  mala-ios
//
//  Created by Elors on 12/22/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

private let TeacherFilterViewCellReusedId = "TeacherFilterViewCellReusedId"
private let TeacherFilterViewSectionHeaderReusedId = "TeacherFilterViewSectionHeaderReusedId"
private let TeacherFilterViewSectionFooterReusedId = "TeacherFilterViewSectionFooterReusedId"

public class TeacherFilterView: UICollectionView, UICollectionViewDelegate, UICollectionViewDataSource {
    
    // MARK: - Variables
    var currentSelectedGrade: FilterViewCell?
    var currentSelectedSubject: FilterViewCell?
    var currentSelectedTag: FilterViewCell?
    
    // Filter Condition Data
    lazy var grades: [GradeModel]? = nil
    
    private lazy var subjectCondition: GradeModel = {
        let grade = GradeModel()
        grade.name = "科目"
        return grade
    }()
    
    private lazy var tagsCondition: GradeModel = {
        let tag = GradeModel()
        tag.name = "风格"
        return tag
    }()
    
    // Current Selected Filter Condition, Default is -1
    lazy var filterObject: ConditionObject = {
        let object = ConditionObject()
        object.grade = GradeModel()
        object.subject = GradeModel()
        object.tag = GradeModel()
        return object
    }()
    
    
    // MARK: - Components
    private lazy var loadingView: UIView = {
        let loadingView = UIView()
        let loadingIndicator = UIActivityIndicatorView(activityIndicatorStyle: UIActivityIndicatorViewStyle.White)
        loadingView.addSubview(loadingIndicator)
        return loadingView
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect, collectionViewLayout layout: UICollectionViewLayout) {
        super.init(frame: frame, collectionViewLayout: layout)
        
        //  Register Class for Cell and Header/Footer View
        registerClass(FilterViewCell.self, forCellWithReuseIdentifier: TeacherFilterViewCellReusedId)
        registerClass(FilterSectionHeaderView.self, forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: TeacherFilterViewSectionHeaderReusedId)
        registerClass(FilterSectionFooterView.self, forSupplementaryViewOfKind: UICollectionElementKindSectionFooter, withReuseIdentifier: TeacherFilterViewSectionFooterReusedId)
        
        // Setup Delegate and DataSource
        delegate = self
        dataSource = self
        
        self.backgroundColor = UIColor.whiteColor()
        
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    func loadFilterCondition() {
        
        // load Grades and Subjects
        var tempArray = NSArray(contentsOfFile: NSBundle.mainBundle().pathForResource("FilterCondition.plist", ofType: nil)!) as? [AnyObject]
        var tempDict: [GradeModel]? = []
        for object in tempArray! {
            if let dict = object as? [String: AnyObject] {
                let set = GradeModel(dict: dict)
                tempDict?.append(set)
            }
        }
        self.grades = tempDict
        self.grades?.append(self.subjectCondition)
        
        // load Tags
        NetworkTool.sharedTools.loadTags{ [weak self] (result, error) -> () in
                        
            // Error
            if error != nil {
                debugPrint("TeacherFilterView - loadTags Request Error")
                return
            }
            
            // Make sure Dict not nil
            guard let dict = result as? [String: AnyObject] else {
                debugPrint("TeacherFilterView - loadTags Format Error")
                return
            }
            
            tempDict = []
            tempArray = ResultModel(dict: dict).results
            for object in tempArray! {
                if let dict = object as? [String: AnyObject] {
                    let set = GradeModel(dict: dict)
                    tempDict?.append(set)
                }
            }
            self?.tagsCondition.subset = tempDict
            self?.reloadData()
        }
        self.grades?.append(self.tagsCondition)
        self.reloadData()
    }
    
    
    // MARK: - DataSource
    public func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        return self.grades?.count ?? 5
    }
    
    public func collectionView(collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, atIndexPath indexPath: NSIndexPath) -> UICollectionReusableView {
        
        var reusableView: UICollectionReusableView?

        if kind == UICollectionElementKindSectionHeader {
            let sectionHeaderView = collectionView.dequeueReusableSupplementaryViewOfKind(UICollectionElementKindSectionHeader, withReuseIdentifier: TeacherFilterViewSectionHeaderReusedId, forIndexPath: indexPath) as! FilterSectionHeaderView
            sectionHeaderView.sectionTitleText = gradeModel(indexPath.section)?.name
            reusableView = sectionHeaderView
        }
        
        if kind == UICollectionElementKindSectionFooter {
            let sectionFooterView: FilterSectionFooterView = collectionView.dequeueReusableSupplementaryViewOfKind(UICollectionElementKindSectionFooter, withReuseIdentifier: TeacherFilterViewSectionFooterReusedId, forIndexPath: indexPath) as! FilterSectionFooterView
            reusableView = sectionFooterView
        }
        return reusableView!
    }
    
    public func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return gradeModel(section)?.subset?.count ?? 0
    }
    
    
    
    public func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(TeacherFilterViewCellReusedId, forIndexPath: indexPath) as! FilterViewCell
        cell.model = gradeModel(indexPath.section, row: indexPath.row)!
        cell.indexPath = indexPath

        var shouldSelected = true

        if indexPath == self.filterObject.gradeIndexPath {
            self.currentSelectedGrade = cell
        }else if indexPath == self.filterObject.subjectIndexPath {
            self.currentSelectedSubject  = cell
        }else if indexPath == self.filterObject.tagIndexPath {
            self.currentSelectedTag  = cell
        }else {
            shouldSelected = false
        }

        cell.selected = shouldSelected
        return cell
    }
    
    /// convenience to get gradeModel whit section and row
    private func gradeModel(section: Int, row: Int? = nil) -> GradeModel? {
        if row == nil {
            return self.grades?[section]
        }else {
            return self.grades?[section].subset?[row!]
        }
    }
    
    
    // MARK: - Delegate
    public func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        
        let sender = collectionView.cellForItemAtIndexPath(indexPath) as! FilterViewCell
        let model = gradeModel(indexPath.section, row: indexPath.row)
        
        // grade,subject,tag
        switch indexPath.section {
        case 0...2:
            let grade = gradeModel(indexPath.section, row: indexPath.row)
            let subjects = grade?.subjects.map({ (i: NSNumber) -> GradeModel in
                let subject = GradeModel()
                subject.id = i.integerValue
                subject.name = MalaSubject[i.integerValue]
                return subject
            })
            self.subjectCondition.subset = subjects
            if model?.subjects.count != self.filterObject.grade.subjects.count  || model?.subjects.count == 0{
                reloadSections(NSIndexSet(index: 3))
            }
            
            currentSelectedGrade?.selected = false
            currentSelectedGrade = sender
            self.filterObject.grade = model!
            self.filterObject.gradeIndexPath = indexPath
            
        case 3:
            currentSelectedSubject?.selected = false
            currentSelectedSubject = sender
            self.filterObject.subject = model!
            self.filterObject.subjectIndexPath = indexPath
            
        case 4:
            currentSelectedTag?.selected = false
            currentSelectedTag = sender
            self.filterObject.tag = model!
            self.filterObject.tagIndexPath = indexPath
            
        default:
            break
        }
        reloadData()
    }
    
}


// MARK: - FilterSectionHeaderView
class FilterSectionHeaderView: UICollectionReusableView {
    
    private lazy var sectionTitle: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(18)
        label.textColor = UIColor(rgbHexValue: 0x333333, alpha: 0.75)
        label.text = "筛选"
        label.sizeToFit()
        return label
    }()
    
    var sectionTitleText: String? {
        didSet {
            sectionTitle.text = sectionTitleText
        }
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        let labelY = frame.size.height - sectionTitle.frame.size.height
        sectionTitle.frame = CGRect(x: 20, y: labelY, width: sectionTitle.frame.size.width, height: sectionTitle.frame.size.height)
        addSubview(sectionTitle)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}


// MARK: - FilterSectionFooterView
class FilterSectionFooterView: UICollectionReusableView {
    
    private lazy var separator: UIView = {
        let separatorLine = UIView()
        separatorLine.backgroundColor = UIColor(rgbHexValue: 0xc8c8c8, alpha: 0.75)
        return separatorLine
    }()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        let lineWidth = frame.width*0.9
        separator.frame = CGRect(x: (frame.width-lineWidth)/2, y: frame.size.height, width: lineWidth, height: 1 / UIScreen.mainScreen().scale)
        addSubview(separator)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}


// MARK: - ConditionObject
class ConditionObject: NSObject {
    var grade: GradeModel = GradeModel()
    var subject: GradeModel = GradeModel()
    var tag: GradeModel = GradeModel()
    
    var gradeIndexPath = NSIndexPath(forItem: 0, inSection: 0)
    var subjectIndexPath = NSIndexPath(forItem: 0, inSection: 3)
    var tagIndexPath = NSIndexPath(forItem: 0, inSection: 4)
}
