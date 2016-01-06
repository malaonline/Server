//
//  TeacherDetailsBaseCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailsBaseCell: UITableViewCell {
    
    // MARK: - Components
    /// label for title
    lazy var title: UILabel = {
        let label = UILabel(title: MalaCommonString_Title)
        label.textColor = MalaDetailsCellTitleColor
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        return label
    }()
    
    /// custom accessoryView at the right corner
    weak var accessory: UIView?
    
    /// the truely container
    lazy var content: UIView = UIView()
    
    private lazy var tagsView: MATabListView = {
        let tagsView = MATabListView(frame: CGRect(x: 0, y: 0, width: UIScreen.mainScreen().bounds.size.width, height: MalaLayout_FontSize_12))
        self.content.addSubview(tagsView)
        tagsView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top)
            make.bottom.equalTo(self.content.snp_bottom)
            make.left.equalTo(self.content.snp_left)
            make.right.equalTo(self.content.snp_right)
//            make.height.equalTo(MalaLayout_FontSize_12)
        }
        return tagsView
    }()
    
    
    /// Strings like tags and certificates
    var labels: [String]? {
        didSet {
            for view in self.tagsView.subviews {
                view.removeFromSuperview()
            }
            self.tagsView.setTags(labels)
        }
    }
    
    
    // MARK: - Life Cycle
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private
    private func setupUserInterface() {
        
        // Style
//        title.backgroundColor = UIColor.grayColor()
//        content.backgroundColor = UIColor.lightGrayColor()
        
        
        // SubViews
        contentView.addSubview(title)
        contentView.addSubview(content)
        
        // Autolayout
        title.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_10)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_14)
            make.width.equalTo(100)
        }
        content.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.title.snp_bottom).offset(MalaLayout_Margin_12)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_15)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
        }
        
        
        if accessory != nil {
            addSubview(accessory!)
            accessory!.snp_makeConstraints(closure: { (make) -> Void in
                make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_10)
                make.left.equalTo(self.title.snp_right).offset(MalaLayout_Margin_10)
                make.height.equalTo(MalaLayout_FontSize_14)
                make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
            })
        }
        
        
    }
    
    
}
