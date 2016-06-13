//
//  CommonFlowLayout.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class CommonFlowLayout: UICollectionViewFlowLayout {
    
    // MARK: - Property
    enum FlowLayoutType {
        case HomeView
        case FilterView
        case SubjectView
        case GradeSelection
        case ProfileItem
    }
    
    
    // MARK: - Constructed
    init(type layoutType: FlowLayoutType) {
        super.init()
        
        // 根据Type来应用对应的布局样式
        switch layoutType {
        case .HomeView:
            homeViewFlowLayout()
        case .FilterView:
            filterViewFlowLayout()
        case .SubjectView:
            subjectViewFlowLayout()
        case .GradeSelection:
            gradeSelectionFlowLayout()
        case .ProfileItem:
            profileItemFlowLayout()
        }
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
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
        let itemWidth: CGFloat = MalaLayout_FilterItemWidth
        let itemHeight: CGFloat = 38.0
        let itemMargin: CGFloat = 0.0
        itemSize = CGSizeMake(itemWidth, itemHeight)
        minimumInteritemSpacing = itemMargin
        minimumLineSpacing = itemMargin
        sectionInset = UIEdgeInsetsMake(itemMargin/2, itemMargin, itemMargin/2, itemMargin)
        headerReferenceSize = CGSize(width: 100, height: 34)
        footerReferenceSize = CGSize(width: 100, height: 30)
    }
    
    private func subjectViewFlowLayout() {
        scrollDirection = .Vertical
        let itemWidth: CGFloat = MalaLayout_FilterItemWidth-10
        let itemHeight: CGFloat = 38.0
        let itemMargin: CGFloat = 0.0
        itemSize = CGSizeMake(itemWidth, itemHeight)
        minimumInteritemSpacing = itemMargin
        minimumLineSpacing = itemMargin
        sectionInset = UIEdgeInsetsMake(itemMargin/2, itemMargin, itemMargin/2, itemMargin)
        headerReferenceSize = CGSize(width: 100, height: 0)
        footerReferenceSize = CGSize(width: 100, height: 0)
    }
    
    private func gradeSelectionFlowLayout() {
        scrollDirection = .Vertical
        let itemWidth = MalaLayout_GradeSelectionWidth
        let itemHeight: CGFloat = MalaLayout_GradeSelectionWidth*0.19
        itemSize = CGSizeMake(itemWidth, itemHeight)
        sectionInset = UIEdgeInsetsMake(0, 0, 0, 0)
        minimumInteritemSpacing = 12
        minimumLineSpacing = 14
    }
    
    private func profileItemFlowLayout() {
        scrollDirection = .Horizontal
        let itemWidth = MalaScreenWidth/3
        let itemHeight: CGFloat = 114
        itemSize = CGSizeMake(itemWidth, itemHeight)
        sectionInset = UIEdgeInsetsMake(0, 0, 0, 0)
        minimumInteritemSpacing = 0
        minimumLineSpacing = 0
    }
}