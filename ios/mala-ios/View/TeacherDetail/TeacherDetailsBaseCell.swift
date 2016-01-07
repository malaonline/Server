//
//  TeacherDetailsBaseCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailsBaseCell: UITableViewCell {
    
    // MARK: - Components
    /// Label for title
    lazy var title: UILabel = {
        let label = UILabel(title: MalaCommonString_Title)
        label.textColor = MalaDetailsCellTitleColor
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_15)
        return label
    }()
    
    /// String for subTitle
    var subTitle: String?
    
    /// Custom accessoryView at the right corner
    var accessory: TeacherDetailsCellAccessoryType {
        didSet {
            switch accessory {
            case .None:
                break
                
            case .RightArrow:
                contentView.addSubview(rightArrow)
                rightArrow.snp_makeConstraints(closure: { (make) -> Void in
                    make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_10)
                    make.width.equalTo(7)
                    make.height.equalTo(self.title.snp_height)
                    make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
                })
                
            case .SubTitle:
                contentView.addSubview(subTitleLabel)
                subTitleLabel.snp_makeConstraints(closure: { (make) -> Void in
                    make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_10)
                    make.left.equalTo(self.title.snp_right).offset(MalaLayout_Margin_10)
                    make.height.equalTo(self.title.snp_height)
                    make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
                })
                
            default:
                break
            }
        }
    }
    
    /// The truely container
    lazy var content: UIView = UIView()
    
    /// View use to put tags
    private lazy var tagsView: MATabListView = {
        let tagsView = MATabListView(frame: CGRect(x: 0, y: 0,
            width: UIScreen.mainScreen().bounds.size.width, height: MalaLayout_FontSize_12))
        self.content.addSubview(tagsView)
        
        tagsView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top)
            make.bottom.equalTo(self.content.snp_bottom)
            make.left.equalTo(self.content.snp_left)
            make.right.equalTo(self.content.snp_right)
        }
        return tagsView
    }()
    
    /// Strings (like tags and certificates)
    var labels: [String]? {
        didSet {
            for view in self.tagsView.subviews {
                view.removeFromSuperview()
            }
            self.tagsView.setTags(labels)
        }
    }
    
    /// Style for display AccessoryType
    enum TeacherDetailsCellAccessoryType : Int {
        
        case None       // don't show any thing
        case RightArrow // info button
        case DropArrow  // detail button
        case SubTitle   // label view, you need to set titleString first for use this
    }
    
    private lazy var rightArrow: UIImageView = {
        let rightArrowView = UIImageView(image: UIImage(named: "rightArrow"))
        return rightArrowView
    }()
    
    /// Label use to display subTitle in custom accessoryView
    private lazy var subTitleLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_10)
        label.textColor = MalaDetailsCellSubTitleColor
        label.textAlignment = .Left
        return label
    }()
    
    
    // MARK: - Life Cycle
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
        
        // Autolayout
        title.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_10)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_15)
            make.width.equalTo(100)
        }
        content.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.title.snp_bottom).offset(MalaLayout_Margin_12)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_15)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
        }
 
    }
    
}
