//
//  MalaBaseCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

/// 自定义附加组件
enum TeacherDetailsCellAccessoryType : Int {
    
    case None       // don't show any thing
    case RightArrow // info button
    case DropArrow  // detail button
    case SubTitle   // label view, you need to set titleString first for use this
}

class MalaBaseCell: UITableViewCell {
    
    // MARK: - Property
    /// 副标题文字
    var subTitle: String? {
        didSet {
            subTitleLabel.text = subTitle
        }
    }
    /// 自定义附加组件类型
    var accessory: TeacherDetailsCellAccessoryType {
        didSet {
            switch accessory {
            case .None:
                break
                
            case .RightArrow:
                contentView.addSubview(rightArrow)
                rightArrow.snp_makeConstraints(closure: { (make) -> Void in
                    make.centerY.equalTo(self.title.snp_centerY)
                    make.width.equalTo(7)
                    make.height.equalTo(self.title.snp_height)
                    make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
                })
                
            case .DropArrow:
                contentView.addSubview(dropArrow)
                dropArrow.snp_makeConstraints(closure: { (make) -> Void in
                    make.centerY.equalTo(self.title.snp_centerY)
                    make.width.equalTo(13)
                    make.height.equalTo(7)
                    make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
                })
                
            case .SubTitle:
                contentView.addSubview(subTitleLabel)
                subTitleLabel.snp_makeConstraints(closure: { (make) -> Void in
                    make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_16)
                    make.left.equalTo(self.title.snp_right).offset(MalaLayout_Margin_10)
                    make.height.equalTo(self.title.snp_height)
                    make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
                })
            }
        }
    }
    /// 标签字符串数组
    var labels: [String]? {
        didSet {
            for view in self.tagsView.subviews {
                view.removeFromSuperview()
            }
            self.tagsView.setTags(labels)
        }
    }
    
    
    // MARK: - Components
    /// 标题label
    lazy var title: UILabel = {
        let label = UILabel(title: MalaCommonString_Title)
        label.textColor = MalaDetailsCellTitleColor
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_15)
        return label
    }()
    /// 真正的控件容器，若有需求要添加新的子控件，请添加于此内部（注意区别于 UITableViewCell 中的 contentView）
    lazy var content: UIView = UIView()
    /// 标签容器
    private lazy var tagsView: MATabListView = {
        let tagsView = MATabListView(frame: CGRect(x: 0, y: 0, width: UIScreen.mainScreen().bounds.size.width, height: MalaLayout_FontSize_12))
        return tagsView
    }()
    /// 详情箭头指示器——附加组件类型之一
    private lazy var rightArrow: UIButton = {
        let rightArrow = UIButton()
        rightArrow.setImage(UIImage(named: "rightArrow"), forState: .Normal)
        rightArrow.addTarget(self, action: "accessoryViewDidTap:", forControlEvents: .TouchUpInside)
        return rightArrow
    }()
    /// 详情箭头指示器——附加组件类型之一
    private lazy var dropArrow: UIButton = {
        let dropArrow = UIButton()
        dropArrow.setImage(UIImage(named: "dropArrow"), forState: .Normal)
        dropArrow.addTarget(self, action: "accessoryViewDidTap:", forControlEvents: .TouchUpInside)
        return dropArrow
    }()
    /// 副标题label——附加组件类型之一
    private lazy var subTitleLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_10)
        label.textColor = MalaDetailsCellSubTitleColor
        label.textAlignment = .Right
        return label
    }()
    
    
    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        self.accessory = .None
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private
    private func setupUserInterface() {
        // SubViews
        contentView.addSubview(title)
        contentView.addSubview(content)
        content.addSubview(tagsView)
        
        // Autolayout
        title.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_16)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_15)
            make.width.equalTo(100)
        }
        content.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.title.snp_bottom).offset(MalaLayout_Margin_14)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_16)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
        }
        tagsView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top)
            make.bottom.equalTo(self.content.snp_bottom)
            make.left.equalTo(self.content.snp_left)
            make.right.equalTo(self.content.snp_right)
        }
    }
    
    @objc func accessoryViewDidTap(sender: UIButton) {
    
    }
}
