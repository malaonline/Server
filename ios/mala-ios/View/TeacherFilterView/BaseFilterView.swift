//
//  BaseFilterView.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

internal let FilterViewCellReusedId = "FilterViewCellReusedId"
internal let FilterViewSectionHeaderReusedId = "FilterViewSectionHeaderReusedId"
internal let FilterViewSectionFooterReusedId = "FilterViewSectionFooterReusedId"
/// 结果返回值闭包
typealias FilterDidTapCallBack = (model: GradeModel?)->()

class BaseFilterView: UICollectionView, UICollectionViewDelegate, UICollectionViewDataSource {
    
    // MARK: - Property
    /// 年级及科目数据模型
    var grades: [GradeModel]? = nil
    /// Cell点击回调闭包
    var didTapCallBack: FilterDidTapCallBack?
    /// 当前选中项下标标记
    var selectedIndexPath: NSIndexPath? 
    
    
    // MARK: - Constructed
    init(frame: CGRect, collectionViewLayout layout: UICollectionViewLayout, didTapCallBack: FilterDidTapCallBack) {
        super.init(frame: frame, collectionViewLayout: layout)
        self.didTapCallBack = didTapCallBack
        configration()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Deleagte
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        let cell = collectionView.cellForItemAtIndexPath(indexPath)
        if let currentIndexPath = selectedIndexPath {
            collectionView.cellForItemAtIndexPath(currentIndexPath)?.selected = false
        }
        cell?.selected = true
        self.selectedIndexPath = indexPath
    }
    
    
    // MARK: - DataSource
    func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        // 默认情况为 小学, 初中, 高中 三项
        return self.grades?.count ?? 3
    }
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return gradeModel(section)?.subset?.count ?? 0
    }
    
    func collectionView(collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, atIndexPath indexPath: NSIndexPath) -> UICollectionReusableView {
        var reusableView: UICollectionReusableView?
        // Section 头部视图
        if kind == UICollectionElementKindSectionHeader {
            let sectionHeaderView = collectionView.dequeueReusableSupplementaryViewOfKind(
                UICollectionElementKindSectionHeader,
                withReuseIdentifier: FilterViewSectionHeaderReusedId,
                forIndexPath: indexPath
                ) as! FilterSectionHeaderView
            sectionHeaderView.sectionTitleText = gradeModel(indexPath.section)?.name ?? "年级"
            reusableView = sectionHeaderView
        }
        // Section 尾部视图
        if kind == UICollectionElementKindSectionFooter {
            let sectionFooterView = collectionView.dequeueReusableSupplementaryViewOfKind(
                UICollectionElementKindSectionFooter,
                withReuseIdentifier: FilterViewSectionFooterReusedId,
                forIndexPath: indexPath
            )
            reusableView = sectionFooterView
        }
        return reusableView!
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(FilterViewCellReusedId, forIndexPath: indexPath) as! FilterViewCell
        cell.model = gradeModel(indexPath.section, row: indexPath.row)!
        cell.indexPath = indexPath
        return cell
    }
    
    
    // MARK: - Private Method
    private func configration() {
        delegate = self
        dataSource = self
        registerClass(FilterViewCell.self,
            forCellWithReuseIdentifier: FilterViewCellReusedId)
        registerClass(FilterSectionHeaderView.self,
            forSupplementaryViewOfKind: UICollectionElementKindSectionHeader,
            withReuseIdentifier: FilterViewSectionHeaderReusedId)
        registerClass(UICollectionReusableView.self,
            forSupplementaryViewOfKind: UICollectionElementKindSectionFooter,
            withReuseIdentifier: FilterViewSectionFooterReusedId)
        self.backgroundColor = UIColor.whiteColor()
    }
    
    /// 便利方法——通过 Section 或 Row 获取对应数据模型
    internal func gradeModel(section: Int, row: Int? = nil) -> GradeModel? {
        if row == nil {
            return self.grades?[section]
        }else {
            return self.grades?[section].subset?[row!]
        }
    }
}


// MARK: - FilterSectionHeaderView
class FilterSectionHeaderView: UICollectionReusableView {
    
    // MARK: - Property
    /// Section标题
    var sectionTitleText: String = "标题" {
        didSet {
            titleLabel.text = sectionTitleText
            switch sectionTitleText {
            case "小学":
                iconView.image = UIImage(named: "primarySchool")
            case "初中":
                iconView.image = UIImage(named: "juniorHigh")
            case "高中":
                iconView.image = UIImage(named: "seniorHigh")
            default:
                break
            }
        }
    }
    
    
    // MARK: - Components
    private lazy var iconView: UIImageView = {
        let iconView = UIImageView()
        iconView.image = UIImage(named: "primarySchool")
        return iconView
    }()
    private lazy var titleLabel: UILabel = {
        let titleLabel = UILabel()
        titleLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        titleLabel.textColor = MalaColor_939393_0
        titleLabel.text = "小学"
        titleLabel.sizeToFit()
        return titleLabel
    }()
    
    
    // MARK: - Contructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // SubViews
        self.addSubview(iconView)
        self.addSubview(titleLabel)
        
        // AutoLayout
        iconView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.snp_top).offset(MalaLayout_Margin_4)
            make.left.equalTo(self.snp_left)
            make.width.equalTo(20)
            make.height.equalTo(20)
        }
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(self.iconView.snp_centerY)
            make.left.equalTo(self.iconView.snp_right).offset(MalaLayout_Margin_9)
        }
    }
}
