//
//  ThemeShareBoard.swift
//  mala-ios
//
//  Created by 王新宇 on 16/8/22.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class ThemeShareBoard: UIView {

    // MARK: - Components
    /// 父布局容器（白色卡片）
    private lazy var content: UIView = {
        let view = UIView()
        return view
    }()
    /// 标题标签
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "分享到",
            fontSize: 15,
            textColor: MalaColor_333333_0
        )
        return label
    }()
    /// 会员服务视图
    private lazy var collectionView: ThemeShareCollectionView = {
        let view = ThemeShareCollectionView(frame: CGRectZero, collectionViewLayout: ThemeShareFlowLayout(frame: CGRectZero))
        return view
    }()
    // 背景视图
    private lazy var backgroundView: UIView = {
        let backgroundView = UIView()
        backgroundView.backgroundColor = UIColor.blackColor()
        backgroundView.alpha = 0.4
        return backgroundView
    }()
    
    
    // MARK: - Instance Method
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
        content.backgroundColor = UIColor.whiteColor() // UIColor.lightGrayColor()
        
        // SubViews
        addSubview(backgroundView)
        addSubview(content)
        content.addSubview(titleLabel)
        content.addSubview(collectionView)
        
        // Autolayout
        backgroundView.snp_makeConstraints { (make) in
            make.center.equalTo(self)
            make.size.equalTo(self)
        }
        content.snp_makeConstraints { (make) in
            make.left.equalTo(self.snp_left)
            make.right.equalTo(self.snp_right)
            make.height.equalTo(126)
            make.bottom.equalTo(self.snp_bottom)
        }
        titleLabel.snp_makeConstraints { (make) in
            make.centerX.equalTo(content.snp_centerX)
            make.top.equalTo(content.snp_top).offset(10)
            make.height.equalTo(15)
        }
        collectionView.snp_makeConstraints { (make) in
            make.top.equalTo(titleLabel.snp_bottom).offset(10)
            make.left.equalTo(content.snp_left).offset(12)
            make.right.equalTo(content.snp_right).offset(-12)
            make.bottom.equalTo(content.snp_bottom)
        }
    }
}