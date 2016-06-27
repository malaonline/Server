//
//  TeacherDetailsHeaderView.swift
//  mala-ios
//
//  Created by Elors on 1/7/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit
import SnapKit
import Kingfisher

class TeacherDetailsHeaderView: UIView {

    // MARK: - Property
    /// 教师头像URL
    var avatar: String = "" {
        didSet{
            guard let url = NSURL(string: avatar) else {
                println("TeacherDetailsHeaderView - AvatarURL Format Error")
                return
            }
            self.avatarView.kf_setImageWithURL(url, placeholderImage: UIImage(named: "avatar_placeholder"))
        }
    }
    /// 教师姓名
    var name: String = "老师姓名" {
        didSet{
            self.nameLabel.text = name
            self.nameLabel.sizeToFit()
        }
    }
    /// 教师性别
    var gender: String = "" {
        didSet{
            switch gender {
            case "f": genderIcon.image = UIImage(named: "gender_female")
            case "m": genderIcon.image = UIImage(named: "gender_male")
            default: genderIcon.image = UIImage(named: "")
            }
        }
    }
    /// 教授学科
    var subject: String = "学科" {
        didSet {
            subjectLabel.text = subject
        }
    }
    /// 最小价格
    var minPrice: Int = 0 {
        didSet{
            self.priceLabel.text = String(MinPrice: minPrice.money, MaxPrice: maxPrice.money)
        }
    }
    /// 最大价格
    var maxPrice: Int = 0 {
        didSet{
            self.priceLabel.text = String(MinPrice: minPrice.money, MaxPrice: maxPrice.money)
        }
    }
    
    
    // MARK: - Components
    /// 内部控件容器（注意本类继承于 UIView 而非 UITableViewCell）
    private lazy var contentView: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.whiteColor()
        return view
    }()
    /// 头像显示控件
    private lazy var avatarView: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "avatar_placeholder"))
        imageView.layer.cornerRadius = (MalaLayout_AvatarSize-5)*0.5
        imageView.layer.masksToBounds = true
        imageView.contentMode = .ScaleAspectFill
        return imageView
    }()
    /// 头像背景
    private lazy var avatarBackground: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.whiteColor()
        view.layer.cornerRadius = MalaLayout_AvatarSize*0.5
        view.layer.masksToBounds = true
        return view
    }()
    /// 会员图标显示控件
    private lazy var vipIconView: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "vip_icon"))
        imageView.layer.cornerRadius = MalaLayout_VipIconSize*0.5
        imageView.layer.masksToBounds = true
        imageView.layer.borderWidth = 1.0
        imageView.layer.borderColor = UIColor.whiteColor().CGColor
        return imageView
    }()
    /// 老师姓名label
    private lazy var nameLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(16)
        return label
    }()
    /// 老师性别Icon
    private lazy var genderIcon: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "gender_female"))
        return imageView
    }()
    /// 科目label
    private lazy var subjectLabel: UILabel = {
        let label = UILabel()
        label.textColor = MalaColor_939393_0
        label.font = UIFont.systemFontOfSize(12)
        return label
    }()
    /// 价格label
    private lazy var priceLabel: UILabel = {
        let label = UILabel()
        label.textColor = MalaColor_939393_0
        label.font = UIFont.systemFontOfSize(12)
        label.textAlignment = .Left
        return label
    }()

    
    // MARK: - Constructed
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
        self.backgroundColor = UIColor.clearColor()
        
        // SubViews
        self.addSubview(contentView)
        self.contentView.addSubview(avatarBackground)
        self.contentView.addSubview(avatarView)
        self.contentView.addSubview(vipIconView)
        self.contentView.addSubview(nameLabel)
        self.contentView.addSubview(genderIcon)
        self.contentView.addSubview(subjectLabel)
        self.contentView.addSubview(priceLabel)
        
        // Autolayout
        contentView.snp_makeConstraints(closure: { (make) -> Void in
            make.left.equalTo(self.snp_left)
            make.right.equalTo(self.snp_right)
            make.bottom.equalTo(self.snp_bottom)
            make.height.equalTo(MalaLayout_DetailHeaderContentHeight)
        })
        avatarBackground.snp_makeConstraints { (make) in
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-7)
            make.width.equalTo(MalaLayout_AvatarSize)
            make.height.equalTo(MalaLayout_AvatarSize)
        }
        avatarView.snp_makeConstraints(closure: { (make) -> Void in
            make.center.equalTo(self.avatarBackground.snp_center)
            make.size.equalTo(self.avatarBackground.snp_size).offset(-5)
        })
        vipIconView.snp_makeConstraints(closure: { (make) -> Void in
            make.right.equalTo(self.avatarView.snp_right).offset(-3)
            make.bottom.equalTo(self.avatarView.snp_bottom).offset(-3)
            make.width.equalTo(MalaLayout_VipIconSize)
            make.height.equalTo(MalaLayout_VipIconSize)
        })
        nameLabel.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(12)
            make.left.equalTo(self.avatarView.snp_right).offset(12)
            make.height.equalTo(16)
        })
        genderIcon.snp_makeConstraints(closure: { (make) -> Void in
            make.centerY.equalTo(self.nameLabel.snp_centerY)
            make.left.equalTo(self.nameLabel.snp_right).offset(12)
            make.width.equalTo(13)
            make.height.equalTo(13)
        })
        subjectLabel.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.nameLabel.snp_bottom).offset(8)
            make.left.equalTo(self.nameLabel.snp_left)
            make.width.equalTo(24)
            make.height.equalTo(12)
        })
        priceLabel.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.nameLabel.snp_bottom).offset(8)
            make.left.equalTo(self.subjectLabel.snp_right).offset(12)
            make.right.equalTo(self.contentView.snp_right).offset(-12)
            make.height.equalTo(12)
        })
    }
}