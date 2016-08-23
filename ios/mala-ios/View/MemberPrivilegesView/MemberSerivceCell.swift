//
//  MemberSerivceCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/17.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class MemberSerivceCell: UITableViewCell {

    // MARK: - Components
    /// 父布局容器（白色卡片）
    private lazy var content: UIView = {
        let view = UIView()
        return view
    }()
    /// 标题标签
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "会员专享",
            fontSize: 15,
            textColor: MalaColor_333333_0
        )
        return label
    }()
    /// 会员服务视图
    private lazy var collectionView: MemberSerivceCollectionView = {
        let view = MemberSerivceCollectionView(frame: CGRectZero, collectionViewLayout: MemberSerivceFlowLayout(frame: CGRectZero))
        return view
    }()
    
    
    // MARK: - Instance Method
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
        content.backgroundColor = UIColor.whiteColor()
        
        // SubViews
        contentView.addSubview(content)
        content.addSubview(titleLabel)
        content.addSubview(collectionView)
        
        // Autolayout
        content.snp_makeConstraints { (make) in
            make.top.equalTo(self.contentView.snp_top).offset(8)
            make.left.equalTo(self.contentView.snp_left)
            make.right.equalTo(self.contentView.snp_right)
            make.height.equalTo(229)
            make.bottom.equalTo(self.contentView.snp_bottom)
        }
        titleLabel.snp_makeConstraints { (make) in
            make.top.equalTo(content.snp_top).offset(16)
            make.left.equalTo(content.snp_top).offset(12)
            make.height.equalTo(15)
        }
        collectionView.snp_makeConstraints { (make) in
            make.top.equalTo(titleLabel.snp_bottom).offset(16)
            make.bottom.equalTo(content.snp_bottom)
            make.left.equalTo(content.snp_left).offset(12)
            make.right.equalTo(content.snp_right).offset(-12)
        }
    }
}