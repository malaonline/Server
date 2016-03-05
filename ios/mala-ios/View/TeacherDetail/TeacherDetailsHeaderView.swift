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
                debugPrint("TeacherDetailsHeaderView - AvatarURL Format Error")
                return
            }
            self.avatarView.kf_setImageWithURL(url, placeholderImage: UIImage.withColor(UIColor.lightGrayColor()))
        }
    }
    /// 教师姓名
    var name: String = "----" {
        didSet{
            self.nameLabel.text = name
        }
    }
    /// 教师性别
    var gender: String = ""{
        didSet{
            switch gender {
            case "m": genderLabel.text = "男"
            case "f": genderLabel.text = "女"
            default: genderLabel.text = "暂无"
            }
        }
    }
    /// 最小价格
    var minPrice: Int = 0 {
        didSet{
            self.priceLabel.text = String(format: "%d-%d元/小时", minPrice, maxPrice)
        }
    }
    /// 最大价格
    var maxPrice: Int = 0 {
        didSet{
            self.priceLabel.text = String(format: "%d-%d元/小时", minPrice, maxPrice)
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
        let imageView = UIImageView()
        imageView.image = UIImage.withColor(UIColor.lightGrayColor())
        imageView.layer.cornerRadius = MalaLayout_AvatarSize*0.5
        imageView.layer.masksToBounds = true
        imageView.layer.borderWidth = 2.5
        imageView.layer.borderColor = UIColor.whiteColor().CGColor
        return imageView
    }()
    /// 会员图标显示控件
    private lazy var vipIconView: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "vip"))
        imageView.layer.cornerRadius = MalaLayout_VipIconSize*0.5
        imageView.layer.masksToBounds = true
        imageView.layer.borderWidth = 1.0
        imageView.layer.borderColor = UIColor.whiteColor().CGColor
        return imageView
    }()
    /// 老师姓名label
    private lazy var nameLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_16)
        return label
    }()
    /// 老师性别label
    private lazy var genderLabel: UILabel = {
        let label = UILabel()
        label.textColor = MalaDetailsCellSubTitleColor
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        return label
    }()
    /// 价格label
    private lazy var priceLabel: UILabel = {
        let label = UILabel()
        label.textColor = MalaDetailsCellSubTitleColor
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
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
        self.contentView.addSubview(avatarView)
        self.contentView.addSubview(vipIconView)
        self.contentView.addSubview(nameLabel)
        self.contentView.addSubview(genderLabel)
        self.contentView.addSubview(priceLabel)
        
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
        priceLabel.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.nameLabel.snp_bottom).offset(MalaLayout_Margin_8)
            make.left.equalTo(self.genderLabel.snp_right).offset(MalaLayout_Margin_12)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_12)
        })
    }
}
