//
//  CourseTableViewSectionHeader.swift
//  mala-ios
//
//  Created by 王新宇 on 16/6/14.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class CourseTableViewSectionHeader: UITableViewHeaderFooterView {

    // MARK: - Components
    /// 背景图片
    private lazy var backgroundImage: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "course_header"))
        return imageView
    }()
    /// 时间文本标签
    private lazy var dateLabel: UILabel = {
        let label = UILabel(
            text: "2077年8月",
            fontSize: 20,
            textColor: MalaColor_000000_0
        )
        return label
    }()
    
    // MARK: - Constructed
    override init(reuseIdentifier: String?) {
        super.init(reuseIdentifier: reuseIdentifier)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private
    private func setupUserInterface() {
        // Style
        contentView.clipsToBounds = true
        
        // SubViews
        contentView.addSubview(backgroundImage)
        contentView.addSubview(dateLabel)
        
        // AutoLayout
        backgroundImage.snp_makeConstraints { (make) in
            make.center.equalTo(contentView)
            make.width.equalTo(contentView)
            make.height.equalTo(backgroundImage.snp_width).multipliedBy(1.05)
        }
        dateLabel.snp_makeConstraints { (make) in
            make.height.equalTo(20)
            make.left.equalTo(contentView.snp_left).offset(70)
            make.bottom.equalTo(contentView.snp_bottom).offset(-20)
        }
    }
}