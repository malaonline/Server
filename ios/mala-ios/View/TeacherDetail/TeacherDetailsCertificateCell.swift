//
//  TeacherDetailsCertificateCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailsCertificateCell: TeacherDetailBaseCell, MATabListViewDelegate {

    // MARK: - Property
    var models: [AchievementModel?] = [] {
        didSet {
            guard models.count != oldValue.count else {
                return
            }
            tagsView.labels = models.map { (model) -> String in
                return model?.title ?? "默认证书"
            }
        }
    }
    var images: [SKPhoto] = []
    
    
    // MARK: - Components
    /// 标签容器
    lazy var tagsView: TagListView = {
        let tagsView = TagListView(frame: CGRect(x: 0, y: 0, width: MalaLayout_CardCellWidth, height: 0))
        tagsView.labelBackgroundColor = MalaColor_FCDFB7_0
        tagsView.textColor = MalaColor_EF8F1D_0
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