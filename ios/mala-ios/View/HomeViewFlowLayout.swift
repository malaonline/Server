//
//  HomeViewFlowLayout.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class HomeViewFlowLayout: UICollectionViewFlowLayout {
    
    // MARK: - Constructed
    override init() {
        super.init()
        
        // Setup
        scrollDirection = .Vertical
        let itemWidth = MalaScreenWidth * MalaProportion_HomeCellWidthWithScreenWidth
        let itemHeight = itemWidth * MalaProportion_HomeCellHeightWithWidth
        let itemMargin = MalaScreenWidth * MalaProportion_HomeCellMarginWithScreenWidth
        itemSize = CGSizeMake(itemWidth, itemHeight)
        minimumInteritemSpacing = itemMargin
        minimumLineSpacing = itemMargin
        sectionInset = UIEdgeInsetsMake(itemMargin, itemMargin, 0, itemMargin)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}
