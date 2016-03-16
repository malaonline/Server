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
            courseLabel.setTitle((model!.grades_shortname ?? "")+" • "+(model!.subject ?? ""), forState: .Normal)
            nameLabel.text = model!.name
            levelLabel.text = "  "+(model!.level ?? "麻辣讲师")+"  "
            avatarView.kf_setImageWithURL((model!.avatar ?? NSURL()), placeholderImage: UIImage(named: "avatar_placeholder"))
            
            let string = String(format: "%@-%@元/小时", model!.min_price.money, model!.max_price.money)
            let attrString: NSMutableAttributedString = NSMutableAttributedString(string: string)
            let rangeLocation = (string as NSString).rangeOfString("元").location
            attrString.addAttribute(
                NSForegroundColorAttributeName,
                value: MalaDetailsButtonBlueColor,
                range: NSMakeRange(0, rangeLocation)
            )
            attrString.addAttribute(
                NSFontAttributeName,
                value: UIFont(name: "HelveticaNeue", size: MalaLayout_FontSize_14)!,
                range: NSMakeRange(0, rangeLocation)
            )
            attrString.addAttribute(
                NSForegroundColorAttributeName,
                value: MalaAppearanceTextColor,
                range: NSMakeRange(rangeLocation, 4)
            )
            attrString.addAttribute(
                NSFontAttributeName,
                value: UIFont(name: "HelveticaNeue", size: MalaLayout_FontSize_12)!,
                range: NSMakeRange(rangeLocation, 4)
            )
            priceLabel.attributedText = attrString
            
            tagsLabel.text = model!.tags?.joinWithSeparator("｜")
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
        courseLabel.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        courseLabel.userInteractionEnabled = false
        return courseLabel
    }()
    /// 老师姓名label
    private lazy var nameLabel: UILabel = {
        let nameLabel = UILabel()
        nameLabel.font = UIFont(name: "HelveticaNeue-Thin", size: MalaLayout_FontSize_17)
        nameLabel.textColor = MalaColor_4A4A4A_0
        return nameLabel
    }()
    /// 老师级别label
    private lazy var levelLabel: UILabel = {
        let levelLabel = UILabel()
        levelLabel.font = UIFont(name: "HelveticaNeue-Thin", size: MalaLayout_FontSize_13)
        levelLabel.backgroundColor = UIColor.whiteColor()
        levelLabel.textColor = MalaTeacherCellLevelColor
        return levelLabel
    }()
    /// 级别所在分割线
    private lazy var separator: UIView = {
        let separator = UIView()
        separator.backgroundColor = MalaTeacherCellSeparatorColor
        return separator
    }()
    /// 老师头像ImageView
    private lazy var avatarView: UIImageView = {
        let avatarView = UIImageView()
        avatarView.frame = CGRect(x: 0, y: 0, width: MalaLayout_AvatarSize, height: MalaLayout_AvatarSize)
        avatarView.layer.cornerRadius = MalaLayout_AvatarSize * 0.5
        avatarView.layer.masksToBounds = true
        avatarView.backgroundColor = UIColor.lightGrayColor()
        avatarView.image = UIImage(named: "avatar_placeholder")
        avatarView.contentMode = .ScaleAspectFill
        return avatarView
    }()
    /// 授课价格label
    private lazy var priceLabel: UILabel = {
        let priceLabel = UILabel()
        priceLabel.font = UIFont(name: "HelveticaNeue", size: MalaLayout_FontSize_12)
        priceLabel.textColor = MalaAppearanceTextColor
        return priceLabel
    }()
    /// 风格标签label
    private lazy var tagsLabel: UILabel = {
        let tagsLabel = UILabel()
        tagsLabel.font = UIFont(name: "HelveticaNeue-Thin", size: MalaLayout_FontSize_11)
        tagsLabel.textColor = MalaDetailsCellSubTitleColor
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
        contentView.backgroundColor = MalaTeacherCellBackgroundColor
        
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
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_4)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_4)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
        }
        courseLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_4)
            make.left.equalTo(self.contentView.snp_left)
            make.height.equalTo(24)
            make.width.equalTo(100)
        }
        nameLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top).offset(MalaLayout_Margin_15)
            make.centerX.equalTo(self.content.snp_centerX)
            make.height.equalTo(MalaLayout_FontSize_17)
        }
        levelLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.nameLabel.snp_bottom).offset(MalaLayout_Margin_10)
            make.centerX.equalTo(self.content.snp_centerX)
            make.height.equalTo(MalaLayout_FontSize_13)
        }
        separator.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(self.content.snp_centerX)
            make.centerY.equalTo(self.levelLabel.snp_centerY)
            make.left.equalTo(self.content.snp_left).offset(MalaLayout_Margin_10)
            make.right.equalTo(self.content.snp_right).offset(-MalaLayout_Margin_10)
            make.height.equalTo(MalaScreenOnePixel)
        }
        avatarView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.levelLabel.snp_bottom).offset(MalaLayout_Margin_12)
            make.centerX.equalTo(self.content.snp_centerX)
            make.width.equalTo(MalaLayout_AvatarSize)
            make.height.equalTo(MalaLayout_AvatarSize)
        }
        priceLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.avatarView.snp_bottom).offset(MalaLayout_Margin_11)
            make.centerX.equalTo(self.content.snp_centerX)
            make.height.equalTo(MalaLayout_FontSize_14)
        }
        tagsLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.priceLabel.snp_bottom).offset(MalaLayout_Margin_12)
            make.centerX.equalTo(self.content.snp_centerX)
            make.height.equalTo(MalaLayout_FontSize_11)
            make.bottom.equalTo(self.content.snp_bottom).offset(-MalaLayout_Margin_15)
        }
    }
}
