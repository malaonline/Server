//
//  TeacherDetailsPhotosCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit
import Kingfisher

class TeacherDetailsPhotosCell: TeacherDetailBaseCell {

    // MARK: - Property
    /// 图片URL数组
    var photos: [String] = [] {
        didSet {
            // 加载图片URL数据
            photoCollection.urls = photos
        }
    }
    
    
    // MARK: - Components
    private lazy var detailButton: UIButton = {
        let button = UIButton()
        button.setImage(UIImage(named: "rightArrow"), forState: .Normal)
        button.setTitle("更多", forState: .Normal)
        button.setTitleColor(MalaColor_939393_0, forState: .Normal)
        button.titleLabel?.font = UIFont.systemFontOfSize(12)
        button.titleEdgeInsets = UIEdgeInsets(top: 0, left: -10, bottom: 0, right: 10)
        button.imageEdgeInsets = UIEdgeInsets(top: 0, left: 24, bottom: 0, right: -24)
        button.addTarget(self, action: #selector(TeacherDetailsPhotosCell.detailButtonDidTap), forControlEvents: .TouchUpInside)
        return button
    }()
    /// 图片滚动浏览视图
    private lazy var photoCollection: ThemePhotoCollectionView = {
        let collection = ThemePhotoCollectionView(frame: CGRectZero, collectionViewLayout: CommonFlowLayout(type: .DetailPhotoView))
        return collection
    }()
    
    
    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        
        // SubViews
//        tagsView.removeFromSuperview()
        headerView.addSubview(detailButton)
        content.addSubview(photoCollection)

        // Autolayout
        detailButton.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(13)
            make.right.equalTo(headerView.snp_right).offset(-12)
            make.centerY.equalTo(headerView.snp_centerY)
        }
        content.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(headerView.snp_bottom).offset(10)
            make.left.equalTo(contentView.snp_left)
            make.right.equalTo(contentView.snp_right)
            make.bottom.equalTo(contentView.snp_bottom).offset(-10)
        }
        photoCollection.snp_makeConstraints { (make) in
            make.left.equalTo(content)
            make.right.equalTo(content)
            make.top.equalTo(content)
            make.height.equalTo(MalaLayout_DetailPhotoWidth)
            make.bottom.equalTo(content)
        }
    }
 
    
    // MARK: - Events Response
    ///  查看相册按钮点击事件
    @objc private func detailButtonDidTap() {
        // 相册
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_PushPhotoBrowser, object: "browser")
    }
}