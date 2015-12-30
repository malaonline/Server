//
//  CommonFlowLayout.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class CommonFlowLayout: UICollectionViewFlowLayout {
    
    // MARK: - FlowLayoutType
    enum FlowLayoutType {
        case HomeView
        case FilterView
    }
    
    // MARK: - Constructed
    init(type layoutType: FlowLayoutType) {
        super.init()
        
        // Layout for Setup 
        switch layoutType {
        case .HomeView:
            homeViewFlowLayout()
        case.FilterView:
            filterViewFlowLayout()
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Function
    private func homeViewFlowLayout() {
        scrollDirection = .Vertical
        let itemWidth: CGFloat = MalaScreenWidth * MalaProportion_HomeCellWidthWithScreenWidth
        let itemHeight: CGFloat = itemWidth * MalaProportion_HomeCellHeightWithWidth
        let itemMargin: CGFloat = MalaScreenWidth * MalaProportion_HomeCellMarginWithScreenWidth
        itemSize = CGSizeMake(itemWidth, itemHeight)
        minimumInteritemSpacing = itemMargin
        minimumLineSpacing = itemMargin
        sectionInset = UIEdgeInsetsMake(itemMargin, itemMargin, itemMargin, itemMargin)
    }
    
    private func filterViewFlowLayout() {
        
        scrollDirection = .Vertical
        let itemWidth: CGFloat = 130.0
        let itemHeight: CGFloat = 20.0
        let itemMargin: CGFloat = 20.0
        itemSize = CGSizeMake(itemWidth, itemHeight)
        minimumInteritemSpacing = itemMargin
        minimumLineSpacing = itemMargin
        sectionInset = UIEdgeInsetsMake(itemMargin/2, itemMargin, itemMargin/2, itemMargin)
        
        headerReferenceSize = CGSize(width: 100, height: 40)
        footerReferenceSize = CGSize(width: 300.0, height: 10)
    }
}