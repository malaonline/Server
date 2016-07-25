//
//  TeacherDetailsCertificateCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailsCertificateCell: MalaBaseCell {

    // MARK: - Property
    var models: [AchievementModel?] = [] {
        didSet {
            guard models.count != oldValue.count else {
                return
            }
            
            /// 解析数据
            for model in models {
                self.labels.append("  "+(model?.title ?? "默认证书"))
                let photo = SKPhoto.photoWithImageURL(model?.img?.absoluteString ?? "")
                photo.caption = model?.title ?? "默认证书"
                images.append(photo)
            }
            tagsView.labels = self.labels
        }
    }
    var labels: [String] = []
    var images: [SKPhoto] = []
    
    
    // MARK: - Components
    /// 标签容器
    lazy var tagsView: TagListView = {
        let tagsView = TagListView(frame: CGRect(x: 0, y: 0, width: MalaLayout_CardCellWidth, height: 0))
        tagsView.labelBackgroundColor = MalaColor_FCDFB7_0
        tagsView.textColor = MalaColor_EF8F1D_0
        tagsView.iconName = "image_icon"
        tagsView.commonTarget = self
        tagsView.commonAction = #selector(TeacherDetailsCertificateCell.tagDidTap(_:))
        return tagsView
    }()

    
    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        configure()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configure() {
        
        content.addSubview(tagsView)
        content.snp_updateConstraints { (make) -> Void in
            make.bottom.equalTo(contentView.snp_bottom).offset(-10)
        }
        tagsView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(content.snp_top)
            make.left.equalTo(content.snp_left)
            make.width.equalTo(MalaLayout_CardCellWidth)
            make.bottom.equalTo(content.snp_bottom)
        }
    }
    
    
    // MARK: - Delegate
    ///  标签点击事件
    func tagDidTap(sender: UITapGestureRecognizer) {
        
        /// 图片浏览器
        if let index = sender.view?.tag {
            let browser = SKPhotoBrowser(photos: images)
            browser.initializePageIndex(index)
            browser.statusBarStyle = nil
            browser.forceDismiss = true
            browser.displayAction = false
            browser.bounceAnimation = false
            browser.displayDeleteButton = false
            browser.displayBackAndForwardButton = false
            browser.navigationController?.navigationBarHidden = true
            
            NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_PushPhotoBrowser, object: browser)
        }
    }
}