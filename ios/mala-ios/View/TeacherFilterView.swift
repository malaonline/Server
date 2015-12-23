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

class TeacherFilterView: UICollectionView, UICollectionViewDelegate, UICollectionViewDataSource {
    
    // MARK: - Variables
    var isShow: Bool = false
    var originFrame: CGRect = CGRectZero
    var currentSelectedGrade = UIButton()
    var currentSelectedSubject = UIButton()
    var currentSelectedTag = UIButton()
    
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
        object.gradeId = -1
        object.subjectId = -1
        object.tagId = -1
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
        registerClass(UICollectionViewCell.self, forCellWithReuseIdentifier: TeacherFilterViewCellReusedId)
        registerClass(FilterSectionHeaderView.self, forSupplementaryViewOfKind: UICollectionElementKindSectionHeader, withReuseIdentifier: TeacherFilterViewSectionHeaderReusedId)
        registerClass(FilterSectionFooterView.self, forSupplementaryViewOfKind: UICollectionElementKindSectionFooter, withReuseIdentifier: TeacherFilterViewSectionFooterReusedId)
        
        // Setup Delegate and DataSource
        delegate = self
        dataSource = self
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    convenience init(viewController: UIViewController, frame: CGRect? = nil) {
        self.init(frame: frame ?? CGRectZero, collectionViewLayout: CommonFlowLayout(type: .FilterView))
        
        // init FilterView
        originFrame = frame ?? CGRectZero
        self.backgroundColor = UIColor.whiteColor()
        viewController.view.addSubview(self)
        
        loadFilterCondition()
    }
    
    
    // MARK: - Private Function
    private func loadFilterCondition() {
        
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
        tempArray = NSArray(contentsOfFile: NSBundle.mainBundle().pathForResource("Tags.plist", ofType: nil)!) as? [AnyObject]
        for object in tempArray! {
            if let dict = object as? [String: AnyObject] {
                let set = GradeModel(dict: dict)
                tempDict?.append(set)
            }
        }
        self.tagsCondition.subset = tempDict
        self.grades?.append(self.tagsCondition)
        
        self.reloadData()
    }
    
    
    // MARK: - API
    func show() {
        UIView.animateWithDuration(0.25, animations: { () -> Void in
            self.frame.origin.y = 64
            }) { (isCompletion) -> Void in
                self.isShow = true
        }
    }
    
    func dismiss() {
        UIView.animateWithDuration(0.25, animations: { () -> Void in
            self.frame = self.originFrame
            }) { (isCompletion) -> Void in
                self.isShow = false
        }
    }
    
    
    // MARK: - DataSource
    func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        return self.grades?.count ?? 0
    }
    
    func collectionView(collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, atIndexPath indexPath: NSIndexPath) -> UICollectionReusableView {
        
        var reusableView: UICollectionReusableView?

        if kind == UICollectionElementKindSectionHeader {
            let sectionHeaderView = collectionView.dequeueReusableSupplementaryViewOfKind(UICollectionElementKindSectionHeader, withReuseIdentifier: TeacherFilterViewSectionHeaderReusedId, forIndexPath: indexPath) as! FilterSectionHeaderView
            sectionHeaderView.sectionTitleText = gradeModel(indexPath.section)?.name
            reusableView = sectionHeaderView
        }
        
        if kind == UICollectionElementKindSectionFooter {
            let sectionHeaderView: FilterSectionFooterView = collectionView.dequeueReusableSupplementaryViewOfKind(UICollectionElementKindSectionFooter, withReuseIdentifier: TeacherFilterViewSectionFooterReusedId, forIndexPath: indexPath) as! FilterSectionFooterView
            reusableView = sectionHeaderView
        }
        return reusableView!
    }
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return gradeModel(section)?.subset?.count ?? 0
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(TeacherFilterViewCellReusedId, forIndexPath: indexPath)
        
        let subject = gradeModel(indexPath.section, row: indexPath.row)
        let button = FilterViewCellButton(title: subject?.name ?? "", titleColor: UIColor.whiteColor(), selectedTitleColor: UIColor.whiteColor(), bgColor: UIColor.lightGrayColor(), selectedBgColor: UIColor.redColor())
        button.indexPath = indexPath
        button.tag = (subject?.id) ?? 0
        button.addTarget(self, action: "cellButtonDidClick:", forControlEvents: .TouchUpInside)
        button.frame = cell.bounds
        button.frame.size.width = cell.bounds.width*0.75
        button.layer.cornerRadius = button.frame.height*0.5
        button.clipsToBounds = true
        cell.contentView.addSubview(button)
        
        return cell
    }
    
    
    // MARK: - Event Response
    @objc private func cellButtonDidClick(sender: FilterViewCellButton) {
        let indexPath = sender.indexPath

        // grade,subject,tag
        switch indexPath.section {
        case 0...2:
            self.filterObject.gradeId = (gradeModel(indexPath.section, row: indexPath.row)?.id)!
            let grade = gradeModel(indexPath.section, row: indexPath.row)
            let subjects = grade?.subjects.map({ (i: NSNumber) -> GradeModel in
                let subject = GradeModel()
                subject.id = i.integerValue
                subject.name = MalaSubject[i.integerValue]
                return subject
            })
            self.subjectCondition.subset = subjects
            reloadSections(NSIndexSet(index: 3))

            currentSelectedGrade.selected = false
            sender.selected = true
            currentSelectedGrade = sender
            self.filterObject.gradeId = sender.tag
            
        case 3:
            self.filterObject.subjectId = (gradeModel(indexPath.section, row: indexPath.row)?.id)!
            
            
            currentSelectedSubject.selected = false
            sender.selected = true
            currentSelectedSubject = sender
            self.filterObject.subjectId = sender.tag
        case 4:
            self.filterObject.tagId = (gradeModel(indexPath.section, row: indexPath.row)?.id)!
            
            currentSelectedTag.selected = false
            sender.selected = true
            currentSelectedTag = sender
            self.filterObject.tagId = sender.tag
        default:
            break
        }
        
    }
    
    /// convenience to get gradeModel whit section and row
    private func gradeModel(section: Int, row: Int? = nil) -> GradeModel? {
        if row == nil {
            return self.grades?[section]
        }else {
            return self.grades?[section].subset?[row!]
        }
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
    var gradeId: Int = 0
    var subjectId: Int = 0
    var tagId: Int = 0
}


class FilterViewCellButton: UIButton {

    var indexPath: NSIndexPath = NSIndexPath(forItem: 0, inSection: 0)
    
    ///  Convenience Function to Create UIButton With TitleColor and BackgroundColor
    ///
    ///  - parameter title:              String for Title
    ///  - parameter titleColor:         UIColor for TitleColor in NormalState
    ///  - parameter selectedTitleColor: UIColor for TitleColor in SelectedState
    ///  - parameter bgColor:            UIColor for BackgroundColor in NormalState
    ///  - parameter selectedBgColor:    UIColor for BackgroundColor in SelectedState
    ///
    ///  - returns: UIButton
    convenience init(title: String, titleColor: UIColor? = nil, selectedTitleColor: UIColor? = nil, bgColor: UIColor? = nil, selectedBgColor: UIColor? = nil) {
        self.init()
        setTitle(title, forState: .Normal)
        titleLabel?.font = UIFont.systemFontOfSize(14)
        setTitleColor(titleColor, forState: .Normal)
        setTitleColor(selectedTitleColor, forState: .Selected)
        setBackgroundImage(UIImage.withColor(bgColor), forState: .Normal)
        setBackgroundImage(UIImage.withColor(selectedBgColor), forState: .Selected)
        sizeToFit()
    }
    
}