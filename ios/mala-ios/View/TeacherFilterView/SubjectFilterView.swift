//
//  SubjectFilterView.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class SubjectFilterView: BaseFilterView {

    // MARK: - Property
    var subjects: [GradeModel]? = nil {
        didSet {
            self.reloadData()
        }
    }
    var currentSelected: GradeModel?
    var shouldDisplayAll: Bool = true
    
    
    // MARK: - Constructed
    override init(frame: CGRect, collectionViewLayout layout: UICollectionViewLayout, didTapCallBack: FilterDidTapCallBack) {
        super.init(frame: frame, collectionViewLayout: layout, didTapCallBack: didTapCallBack)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    // MARK: - Delegate
    override func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        super.collectionView(collectionView, didSelectItemAtIndexPath: indexPath)
        didTapCallBack?(model: self.subjects![indexPath.row])
    }
    
    
    // MARK: - DataSource
    override func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        return 1
    }
    
    override func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return shouldDisplayAll ? 9 : 3
    }
    
    override func collectionView(collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, atIndexPath indexPath: NSIndexPath) -> UICollectionReusableView {
        let sectionFooterView = collectionView.dequeueReusableSupplementaryViewOfKind(UICollectionElementKindSectionFooter, withReuseIdentifier: FilterViewSectionFooterReusedId, forIndexPath: indexPath)
        return sectionFooterView
    }
    
    override func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(FilterViewCellReusedId, forIndexPath: indexPath) as! FilterViewCell
        cell.model = subjects![indexPath.row]
        return cell
    }
}
