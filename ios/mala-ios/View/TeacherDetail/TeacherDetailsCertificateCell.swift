//
//  TeacherDetailsCertificateCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailsCertificateCell: MalaBaseCell, MATabListViewDelegate {

    // MARK: - Property
    var models: [AchievementModel?] = [] {
        didSet {
            
            labels.removeAll()
            
            for model in models {
                labels.append(model?.title ?? "")
                let photo = SKPhoto.photoWithImageURL(model?.img?.absoluteString ?? "")
                photo.caption = model?.title ?? ""
                images.append(photo)
            }
        }
    }
    var images: [SKPhoto] = []
    
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
        self.tagsView.delegate = self
    }
    
    
    // MARK: - Delegate
    ///  标签点击事件
    func tagDidTap(sender: UILabel, tabListView: MATabListView) {
        
        /// 图片浏览器
        let browser = SKPhotoBrowser(photos: images)
        browser.initializePageIndex(sender.tag-1)
        browser.statusBarStyle = nil
        browser.forceDismiss = true
        browser.displayAction = false
        browser.bounceAnimation = false
        browser.displayDeleteButton = false
        browser.displayBackAndForwardButton = false
        browser.navigationController?.navigationBarHidden = true
        
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_PushPhotoBrowser, object: browser)
    }
    
    func tagShourldDisplayBorder(sender: UILabel, tabListView: MATabListView) -> Bool {
        return true
    }
}