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
                    make.width.equalTo(50)
                    make.height.equalTo(title.snp_height)
                    make.right.equalTo(self.contentView.snp_right).offset(-12)
                })
                
            case .DropArrow:
                contentView.addSubview(dropArrow)
                dropArrow.snp_makeConstraints(closure: { (make) -> Void in
                    make.centerY.equalTo(self.title.snp_centerY)
                    make.width.equalTo(50)
                    make.height.equalTo(title.snp_height)
                    make.right.equalTo(self.contentView.snp_right).offset(-12)
                })
                
            case .SubTitle:
                contentView.addSubview(subTitleLabel)
                subTitleLabel.snp_makeConstraints(closure: { (make) -> Void in
                    make.top.equalTo(self.contentView.snp_top).offset(16)
                    make.left.equalTo(self.title.snp_right).offset(10)
                    make.height.equalTo(self.title.snp_height)
                    make.right.equalTo(self.contentView.snp_right).offset(-12)
                })
            }
        }
    }
    /// 标签字符串数组
    var labels: [String] = [] {
        didSet {
            if labels != oldValue {
                for view in self.tagsView.subviews {
                    view.removeFromSuperview()
                }
                self.tagsView.setTags(labels)
            }
        }
    }
    
    
    // MARK: - Components
    /// 标题label
    lazy var title: UILabel = {
        let label = UILabel(title: MalaCommonString_Title)
        label.textColor = MalaColor_333333_0
        label.font = UIFont.systemFontOfSize(15)
        return label
    }()
    /// 真正的控件容器，若有需求要添加新的子控件，请添加于此内部（注意区别于 UITableViewCell 中的 contentView）
    lazy var content: UIView = UIView()
    /// 标签容器
    lazy var tagsView: MATabListView = {
        let tagsView = MATabListView(frame: CGRect(x: 0, y: 0, width: UIScreen.mainScreen().bounds.size.width, height: 12))
        return tagsView
    }()
    /// 详情箭头指示器——附加组件类型之一
    lazy var rightArrow: UIButton = {
        let rightArrow = UIButton()
        rightArrow.setImage(UIImage(named: "rightArrow"), forState: .Normal)
        rightArrow.imageEdgeInsets = UIEdgeInsets(top: 0, left: 19, bottom: 0, right: -19)
        rightArrow.addTarget(self, action: #selector(MalaBaseCell.accessoryViewDidTap(_:)), forControlEvents: .TouchUpInside)
        return rightArrow
    }()
    /// 详情箭头指示器——附加组件类型之一
    lazy var dropArrow: UIButton = {
        let dropArrow = UIButton()
        dropArrow.setImage(UIImage(named: "dropArrow"), forState: .Normal)
        dropArrow.setImage(UIImage(named: "upArrow"), forState: .Selected)
        dropArrow.imageEdgeInsets = UIEdgeInsets(top: 0, left: 19, bottom: 0, right: -19)
        dropArrow.addTarget(self, action: #selector(MalaBaseCell.accessoryViewDidTap(_:)), forControlEvents: .TouchUpInside)
        return dropArrow
    }()
    /// 副标题label——附加组件类型之一
    lazy var subTitleLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(10)
        label.textColor = MalaColor_939393_0
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
        // Style 
        
        // SubViews
        contentView.addSubview(title)
        contentView.addSubview(content)
        content.addSubview(tagsView)
        
        // Autolayout
        title.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(16)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.height.equalTo(15)
            make.width.equalTo(100)
            make.bottom.equalTo(content.snp_top).offset(-14)
        }
        content.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.title.snp_bottom).offset(14)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.right.equalTo(self.contentView.snp_right).offset(-12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-14)
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