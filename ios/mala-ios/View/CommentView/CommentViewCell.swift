//
//  CommentViewCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/6/7.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class CommentViewCell: UITableViewCell {

    // MARK: - Property
    /// 单个课程模型
    var model: StudentCourseModel? {
        didSet {
            // 设置单个课程模型
            teacherLabel.text = model?.teacher?.name
            subjectLabel.text = (model?.grade ?? "") + " " + (model?.subject ?? "")
            setDateString()
            schoolLabel.text = model?.school
            
            // 老师头像
            avatarView.kf_setImageWithURL(model?.teacher?.avatar ?? NSURL(), placeholderImage: UIImage(named: "profileAvatar_placeholder"))
            
            // 课程评价状态
            if let _ = model?.is_expired {
                // 过期
                statusIcon.enabled = false
                
            }else if let _ = model?.comment {
                // 已评价
                statusIcon.highlighted = true
                
            }else {
                // 未评价
                statusIcon.enabled = true
            }
        }
    }
    
    // MARK: - Components
    /// 主要布局容器
    private lazy var content: UIView = {
        let view = UIView.separator(UIColor.whiteColor())
        view.layer.shadowOffset = CGSize(width: 0, height: MalaScreenOnePixel)
        view.layer.shadowColor = MalaColor_D7D7D7_0.CGColor
        view.layer.shadowOpacity = 1
        return view
    }()
    /// 课程信息布局容器
    private lazy var mainLayoutView: UIView = {
        let view = UIView()
        return view
    }()
    /// 课程评价状态标示
    private lazy var statusIcon: UIButton = {
        let button = UIButton()
        button.setBackgroundImage(UIImage(named: "uncomment"), forState: .Normal)
        button.setBackgroundImage(UIImage(named: "commented"), forState: .Highlighted)
        button.setBackgroundImage(UIImage(named: "comment_expired"), forState: .Disabled)
        button.setTitle("待 评", forState: .Normal)
        button.setTitle("已 评", forState: .Highlighted)
        button.setTitle("过 期", forState: .Disabled)
        button.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        button.userInteractionEnabled = false
        return button
    }()
    /// 老师头像
    private lazy var avatarView: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "profileAvatar_placeholder"))
        imageView.contentMode = .ScaleAspectFill
        imageView.layer.cornerRadius = 55/2
        imageView.layer.masksToBounds = true
        return imageView
    }()
    /// 老师姓名图标
    private lazy var teacherIcon: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "comment_teacher_normal"))
        return imageView
    }()
    /// 老师姓名
    private lazy var teacherLabel: UILabel = {
        let label = UILabel(
            text: "教师姓名",
            fontSize: MalaLayout_FontSize_13,
            textColor: MalaColor_8FBCDD_0
        )
        return label
    }()
    /// 学科信息图标
    private lazy var subjectIcon: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "comment_subject"))
        return imageView
    }()
    /// 学科信息
    private lazy var subjectLabel: UILabel = {
        let label = UILabel(
            text: "年级-学科",
            fontSize: MalaLayout_FontSize_13,
            textColor: MalaColor_6C6C6C_0
        )
        return label
    }()
    /// 上课时间图标
    private lazy var timeSlotIcon: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "comment_time"))
        return imageView
    }()
    /// 上课日期信息
    private lazy var timeSlotLabel: UILabel = {
        let label = UILabel(
            text: "上课时间",
            fontSize: MalaLayout_FontSize_13,
            textColor: MalaColor_6C6C6C_0
        )
        return label
    }()
    /// 上课时间信息
    private lazy var timeLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: MalaLayout_FontSize_13,
            textColor: MalaColor_939393_0
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
            fontSize: MalaLayout_FontSize_13,
            textColor: MalaColor_6C6C6C_0
        )
        return label
    }()
    /// 中部分割线
    private lazy var separatorLine: UIView = {
        let view = UIView.line(MalaColor_DADADA_0)
        return view
    }()
    
    /// 底部价格及操作布局容器
    private lazy var bottomLayoutView: UIView = {
        let view = UIView()
        return view
    }()
    
    
    // MARK: - Constructed
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
        contentView.backgroundColor = MalaColor_EDEDED_0
        
        // SubViews
        contentView.addSubview(content)
        content.addSubview(mainLayoutView)
        mainLayoutView.addSubview(avatarView)
        mainLayoutView.addSubview(statusIcon)
        
        mainLayoutView.addSubview(teacherIcon)
        mainLayoutView.addSubview(teacherLabel)
        mainLayoutView.addSubview(subjectIcon)
        mainLayoutView.addSubview(subjectLabel)
        mainLayoutView.addSubview(timeSlotIcon)
        mainLayoutView.addSubview(timeSlotLabel)
        mainLayoutView.addSubview(timeLabel)
        mainLayoutView.addSubview(schoolIcon)
        mainLayoutView.addSubview(schoolLabel)
        
        
        // Autolayout
        content.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(6)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-6)
            make.right.equalTo(self.contentView.snp_right).offset(-12)
            make.height.equalTo(300)
        }
        mainLayoutView.snp_makeConstraints { (make) in
            make.top.equalTo(content.snp_top)
            make.height.equalTo(content.snp_height).multipliedBy(0.74)
            make.left.equalTo(content)
            make.right.equalTo(content)
        }
        statusIcon.snp_makeConstraints { (make) in
            make.right.equalTo(mainLayoutView.snp_right).offset(-30)
            make.top.equalTo(mainLayoutView.snp_top).offset(-6)
        }
        avatarView.snp_makeConstraints { (make) in
            make.centerX.equalTo(statusIcon.snp_centerX)
            make.top.equalTo(statusIcon.snp_bottom).offset(10)
            make.height.equalTo(55)
            make.width.equalTo(55)
        }
        
        teacherIcon.snp_makeConstraints { (make) in
            make.top.equalTo(mainLayoutView.snp_top).offset(18)
            make.left.equalTo(mainLayoutView.snp_left).offset(12)
            make.height.equalTo(14)
            make.width.equalTo(14)
        }
        teacherLabel.snp_makeConstraints { (make) in
            make.top.equalTo(teacherIcon.snp_top)
            make.left.equalTo(teacherIcon.snp_right).offset(10)
            make.height.equalTo(MalaLayout_Margin_13)
        }
        subjectIcon.snp_makeConstraints { (make) in
            make.top.equalTo(teacherIcon.snp_bottom).offset(10)
            make.left.equalTo(mainLayoutView.snp_left).offset(12)
            make.height.equalTo(14)
            make.width.equalTo(14)
        }
        subjectLabel.snp_makeConstraints { (make) in
            make.top.equalTo(subjectIcon.snp_top)
            make.left.equalTo(subjectIcon.snp_right).offset(10)
            make.height.equalTo(13)
        }
        timeSlotIcon.snp_makeConstraints { (make) in
            make.top.equalTo(subjectIcon.snp_bottom).offset(10)
            make.left.equalTo(mainLayoutView.snp_left).offset(12)
            make.height.equalTo(14)
            make.width.equalTo(14)
        }
        timeSlotLabel.snp_makeConstraints { (make) in
            make.top.equalTo(timeSlotIcon.snp_top)
            make.left.equalTo(timeSlotIcon.snp_right).offset(10)
            make.height.equalTo(13)
        }
        timeLabel.snp_makeConstraints { (make) in
            make.top.equalTo(timeSlotLabel)
            make.left.equalTo(timeSlotLabel.snp_right).offset(5)
            make.height.equalTo(13)
        }
        schoolIcon.snp_makeConstraints { (make) in
            make.top.equalTo(timeSlotIcon.snp_bottom).offset(10)
            make.left.equalTo(mainLayoutView.snp_left).offset(12)
            make.height.equalTo(15)
            make.width.equalTo(14)
        }
        schoolLabel.snp_makeConstraints { (make) in
            make.top.equalTo(schoolIcon.snp_top)
            make.left.equalTo(schoolIcon.snp_right).offset(10)
            make.height.equalTo(13)
        }
    }
    
    ///  设置日期样式
    private func setDateString() {
        
        guard let start = model?.start else {
            return
        }
        
        let dateString = getDateString(start, format: "yyyy-MM-dd")
        let startString = getDateString(start, format: "HH:mm")
        let endString = getDateString(date: NSDate(timeIntervalSince1970: start).dateByAddingHours(2), format: "HH:mm")
        
        timeSlotLabel.text = String(format: "%@", dateString)
        timeLabel.text = String(format: "%@-%@", startString, endString)
    }
    
    ///  设置过期样式
    private func setStyleExpired() {
        
    }
    
    ///  设置普通样式（包含可使用、已使用）
    private func setStyleNormal() {
        
    }
}