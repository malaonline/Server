//
//  StyleFilterView.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class StyleFilterView: BaseFilterView {

    // MARK: - Property
    var tags: [BaseObjectModel]? = nil {
        didSet {
            self.reloadData()
        }
    }
    
    
    // MARK: - Constructed
    override init(frame: CGRect, collectionViewLayout layout: UICollectionViewLayout, didTapCallBack: FilterDidTapCallBack) {
        super.init(frame: frame, collectionViewLayout: layout, didTapCallBack: didTapCallBack)
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
