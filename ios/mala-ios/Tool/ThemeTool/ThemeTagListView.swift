//
//  ThemeTagListView.swift
//  mala-ios
//
//  Created by 王新宇 on 16/7/12.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class ThemeTagListView: UIView {

    // MARK: - Property
    /// 图标图片名
    var imageName: String = "" {
        didSet {
            iconView.image = UIImage(named: imageName)
        }
    }
    /// 标签字符串数组
    var labels: [String] = [] {
        didSet {
            if labels != oldValue {
                setupLabels()
            }
        }
    }
    
    
    // MARK: - Components
    /// 图标
    private lazy var iconView: UIImageView = {
        let imageView = UIImageView()
        return imageView
    }()
    /// 标签容器
    lazy var tagsView: TagListView = {
        let tagsView = TagListView()
        return tagsView
    }()
    
    
    // MARK: - Instance Method
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private 
    private func setupUserInterface() { 
        // SubViews
        addSubview(iconView)
        addSubview(tagsView)
        
        // AutoLayout
        iconView.snp_makeConstraints { (make) in
            make.left.equalTo(self.snp_left)
            make.centerY.equalTo(self.snp_centerY)
            make.height.equalTo(21)
            make.width.equalTo(21)
        }
        tagsView.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(iconView.snp_right).offset(12)
            make.centerY.equalTo(self.snp_centerY)
            make.height.equalTo(25)
            make.right.equalTo(self.snp_right)
        }
    }
    
    private func setupLabels() {
        tagsView.reset()
        for string in labels {
            tagsView.addTag(string, backgroundColor: MalaColor_BCD0DE_0, textColor: MalaColor_5789AC_0)
        }
    }
}