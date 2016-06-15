//
//  SingleCourseView.swift
//  mala-ios
//
//  Created by 王新宇 on 16/6/15.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class SingleCourseView: UIView {
    
    // MARK: - Property
    /// 单个课程数据模型
    var model: StudentCourseModel? {
        didSet {
            subjectLabel.text = String(format: "%@ %@", model?.grade ?? "", model?.subject ?? "")
            nameLabel.text = model?.teacher?.name
            timeLabel.text = String(format: "%@-%@", getDateString(model?.start, format: "HH:mm"), getDateString(model?.end, format: "HH:mm"))
            schoolLabel.text = model?.school
        }
    }
    
    
    // MARK: - Components
    /// 信息背景视图
    private lazy var headerBackground: UIView = {
        let view = UIView()
        view.layer.cornerRadius = 4
        view.layer.masksToBounds = true
        view.backgroundColor = MalaColor_CFCFCF_0
        return view
    }()
    /// 学科年级信息标签
    private lazy var subjectLabel: UILabel = {
        let label = UILabel(
            text: "学科",
            fontSize: 14,
            textColor: MalaColor_FFFFFF_9
        )
        return label
    }()
    /// 老师姓名标签
    private lazy var nameLabel: UILabel = {
        let label = UILabel(
            text: "老师姓名",
            fontSize: 14,
            textColor: MalaColor_FFFFFF_9
        )
        return label
    }()
    /// 上课时间图标
    private lazy var timeSlotIcon: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "comment_time"))
        return imageView
    }()
    /// 上课时间信息
    private lazy var timeLabel: UILabel = {
        let label = UILabel(
            text: "上课时间",
            fontSize: 13,
            textColor: MalaColor_6C6C6C_0
        )
        return label
    }()
    /// 上课地点图标
    private lazy var schoolIcon: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "comment_location"))
        return imageView
    }()
    /// 上课地点
    private lazy var schoolLabel: UILabel = {
        let label = UILabel(
            text: "上课地点",
            fontSize: 13,
            textColor: MalaColor_6C6C6C_0
        )
        return label
    }()
    
    // MARK: - Instance Method
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private
    private func setupUserInterface() {
        // SubView
        addSubview(headerBackground)
        headerBackground.addSubview(subjectLabel)
        headerBackground.addSubview(nameLabel)
        
        addSubview(timeSlotIcon)
        addSubview(timeLabel)
        addSubview(schoolIcon)
        addSubview(schoolLabel)
        
        // AutoLayout
        headerBackground.snp_makeConstraints { (make) in
            make.top.equalTo(self)
            make.height.equalTo(32)
            make.left.equalTo(self)
            make.right.equalTo(self)
        }
        subjectLabel.snp_makeConstraints { (make) in
            make.height.equalTo(14)
            make.left.equalTo(headerBackground.snp_left).offset(10)
            make.centerY.equalTo(headerBackground.snp_centerY)
        }
        nameLabel.snp_makeConstraints { (make) in
            make.height.equalTo(14)
            make.right.equalTo(headerBackground.snp_right).offset(-10)
            make.centerY.equalTo(headerBackground.snp_centerY)
        }
        timeSlotIcon.snp_makeConstraints { (make) in
            make.top.equalTo(headerBackground.snp_bottom).offset(10)
            make.left.equalTo(headerBackground.snp_left)
            make.width.equalTo(13)
            make.height.equalTo(13)
        }
        timeLabel.snp_makeConstraints { (make) in
            make.top.equalTo(timeSlotIcon.snp_top)
            make.left.equalTo(timeSlotIcon.snp_right).offset(5)
            make.height.equalTo(13)
        }
        schoolIcon.snp_makeConstraints { (make) in
            make.top.equalTo(timeSlotIcon.snp_bottom).offset(10)
            make.left.equalTo(headerBackground.snp_left)
            make.width.equalTo(13)
            make.height.equalTo(15)
        }
        schoolLabel.snp_makeConstraints { (make) in
            make.centerY.equalTo(schoolIcon.snp_centerY)
            make.left.equalTo(schoolIcon.snp_right).offset(5)
            make.height.equalTo(13)
        }
    }
}