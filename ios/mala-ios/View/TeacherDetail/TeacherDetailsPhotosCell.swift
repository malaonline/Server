//
//  TeacherDetailsPhotosCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit
import Kingfisher

class TeacherDetailsPhotosCell: MalaBaseCell {

    // MARK: - Property
    /// 图片URL数组
    var photos: [String] = [] {
        didSet {
            
            // 老师详情中照片最少为3张
            if photos.count >= 3 {
                leftPhoto.kf_setImageWithURL(NSURL(string: photos[0]) ?? NSURL(), placeholderImage: nil)
                centerPhoto.kf_setImageWithURL(NSURL(string: photos[1]) ?? NSURL(), placeholderImage: nil)
                rightPhoto.kf_setImageWithURL(NSURL(string: photos[2]) ?? NSURL(), placeholderImage: nil)
            }
            
            // 加载图片对象
            images.removeAll()
            for imageURL in photos {
                let image = SKPhoto.photoWithImageURL(imageURL)
                image.shouldCachePhotoURLImage = true
                images.append(image)
            }
        }
    }
    /// 图片浏览器 - 图片对象
    var images: [SKPhoto] = [SKPhoto]()
    
    
    // MARK: - Components
    private lazy var leftPhoto: UIImageView = UIImageView.placeHolder()
    private lazy var centerPhoto: UIImageView = UIImageView.placeHolder()
    private lazy var rightPhoto: UIImageView = UIImageView.placeHolder()
    
    
    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        configure()
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // SubViews
        content.addSubview(leftPhoto)
        content.addSubview(centerPhoto)
        content.addSubview(rightPhoto)

        // Autolayout
        leftPhoto.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(MalaLayout_DetailPhotoHeight)
            make.width.equalTo(MalaLayout_DetailPhotoWidth)
            make.top.equalTo(self.content.snp_top)
            make.bottom.equalTo(self.content.snp_bottom)
            make.left.equalTo(self.content.snp_left)
        }
        centerPhoto.snp_makeConstraints { (make) -> Void in
            make.width.equalTo(MalaLayout_DetailPhotoWidth)
            make.height.equalTo(self.leftPhoto.snp_height)
            make.top.equalTo(self.leftPhoto.snp_top)
            make.left.equalTo(self.leftPhoto.snp_right).offset(MalaLayout_Margin_5)
        }
        rightPhoto.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(self.centerPhoto.snp_height)
            make.top.equalTo(self.centerPhoto.snp_top)
            make.left.equalTo(self.centerPhoto.snp_right).offset(MalaLayout_Margin_5)
            make.right.equalTo(self.content.snp_right)
        }
    }
    
    private func configure() {
        rightArrow.addTarget(self, action: "detailButtonDidTap", forControlEvents: .TouchUpInside)
    }
 
    
    // MARK: - Events Response
    ///  查看相册按钮点击事件
    @objc private func detailButtonDidTap() {
        
        println("查看相册按钮点击事件")
        
        let browser = MalaPhotoBrowser()
        browser.imageURLs = photos
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_PushPhotoBrowser, object: browser)
    }
}