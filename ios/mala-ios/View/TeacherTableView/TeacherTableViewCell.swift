//
//  TeacherTableViewCell.swift
//  mala-ios
//
//  Created by Elors on 1/14/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherTableViewCell: UITableViewCell {

    // MARK: - Property
    /// 老师简介模型
    var model: TeacherModel? {
        didSet{
            
            guard let model = model else {
                return
            }
            
            courseLabel.setTitle((model.grades_shortname ?? "")+" • "+(model.subject ?? ""), forState: .Normal)
            nameLabel.text = model.name
            levelLabel.text = String(format: "  T%d  ", model.level)
            avatarView.kf_setImageWithURL(
                (model.avatar ?? NSURL()),
                placeholderImage: UIImage(named: "avatar_placeholder"),
                optionsInfo: [.Transition(.Fade(0.25))]
            )
            
            let string = String(MinPrice: model.min_price.money, MaxPrice: model.max_price.money)
            let attrString: NSMutableAttributedString = NSMutableAttributedString(string: string)
            let rangeLocation = (string as NSString).rangeOfString("元").location
            attrString.addAttribute(
                NSForegroundColorAttributeName,
                value: MalaColor_82B4D9_0,
                range: NSMakeRange(0, rangeLocation)
            )
            attrString.addAttribute(
                NSFontAttributeName,
                value: UIFont.systemFontOfSize(14),
                range: NSMakeRange(0, rangeLocation)
            )
            attrString.addAttribute(
                NSForegroundColorAttributeName,
                value: MalaColor_6C6C6C_0,
                range: NSMakeRange(rangeLocation, 4)
            )
            attrString.addAttribute(
                NSFontAttributeName,
                value: UIFont.systemFontOfSize(12),
                range: NSMakeRange(rangeLocation, 4)
            )
            priceLabel.attributedText = attrString
            
            tagsLabel.text = model.tags?.joinWithSeparator("｜")
        }
    }
    
    
    // MARK: - Components
    /// 布局视图（卡片式Cell白色背景）
    private lazy var content: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.whiteColor()
        return view
    }()
    /// 授课年级及科目label
    private lazy var courseLabel: UIButton = {
        let courseLabel = UIButton()
        courseLabel.setBackgroundImage(UIImage(named: "tagsTitle"), forState: .Normal)
        courseLabel.titleLabel?.font = UIFont.systemFontOfSize(11)
        courseLabel.titleEdgeInsets = UIEdgeInsets(top: -1, left: 0, bottom: 1, right: 0)
        courseLabel.userInteractionEnabled = false
        return courseLabel
    }()
    /// 老师姓名label
    private lazy var nameLabel: UILabel = {
        let nameLabel = UILabel()
        nameLabel.font = UIFont(name: "HelveticaNeue-Thin", size: 17)
        nameLabel.textColor = MalaColor_4A4A4A_0
        return nameLabel
    }()
    /// 老师级别label
    private lazy var levelLabel: UILabel = {
        let levelLabel = UILabel()
        levelLabel.font = UIFont(name: "HelveticaNeue-Thin", size: 13)
        levelLabel.backgroundColor = UIColor.whiteColor()
        levelLabel.textColor = MalaColor_E26254_0
        return levelLabel
    }()
    /// 级别所在分割线
    private lazy var separator: UIView = {
        let separator = UIView()
        separator.backgroundColor = MalaColor_DADADA_0
        return separator
    }()
    /// 老师头像ImageView
    private lazy var avatarView: UIImageView = {
        let avatarView = UIImageView()
        avatarView.frame = CGRect(x: 0, y: 0, width: MalaLayout_AvatarSize, height: MalaLayout_AvatarSize)
        avatarView.layer.cornerRadius = MalaLayout_AvatarSize * 0.5
        avatarView.layer.masksToBounds = true
        avatarView.image = UIImage(named: "avatar_placeholder")
        avatarView.contentMode = .ScaleAspectFill
        return avatarView
    }()
    /// 授课价格label
    private lazy var priceLabel: UILabel = {
        let priceLabel = UILabel()
        priceLabel.font = UIFont.systemFontOfSize(14)
        priceLabel.textColor = MalaColor_6C6C6C_0
        return priceLabel
    }()
    /// 风格标签label
    private lazy var tagsLabel: UILabel = {
        let tagsLabel = UILabel()
        tagsLabel.font = UIFont(name: "HelveticaNeue-Thin", size: 11)
        tagsLabel.textColor = MalaColor_333333_6
        return tagsLabel
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
        contentView.addSubview(courseLabel)
        content.addSubview(nameLabel)
        content.addSubview(levelLabel)
        content.insertSubview(separator, belowSubview: levelLabel)
        content.addSubview(avatarView)
        content.addSubview(priceLabel)
        content.addSubview(tagsLabel)
        
        // Autolayout
        content.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(4)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-4)
            make.right.equalTo(self.contentView.snp_right).offset(-12)
        }
        courseLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(4)
            make.left.equalTo(self.contentView.snp_left)
            make.height.equalTo(24)
            make.width.equalTo(100)
        }
        nameLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top).offset(15)
            make.centerX.equalTo(self.content.snp_centerX)
            make.height.equalTo(17)
        }
        levelLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.nameLabel.snp_bottom).offset(10)
            make.centerX.equalTo(self.content.snp_centerX)
            make.height.equalTo(13)
        }
        separator.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(self.content.snp_centerX)
            make.centerY.equalTo(self.levelLabel.snp_centerY)
            make.left.equalTo(self.content.snp_left).offset(10)
            make.right.equalTo(self.content.snp_right).offset(-10)
            make.height.equalTo(MalaScreenOnePixel)
        }
        avatarView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.levelLabel.snp_bottom).offset(12)
            make.centerX.equalTo(self.content.snp_centerX)
            make.width.equalTo(MalaLayout_AvatarSize)
            make.height.equalTo(MalaLayout_AvatarSize)
        }
        priceLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.avatarView.snp_bottom).offset(11)
            make.centerX.equalTo(self.content.snp_centerX)
            make.height.equalTo(14)
        }
        tagsLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.priceLabel.snp_bottom).offset(12)
            make.centerX.equalTo(self.content.snp_centerX)
            make.height.equalTo(11)
            make.bottom.equalTo(self.content.snp_bottom).offset(-15)
        }
    }
}
