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

            // 加载图片展示在老师详情页面
            if photos.count >= 1 {
                leftPhoto.kf_setImageWithURL(NSURL(string: photos[0]) ?? NSURL(), placeholderImage: nil)
            }
            
            if photos.count >= 2 {
                centerPhoto.kf_setImageWithURL(NSURL(string: photos[1]) ?? NSURL(), placeholderImage: nil)
            }
            
            if photos.count >= 3 {
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
    private lazy var leftPhoto: UIImageView = {
        let leftPhoto =  UIImageView.placeHolder()
        leftPhoto.tag = 0
        leftPhoto.userInteractionEnabled = true
        leftPhoto.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(TeacherDetailsPhotosCell.imagesDidTap(_:))))
        return leftPhoto
    }()
    private lazy var centerPhoto: UIImageView = {
        let centerPhoto =  UIImageView.placeHolder()
        centerPhoto.tag = 1
        centerPhoto.userInteractionEnabled = true
        centerPhoto.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(TeacherDetailsPhotosCell.imagesDidTap(_:))))
        return centerPhoto
    }()
    private lazy var rightPhoto: UIImageView = {
        let rightPhoto =  UIImageView.placeHolder()
        rightPhoto.tag = 2
        rightPhoto.userInteractionEnabled = true
        rightPhoto.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(TeacherDetailsPhotosCell.imagesDidTap(_:))))
        return rightPhoto
    }()
    
    
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
            make.left.equalTo(self.leftPhoto.snp_right).offset(5)
        }
        rightPhoto.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(self.centerPhoto.snp_height)
            make.top.equalTo(self.centerPhoto.snp_top)
            make.left.equalTo(self.centerPhoto.snp_right).offset(5)
            make.right.equalTo(self.content.snp_right)
        }
    }
    
    private func configure() {
//        rightArrow.addTarget(self, action: #selector(TeacherDetailsPhotosCell.detailButtonDidTap), forControlEvents: .TouchUpInside)
    }
 
    
    // MARK: - Events Response
    ///  查看相册按钮点击事件
    @objc private func detailButtonDidTap() {
        
        // 相册
        let browser = MalaPhotoBrowser()
        browser.imageURLs = photos
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_PushPhotoBrowser, object: browser)
    }
    ///  图片点击事件
    @objc private func imagesDidTap(gesture: UITapGestureRecognizer) {
        
        /// 确保是imageView触发手势，且imageView图片存在
        guard let imageView = gesture.view as? UIImageView, originImage = imageView.image else {
            return
        }
        
        /// 图片浏览器
        let browser = SKPhotoBrowser(originImage: originImage, photos: images, animatedFromView: imageView)
        browser.initializePageIndex(imageView.tag)
        browser.displayAction = false
        browser.displayBackAndForwardButton = false
        browser.displayDeleteButton = false
        browser.statusBarStyle = nil
        browser.bounceAnimation = false
        browser.navigationController?.navigationBarHidden = true
        
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_PushPhotoBrowser, object: browser)
    }
}