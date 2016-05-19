//
//  LearningReportTitlePageCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/19.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class LearningReportTitlePageCell: MalaBaseCardCell {
    
    // MARK: - Components
    /// 标题标签
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "麻辣老师学生学习报告样本",
            fontSize: 16,
            textColor: MalaColor_5E5E5E_0
        )
        return label
    }()
    /// 标题背景图
    private lazy var titleBackground: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "reportTitle_background"))
        return imageView
    }()
    /// 日期范围标签
    private lazy var dateLabel: UILabel = {
        let label = UILabel(
            text: "2016年4月05～2016年5月30",
            fontSize: 10,
            textColor: UIColor.whiteColor()
        )
        label.backgroundColor = MalaColor_FDAF6B_0
        label.textAlignment = .Center
        return label
    }()
    /// 文件夹图片
    private lazy var folderImage: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "folder_icon"))
        return imageView
    }()
    /// "学生姓名"文字
    private lazy var nameString: UILabel = {
        let label = UILabel(
            text: "学生姓名：",
            fontSize: 12,
            textColor: MalaColor_8DBEDE_0
        )
        return label
    }()
    /// 学生姓名标签
    private lazy var nameLabel: UILabel = {
        let label = UILabel(
            text: "姓名",
            fontSize: 12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    /// 姓名分割线
    private lazy var nameSeparator: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "label_rightSeparator"))
        return imageView
    }()
    /// "所在年纪"文字
    private lazy var gradeString: UILabel = {
        let label = UILabel(
            text: "所在年级：",
            fontSize: 12,
            textColor: MalaColor_8DBEDE_0
        )
        return label
    }()
    /// 所在年级标签
    private lazy var gradeLabel: UILabel = {
        let label = UILabel(
            text: "初中二年级",
            fontSize: 12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    /// 年级分割线
    private lazy var gradeSeparator: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "label_leftSeparator"))
        return imageView
    }()
    
    
    // MARK: - Instance Method
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        contentView.backgroundColor = MalaColor_F2F2F2_0
        
        // SubViews
        layoutView.addSubview(titleLabel)
        layoutView.addSubview(titleBackground)
        layoutView.addSubview(dateLabel)
        layoutView.addSubview(folderImage)
        layoutView.addSubview(nameString)
        layoutView.addSubview(nameLabel)
        layoutView.addSubview(nameSeparator)
        layoutView.addSubview(gradeString)
        layoutView.addSubview(gradeLabel)
        layoutView.addSubview(gradeSeparator)
        
        // Autolayout
        titleBackground.snp_makeConstraints { (make) in
            make.center.equalTo(titleLabel.snp_center)
            make.width.equalTo(titleLabel.snp_width).offset(16)
        }
        titleLabel.snp_makeConstraints { (make) in
            make.height.equalTo(20)
            make.centerX.equalTo(layoutView.snp_centerX)
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.09)
        }
        dateLabel.snp_makeConstraints { (make) in
            make.width.equalTo(160)
            make.height.equalTo(24)
            make.centerX.equalTo(layoutView.snp_centerX)
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.18)
        }
        folderImage.snp_makeConstraints { (make) in
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.32)
            make.centerX.equalTo(layoutView.snp_centerX)
            make.width.equalTo(100.5)
            make.height.equalTo(142.5)
        }
        nameString.snp_makeConstraints { (make) in
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.73)
            make.height.equalTo(12)
            make.right.equalTo(layoutView.snp_centerX)
        }
        nameLabel.snp_makeConstraints { (make) in
            make.top.equalTo(nameString.snp_top)
            make.left.equalTo(layoutView.snp_centerX)
        }
        nameSeparator.snp_makeConstraints { (make) in
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.76)
            make.height.equalTo(6)
            make.left.equalTo(layoutView.snp_left)
            make.right.equalTo(nameLabel.snp_right).offset(20)
        }
        gradeString.snp_makeConstraints { (make) in
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.81)
            make.height.equalTo(12)
            make.right.equalTo(layoutView.snp_centerX)
        }
        gradeLabel.snp_makeConstraints { (make) in
            make.top.equalTo(gradeString.snp_top)
            make.left.equalTo(layoutView.snp_centerX)
        }
        gradeSeparator.snp_makeConstraints { (make) in
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.84)
            make.height.equalTo(6)
            make.left.equalTo(gradeString.snp_left).offset(-20)
            make.right.equalTo(layoutView.snp_right)
        }
    }
}
