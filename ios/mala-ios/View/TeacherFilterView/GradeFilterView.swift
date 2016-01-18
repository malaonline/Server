//
//  GradeFilterView.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class GradeFilterView: BaseFilterView {

    // MARK: - Property
    override var grades: [GradeModel]? {
        didSet {
            reloadData()
        }
    }
    var currentSelected: GradeModel?
    
    
    // MARK: - Constructed
    override init(frame: CGRect, collectionViewLayout layout: UICollectionViewLayout, didTapCallBack: FilterDidTapCallBack) {
        super.init(frame: frame, collectionViewLayout: layout, didTapCallBack: didTapCallBack)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Override
    override func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = super.collectionView(collectionView, cellForItemAtIndexPath: indexPath)
        
        return cell
    }
    
    override func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        super.collectionView(collectionView, didSelectItemAtIndexPath: indexPath)
        didTapCallBack?(model: self.gradeModel(indexPath.section, row: indexPath.row)!)
    }
}