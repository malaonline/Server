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
            
            // 课程评价状态
            
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
    /// "老师姓名"文字
    private lazy var teacherNameLabel: UILabel = {
        let label = UILabel()
        label.text = "教师姓名："
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = MalaColor_636363_0
        return label
    }()
    /// 老师姓名
    private lazy var teacherNameString: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = MalaColor_939393_0
        return label
    }()
    /// "课程名称"文字
    private lazy var subjectLabel: UILabel = {
        let label = UILabel()
        label.text = "课程名称："
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = MalaColor_636363_0
        return label
    }()
    /// 课程名称
    private lazy var subjectString: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = MalaColor_939393_0
        return label
    }()
    /// "上课地点"文字
    private lazy var schoolLabel: UILabel = {
        let label = UILabel()
        label.text = "上课地点："
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = MalaColor_636363_0
        return label
    }()
    /// 课程名称
    private lazy var schoolString: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = MalaColor_939393_0
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
        
        // Autolayout
        content.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(6)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-6)
            make.right.equalTo(self.contentView.snp_right).offset(-12)
            make.height.equalTo(content.snp_width).multipliedBy(0.62)
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
    }
    
    ///  设置过期样式
    private func setStyleExpired() {
        
    }
    
    ///  设置普通样式（包含可使用、已使用）
    private func setStyleNormal() {
        
    }
}