//
//  MemberSerivceCollectionView.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/17.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

private let MemberSerivceCollectionViewCellReuseId = "MemberSerivceCollectionViewCellReuseId"
private let MemberSerivceCollectionViewSectionHeaderReuseId = "MemberSerivceCollectionViewSectionHeaderReuseId"
private let MemberSerivceCollectionViewSectionFooterReuseId = "MemberSerivceCollectionViewSectionFooterReuseId"

class MemberSerivceCollectionView: UICollectionView, UICollectionViewDelegate, UICollectionViewDataSource {
    
    // MARK: - Property
    /// 会员专享服务数据
    var model: [IntroductionModel] = MalaConfig.memberServiceData() {
        didSet {
            reloadData()
        }
    }
    
    
    // MARK: - Instance Method
    override init(frame: CGRect, collectionViewLayout layout: UICollectionViewLayout) {
        super.init(frame: frame, collectionViewLayout: layout)
        
        configure()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configure() {
        delegate = self
        dataSource = self
        backgroundColor = UIColor.whiteColor()
        bounces = false
        scrollEnabled = false
        
        registerClass(MemberSerivceCollectionViewCell.self, forCellWithReuseIdentifier: MemberSerivceCollectionViewCellReuseId)
        registerClass(
            MemberSerivceCollectionViewSectionHeader.self,
            forSupplementaryViewOfKind: UICollectionElementKindSectionHeader,
            withReuseIdentifier: MemberSerivceCollectionViewSectionHeaderReuseId
        )
        registerClass(
            MemberSerivceCollectionViewSectionHeader.self,
            forSupplementaryViewOfKind: UICollectionElementKindSectionFooter,
            withReuseIdentifier: MemberSerivceCollectionViewSectionFooterReuseId
        )
    }
    
    
    // MARK: - Delegate
    func collectionView(collectionView: UICollectionView, viewForSupplementaryElementOfKind kind: String, atIndexPath indexPath: NSIndexPath) -> UICollectionReusableView {
        
        // Section 头部视图
        if kind == UICollectionElementKindSectionHeader {
            let sectionHeaderView = collectionView.dequeueReusableSupplementaryViewOfKind(
                UICollectionElementKindSectionHeader,
                withReuseIdentifier: MemberSerivceCollectionViewSectionHeaderReuseId,
                forIndexPath: indexPath
                ) as! MemberSerivceCollectionViewSectionHeader
            return sectionHeaderView
        }
        return UICollectionReusableView()
    }
    
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        // 所有操作结束弹栈时，取消选中项
        defer {
            collectionView.deselectItemAtIndexPath(indexPath, animated: true)
        }
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_PushIntroduction, object: (indexPath.section*4+(indexPath.row)))
    }
    
    func collectionView(collectionView: UICollectionView, shouldHighlightItemAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }
    
    
    // MARK: - DataSource
    func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        return 2
    }
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return 4
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(MemberSerivceCollectionViewCellReuseId, forIndexPath: indexPath) as! MemberSerivceCollectionViewCell
        let index = indexPath.section*4 + (indexPath.row)
        cell.model = self.model[index]
        if indexPath.row == 0 {
            cell.hideSeparator(true)
        }
        return cell
    }
}


// MARK: - MemberSerivceCollectionViewSectionHeader
class MemberSerivceCollectionViewSectionHeader: UICollectionReusableView {
    
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
        backgroundColor = MalaColor_E5E5E5_0
    }
}


class MemberSerivceCollectionViewCell: UICollectionViewCell {
    
    // MARK: - Property
    /// 会员专享模型
    var model: IntroductionModel? {
        didSet {
            iconView.image = UIImage(named: model?.image ?? "")
            titleLabel.text = model?.title
        }
    }
    /// 选中状态
    override internal var selected: Bool {
        didSet {
            if selected {
                contentView.backgroundColor = MalaColor_E5E5E5_0
            }else {
                contentView.backgroundColor = UIColor.whiteColor()
            }
        }
    }

    
    // MARK: - Compontents
    /// 图标
    private lazy var iconView: UIImageView = {
        let imageView = UIImageView()
        return imageView
    }()
    /// 标题标签
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: 13,
            textColor: MalaColor_636363_0
        )
        label.textAlignment = .Center
        return label
    }()
    /// 侧分割线
    lazy var separator: UIView = {
        let view = UIView.separator(MalaColor_E5E5E5_0)
        return view
    }()
    
    
    // MARK: - Instance Method
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        
        // SubViews
        contentView.addSubview(iconView)
        contentView.addSubview(titleLabel)
        contentView.addSubview(separator)
        
        // Autolayout
        iconView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(20)
            make.centerX.equalTo(self.contentView.snp_centerX)
            make.width.equalTo(23)
            make.height.equalTo(23)
        }
        titleLabel.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(self.contentView.snp_centerX)
            make.height.equalTo(13)
            make.top.equalTo(iconView.snp_bottom).offset(14)
        }
        separator.snp_makeConstraints { (make) in
            make.left.equalTo(self.contentView.snp_left)
            make.width.equalTo(MalaScreenOnePixel)
            make.top.equalTo(self.contentView.snp_top).offset(15)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-15)
        }
    }
    
    func hideSeparator(hide: Bool) {
        separator.hidden = hide
    }
}


class MemberSerivceFlowLayout: UICollectionViewFlowLayout {
    
    // MARK: - Instance Method
    init(frame: CGRect) {
        super.init()
        configure()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configure() {
        scrollDirection = .Vertical
        let itemWidth: CGFloat = MalaLayout_CardCellWidth / 4
        let itemHeight: CGFloat = 91
        itemSize = CGSizeMake(itemWidth, itemHeight)
        headerReferenceSize = CGSize(width: 300, height: MalaScreenOnePixel)
        minimumInteritemSpacing = 0
        minimumLineSpacing = 0
    }
}