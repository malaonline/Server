//
//  ThemePhotoCollectionView.swift
//  mala-ios
//
//  Created by 王新宇 on 16/7/6.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

private let ThemePhotoCollectionViewCellReuseId = "ThemePhotoCollectionViewCellReuseId"

class ThemePhotoCollectionView: UICollectionView, UICollectionViewDelegate, UICollectionViewDataSource {

    // MARK: - Property
    /// 图片URL数组
    var urls: [String] = [] {
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
        showsVerticalScrollIndicator = false
        showsHorizontalScrollIndicator = false
        backgroundColor = UIColor.whiteColor()
        registerClass(ThemePhotoCollectionViewCell.self, forCellWithReuseIdentifier: ThemePhotoCollectionViewCellReuseId)
    }
    
    // MARK: - DataSource
    func collectionView(collectionView: UICollectionView, numberOfItemsInSection section: Int) -> Int {
        return urls.count
    }
    
    func collectionView(collectionView: UICollectionView, cellForItemAtIndexPath indexPath: NSIndexPath) -> UICollectionViewCell {
        let cell = collectionView.dequeueReusableCellWithReuseIdentifier(ThemePhotoCollectionViewCellReuseId, forIndexPath: indexPath) as! ThemePhotoCollectionViewCell
        cell.url = urls[indexPath.row]
        cell.imageView.tag = indexPath.row
        return cell
    }
    
    // MARK: - Delegate
    func collectionView(collectionView: UICollectionView, didSelectItemAtIndexPath indexPath: NSIndexPath) {
        let cell = collectionView.cellForItemAtIndexPath(indexPath) as! ThemePhotoCollectionViewCell
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_PushPhotoBrowser, object: cell.imageView)
    }
}


class ThemePhotoCollectionViewCell: UICollectionViewCell {
    
    // MARK: - Property
    /// 图片URL
    var url: String = "" {
        didSet {
            imageView.kf_setImageWithURL((NSURL(string: url) ?? NSURL()), optionsInfo: [.Transition(.Fade(0.25))])
        }
    }
    
    
    // MARK: - Components
    /// 图片视图
    private lazy var imageView: UIImageView = {
        let imageView = UIImageView.placeHolder()
        return imageView
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
        contentView.addSubview(imageView)
        
        // Autolayout
        imageView.snp_makeConstraints { (make) in
            make.center.equalTo(contentView)
            make.size.equalTo(contentView)
        }
    }
}