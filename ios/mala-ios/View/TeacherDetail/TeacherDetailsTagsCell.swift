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
            tagsView.labels = labels
        }
    }
    
    
    // MARK: - Components
    /// 标签容器
    lazy var tagsView: ThemeTagListView = {
        let tagsView = ThemeTagListView()
        return tagsView
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
        tagsView.imageName = "tags_icon"
        
        // SubViews
        content.addSubview(tagsView)
        
        // AutoLayout
        tagsView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(content.snp_top)
            make.left.equalTo(content.snp_left)
            make.bottom.equalTo(content.snp_bottom)
            make.height.equalTo(25)
            make.right.equalTo(content.snp_right)
        }
    }
}