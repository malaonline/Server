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
            if model?.comment != nil {
                // 已评价
                setStyleCommented()
                
            }else if model?.is_expired == true {
                // 过期
                setStyleExpired()
                
            }else {
                // 未评价
                setStyleNoComments()
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
        button.titleLabel?.font = UIFont.systemFontOfSize(12)
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
            fontSize: 13,
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
            fontSize: 13,
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
            fontSize: 13,
            textColor: MalaColor_6C6C6C_0
        )
        return label
    }()
    /// 上课时间信息
    private lazy var timeLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: 13,
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
            fontSize: 13,
            textColor: MalaColor_6C6C6C_0
        )
        return label
    }()
    /// 中部分割线
    private lazy var separatorLine: UIView = {
        let view = UIView.line(MalaColor_DADADA_0)
        return view
    }()
    /// 评分面板
    private lazy var floatRating: FloatRatingView = {
        let floatRating = FloatRatingView()
        floatRating.backgroundColor = UIColor.whiteColor()
        floatRating.editable = false
        return floatRating
    }()
    /// 底部布局容器
    private lazy var bottomLayoutView: UIView = {
        let view = UIView()
        return view
    }()
    /// 已过期文字标签
    private lazy var expiredLabel: UILabel = {
        let label = UILabel(
            text: "评价已过期",
            fontSize: 12,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    /// 评论按钮
    private lazy var commentButton: UIButton = {
        let button = UIButton()
        button.layer.borderColor = MalaColor_E26254_0.CGColor
        button.layer.borderWidth = MalaScreenOnePixel
        button.layer.cornerRadius = 3
        button.layer.masksToBounds = true
        
        button.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(MalaColor_FFF0EE_0), forState: .Highlighted)
        button.setTitle("去评价", forState: .Normal)
        button.setTitleColor(MalaColor_E26254_0, forState: .Normal)
        button.titleLabel?.font = UIFont.systemFontOfSize(12)
        button.addTarget(self, action: #selector(CommentViewCell.toComment), forControlEvents: .TouchUpInside)
        button.hidden = true
        return button
    }()
    /// 查看评论按钮
    private lazy var showCommentButton: UIButton = {
        let button = UIButton()
        button.layer.borderColor = MalaColor_82B4D9_0.CGColor
        button.layer.borderWidth = MalaScreenOnePixel
        button.layer.cornerRadius = 3
        button.layer.masksToBounds = true
        
        button.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(MalaColor_E6F1FC_0), forState: .Highlighted)
        button.setTitle("查看评价", forState: .Normal)
        button.setTitleColor(MalaColor_82B4D9_0, forState: .Normal)
        button.titleLabel?.font = UIFont.systemFontOfSize(12)
        button.addTarget(self, action: #selector(CommentViewCell.showComment), forControlEvents: .TouchUpInside)
        button.hidden = true
        return button
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
        content.addSubview(separatorLine)
        content.addSubview(bottomLayoutView)
        content.addSubview(floatRating)
        
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
        
        bottomLayoutView.addSubview(expiredLabel)
        bottomLayoutView.addSubview(commentButton)
        bottomLayoutView.addSubview(showCommentButton)
        
        // Autolayout
        content.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(6)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-6)
            make.right.equalTo(self.contentView.snp_right).offset(-12)
        }
        mainLayoutView.snp_makeConstraints { (make) in
            make.top.equalTo(content.snp_top)
            make.left.equalTo(content.snp_left)
            make.height.equalTo(252)
            make.right.equalTo(content.snp_right)
            make.bottom.equalTo(separatorLine.snp_top)
        }
        separatorLine.snp_makeConstraints { (make) in
            make.top.equalTo(mainLayoutView.snp_bottom).offset(14)
            make.height.equalTo(MalaScreenOnePixel)
            make.left.equalTo(content).offset(5)
            make.right.equalTo(content).offset(-5)
        }
        floatRating.snp_makeConstraints { (make) in
            make.center.equalTo(separatorLine.snp_center)
            make.height.equalTo(20)
            make.width.equalTo(80)
        }
        bottomLayoutView.snp_makeConstraints { (make) in
            make.top.equalTo(separatorLine.snp_bottom)
            make.bottom.equalTo(content.snp_bottom)
            make.left.equalTo(content.snp_left)
            make.right.equalTo(content.snp_right)
            make.height.equalTo(50)
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
            make.top.equalTo(mainLayoutView.snp_top).offset(14)
            make.left.equalTo(mainLayoutView.snp_left).offset(12)
            make.height.equalTo(14)
            make.width.equalTo(14)
        }
        teacherLabel.snp_makeConstraints { (make) in
            make.top.equalTo(teacherIcon.snp_top)
            make.left.equalTo(teacherIcon.snp_right).offset(10)
            make.height.equalTo(13)
        }
        subjectIcon.snp_makeConstraints { (make) in
            make.top.equalTo(teacherIcon.snp_bottom).offset(14)
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
            make.top.equalTo(subjectIcon.snp_bottom).offset(14)
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
            make.top.equalTo(timeSlotIcon.snp_bottom).offset(14)
            make.left.equalTo(mainLayoutView.snp_left).offset(12)
            make.height.equalTo(15)
            make.width.equalTo(14)
        }
        schoolLabel.snp_makeConstraints { (make) in
            make.top.equalTo(schoolIcon.snp_top)
            make.left.equalTo(schoolIcon.snp_right).offset(10)
            make.height.equalTo(13)
            make.bottom.equalTo(mainLayoutView.snp_bottom).offset(-14)
        }
        expiredLabel.snp_makeConstraints { (make) in
            make.height.equalTo(12)
            make.center.equalTo(bottomLayoutView.snp_center)
        }
        commentButton.snp_makeConstraints { (make) in
            make.center.equalTo(bottomLayoutView.snp_center)
            make.width.equalTo(96)
            make.height.equalTo(24)
        }
        showCommentButton.snp_makeConstraints { (make) in
            make.center.equalTo(bottomLayoutView.snp_center)
            make.width.equalTo(96)
            make.height.equalTo(24)
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
        println("课程评价状态 - 过期")
        
        showCommentButton.hidden = true
        commentButton.hidden = true
        
        statusIcon.highlighted = false
        statusIcon.enabled = false
        expiredLabel.hidden = false
        floatRating.hidden = true
    }
    
    ///  设置已评论样式
    private func setStyleCommented() {
        println("课程评价状态 - 已评价")
        
        commentButton.hidden = true
        showCommentButton.hidden = false
        
        statusIcon.enabled = true
        statusIcon.highlighted = true
        expiredLabel.hidden = true
        floatRating.hidden = false
        floatRating.rating = Float((model?.comment?.score) ?? 0)
    }
    
    ///  设置待评论样式
    private func setStyleNoComments() {
        println("课程评价状态 - 待评价")
        
        commentButton.hidden = false
        showCommentButton.hidden = true
        
        statusIcon.highlighted = false
        statusIcon.enabled = true
        expiredLabel.hidden = true
        floatRating.hidden = true
    }
    
    
    // MARK: - Event Response
    ///  去评价
    @objc private func toComment() {
        let commentWindow = CommentViewWindow(contentView: UIView())
        
        commentWindow.finishedAction = { [weak self] in
            self?.setStyleCommented()
        }
        
        commentWindow.model = self.model ?? StudentCourseModel()
        commentWindow.isJustShow = false
        commentWindow.show()
    }
    ///  查看评价
    @objc private func showComment() {
        let commentWindow = CommentViewWindow(contentView: UIView())
        commentWindow.model = self.model ?? StudentCourseModel()
        commentWindow.isJustShow = true
        commentWindow.show()
    }
    
    override func prepareForReuse() {
        super.prepareForReuse()
        statusIcon.highlighted = false
        statusIcon.enabled = true
    }
}