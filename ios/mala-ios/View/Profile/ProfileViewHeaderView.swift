//
//  ProfileViewHeaderView.swift
//  mala-ios
//
//  Created by 王新宇 on 3/10/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class ProfileViewHeaderView: UIView {

    // MARK: - Property
    /// 学生姓名
    var name: String = "学生姓名" {
        didSet {
            nameLabel.text = name
        }
    }
    /// 用户头像URL
    var avatarURL: String = "" {
        didSet {
            avatarView.kf_setImageWithURL(NSURL(string: avatarURL) ?? NSURL(), placeholderImage: UIImage(named: "avatar_placeholder"))
        }
    }
    
    
    // MARK: - Components
    /// 头像ImageView控件
    private lazy var avatarView: UIImageView = {
        let imageView = UIImageView()
        imageView.backgroundColor = UIColor.whiteColor()
        imageView.image = UIImage(named: "avatar_placeholder")
        imageView.layer.cornerRadius = MalaLayout_AvatarSize*0.5
        imageView.layer.masksToBounds = true
        imageView.layer.borderWidth = 2.5
        imageView.layer.borderColor = UIColor.whiteColor().CGColor
        return imageView
    }()
    /// 姓名label控件
    private lazy var nameLabel: UILabel = {
        let nameLabel = UILabel()
        nameLabel.textColor = UIColor.whiteColor()
        nameLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        nameLabel.textAlignment = .Center
        return nameLabel
    }()
    /// 底部分隔视图
    private lazy var sectionView: UIView = {
        let sectionView = UIView()
        sectionView.backgroundColor = MalaProfileBackgroundColor
        return sectionView
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupUserInterface()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    // MARK: - Private
    private func setupUserInterface() {
        // Style
        backgroundColor = UIColor.clearColor()
        
        // SubViews
        addSubview(avatarView)
        addSubview(nameLabel)
        addSubview(sectionView)
        
        // Autolayout
        avatarView.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.snp_top).offset(MalaLayout_Margin_16)
            make.centerX.equalTo(self.snp_centerX)
            make.width.equalTo(MalaLayout_AvatarSize)
            make.height.equalTo(MalaLayout_AvatarSize)
        })
        nameLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(avatarView.snp_bottom).offset(MalaLayout_Margin_5)
            make.centerX.equalTo(avatarView.snp_centerX)
            make.width.equalTo(100)
            make.height.equalTo(MalaLayout_FontSize_14)
        }
        sectionView.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.snp_bottom)
            make.left.equalTo(self.snp_left)
            make.right.equalTo(self.snp_right)
            make.height.equalTo(8)
        }
    }
}