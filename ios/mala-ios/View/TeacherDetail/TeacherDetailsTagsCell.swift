//
//  TeacherDetailsTagsCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailsTagsCell: TeacherDetailBaseCell {

    // MARK: - Property
    /// 标签字符串数组
    var labels: [String] = [] {
        didSet {
            if labels != oldValue {
                setupLabels()
            }
        }
    }
    
    
    // MARK: - Components
    /// 标签容器
    lazy var tagsView: TagListView = {
        let tagsView = TagListView()
        return tagsView
    }()
    private lazy var iconView: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "tags_icon"))
        return imageView
    }()
    
    
    // MARK: - Instance Method
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        
        
        // SubViews
        content.addSubview(iconView)
        content.addSubview(tagsView)
        
        // AutoLayout
        tagsView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(content.snp_top)
            make.left.equalTo(iconView.snp_right).offset(12)
            make.bottom.equalTo(content.snp_bottom)
            make.height.equalTo(25)
            make.right.equalTo(content.snp_right)
        }
        iconView.snp_makeConstraints { (make) in
            make.left.equalTo(content.snp_left)
            make.centerY.equalTo(content.snp_centerY)
            make.height.equalTo(21)
            make.width.equalTo(21)
        }
    }
    
    private func setupLabels() {
        tagsView.reset()
        for string in labels {
            tagsView.addTag(string, backgroundColor: MalaColor_BCD0DE_0, textColor: MalaColor_5789AC_0)
        }
    }
}
