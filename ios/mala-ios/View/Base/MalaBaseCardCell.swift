//
//  MalaBaseCardCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/19.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class MalaBaseCardCell: UICollectionViewCell {
    
    // MARK: - Components
    /// 布局视图（卡片）
    lazy var layoutView: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.whiteColor()
        return view
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
        contentView.backgroundColor = MalaColor_F2F2F2_0
        
        // SubViews
        contentView.addSubview(layoutView)
        
        
        // Autolayout
        layoutView.snp_makeConstraints { (make) in
            make.top.equalTo(self.contentView.snp_top).offset(6)
            make.left.equalTo(self.contentView.snp_left).offset(6)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-6)
            make.right.equalTo(self.contentView.snp_right).offset(-6)
        }
    }
}