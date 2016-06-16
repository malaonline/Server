//
//  ProfileItemCollectionView.swift
//  mala-ios
//
//  Created by 王新宇 on 16/6/13.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

private let ProfileItemCollectionViewCellReuseId = "ProfileItemCollectionViewCellReuseId"

class ProfileItemCollectionView: UICollectionView, UICollectionViewDelegate, UICollectionViewDataSource {

    // MARK: - Property
    var model: [ProfileElementModel]? {
        didSet {
            self.reloadData()
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
        
        registerClass(ProfileItemCollectionViewCell.self, forCellWithReuseIdentifier: ProfileItemCollectionViewCellReuseId)
    }
    
    
    // MARK: - DataSource
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        // 所有操作结束弹栈时，取消选中项
        defer {
            collectionView.deselectItemAtIndexPath(indexPath, animated: true)
        }
        
        if let model = model?[indexPath.row] {
            NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_PushProfileItemController, object: model)
        }
    }
    
    func collectionView(collectionView: UICollectionView, shouldHighlightItemAtIndexPath indexPath: NSIndexPath) -> Bool {
        return true
    }
    
    
    // MARK: - DataSource
    func numberOfSectionsInCollectionView(collectionView: UICollectionView) -> Int {
        return 1
    }
    
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return model?.count ?? 3
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(ProfileItemCollectionViewCellReuseId, forIndexPath: indexPath) as! ProfileItemCollectionViewCell
        cell.model = model?[indexPath.row]
        return cell
    }
}


class ProfileItemCollectionViewCell: UICollectionViewCell {
    
    // MARK: - Property
    var model: ProfileElementModel? {
        didSet {
            iconView.image = UIImage(named: model?.iconName ?? "")
            newMessageView.image = UIImage(named: model?.newMessageIconName ?? "")
            titleLabel.text = model?.controllerTitle
            
            if let title = model?.controllerTitle {
                if title == "我的订单" {
                    newMessageView.hidden = (MalaUnpaidOrderCount == 0)
                }else if title == "我的评价" {
                    println("我的评价数量 － \(MalaToCommentCount)")
                    newMessageView.hidden = (MalaToCommentCount == 0)
                }
            }
        }
    }
    
    
    // MARK: - Components
    /// 图标
    private lazy var iconView: UIImageView = {
        let imageView = UIImageView()
        return imageView
    }()
    /// 新消息标签
    private lazy var newMessageView: UIImageView = {
        let imageView = UIImageView()
        imageView.hidden = true
        return imageView
    }()
    /// 标题标签
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: 14,
            textColor: MalaColor_636363_0
        )
        return label
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
        // SubViews
        contentView.addSubview(iconView)
        contentView.addSubview(newMessageView)
        contentView.addSubview(titleLabel)
        
        // AutoLayout
        iconView.snp_makeConstraints { (make) in
            make.centerX.equalTo(self.contentView.snp_centerX)
            make.top.equalTo(self.contentView.snp_top).offset(13)
            make.width.equalTo(53)
            make.height.equalTo(53)
        }
        newMessageView.snp_makeConstraints { (make) in
            make.top.equalTo(self.iconView.snp_top)
            make.right.equalTo(self.contentView.snp_right).offset(-10)
            make.width.equalTo(39)
            make.height.equalTo(15)
        }
        titleLabel.snp_makeConstraints { (make) in
            make.centerX.equalTo(self.contentView.snp_centerX)
            make.top.equalTo(self.iconView.snp_bottom).offset(17)
            make.height.equalTo(14)
        }
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        newMessageView.hidden = true
    }
}