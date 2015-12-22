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
    lazy var grades = [[]]
    
    
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
        
        // Setup Style
        bounces = false
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    convenience init(viewController: UIViewController, frame: CGRect? = nil) {
        self.init(frame: frame ?? CGRectZero, collectionViewLayout: CommonFlowLayout(type: .FilterView))
        // init FilterView
        originFrame = frame ?? CGRectZero
        self.backgroundColor = UIColor.whiteColor()
        self.alpha = 0.9
        viewController.view.addSubview(self)
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
        return 3
    }
    
    func collectionView(collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, atIndexPath indexPath: NSIndexPath) -> UICollectionReusableView {
        
        var reusableView: UICollectionReusableView?

        if kind == UICollectionElementKindSectionHeader {
            let sectionHeaderView = collectionView.dequeueReusableSupplementaryViewOfKind(UICollectionElementKindSectionHeader, withReuseIdentifier: TeacherFilterViewSectionHeaderReusedId, forIndexPath: indexPath) as! FilterSectionHeaderView
            sectionHeaderView.sectionTitleText = "标题"
            reusableView = sectionHeaderView
        }
        
        if kind == UICollectionElementKindSectionFooter {
            let sectionHeaderView: FilterSectionFooterView = collectionView.dequeueReusableSupplementaryViewOfKind(UICollectionElementKindSectionFooter, withReuseIdentifier: TeacherFilterViewSectionFooterReusedId, forIndexPath: indexPath) as! FilterSectionFooterView
            reusableView = sectionHeaderView
        }
        return reusableView!
    }
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 6
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(TeacherFilterViewCellReusedId, forIndexPath: indexPath)
        cell.backgroundColor =  UIColor.redColor()
        cell.alpha = 0.4
        return cell
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


