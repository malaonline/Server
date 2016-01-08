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

    // MARK: - Variables
    var avatar: String {
        didSet{
            guard let url = NSURL(string: avatar) else {
                debugPrint("TeacherDetailsHeaderView - AvatarURL Format Error")
                return
            }
            self.avatarView.kf_setImageWithURL(url, placeholderImage: UIImage.withColor(UIColor.lightGrayColor()))
        }
    }
    
    var name: String {
        didSet{
            self.nameLabel.text = name
        }
    }
    
    var gender: String {
        didSet{
            switch gender {
            case "m": genderLabel.text = "男"
            case "fm": genderLabel.text = "女"
            default: genderLabel.text = "暂无"
            }
        }
    }
    
    var teachingAge: Int {
        didSet{
            self.teachingAgeLabel.text = String(format: "教龄%d年", teachingAge)
        }
    }
    
    
    // MARK: - Components
    private lazy var contentView: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.whiteColor()
        return view
    }()
    
    private lazy var avatarView: UIImageView = {
        let imageView = UIImageView()
        imageView.image = UIImage.withColor(UIColor.lightGrayColor())
        imageView.layer.cornerRadius = MalaLayout_AvatarSize*0.5
        imageView.layer.masksToBounds = true
        imageView.layer.borderWidth = 2.5
        imageView.layer.borderColor = UIColor.whiteColor().CGColor
        return imageView
    }()
    
    private lazy var vipIconView: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "vip"))
        imageView.layer.cornerRadius = MalaLayout_VipIconSize*0.5
        imageView.layer.masksToBounds = true
        imageView.layer.borderWidth = 1.0
        imageView.layer.borderColor = UIColor.whiteColor().CGColor
        return imageView
    }()

    private lazy var nameLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_16)
        return label
    }()
    
    private lazy var genderLabel: UILabel = {
        let label = UILabel()
        label.textColor = MalaDetailsCellSubTitleColor
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        return label
    }()
    
    private lazy var teachingAgeLabel: UILabel = {
        let label = UILabel()
        label.textColor = MalaDetailsCellSubTitleColor
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        return label
    }()

    
    // MARK: - Constructed
    override init(frame: CGRect) {
        
        // Set default value
        self.avatar = ""
        self.name = "教师姓名"
        self.gender = "男"
        self.teachingAge = 10
        
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
        self.contentView.addSubview(avatarView)
        self.contentView.addSubview(vipIconView)
        self.contentView.addSubview(nameLabel)
        self.contentView.addSubview(genderLabel)
        self.contentView.addSubview(teachingAgeLabel)
        
        // Autolayout
        contentView.snp_makeConstraints(closure: { (make) -> Void in
            make.left.equalTo(self.snp_left)
            make.right.equalTo(self.snp_right)
            make.bottom.equalTo(self.snp_bottom)
            make.height.equalTo(MalaLayout_DetailHeaderContentHeight)
        })
        avatarView.snp_makeConstraints(closure: { (make) -> Void in
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_7)
            make.width.equalTo(MalaLayout_AvatarSize)
            make.height.equalTo(MalaLayout_AvatarSize)
        })
        vipIconView.snp_makeConstraints(closure: { (make) -> Void in
            make.right.equalTo(self.avatarView.snp_right).offset(-MalaLayout_Margin_3)
            make.bottom.equalTo(self.avatarView.snp_bottom).offset(-MalaLayout_Margin_3)
            make.width.equalTo(MalaLayout_VipIconSize)
            make.height.equalTo(MalaLayout_VipIconSize)
        })
        nameLabel.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_12)
            make.left.equalTo(self.avatarView.snp_right).offset(MalaLayout_Margin_12)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_16)
        })
        genderLabel.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.nameLabel.snp_bottom).offset(MalaLayout_Margin_8)
            make.left.equalTo(self.avatarView.snp_right).offset(MalaLayout_Margin_12)
            make.width.equalTo(MalaLayout_FontSize_12)
            make.height.equalTo(MalaLayout_FontSize_12)
        })
        teachingAgeLabel.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.nameLabel.snp_bottom).offset(MalaLayout_Margin_8)
            make.left.equalTo(self.genderLabel.snp_right).offset(MalaLayout_Margin_12)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_12)
        })
    }
    
}
