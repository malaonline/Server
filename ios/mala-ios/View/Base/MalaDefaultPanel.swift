//
//  MalaDefaultPanel.swift
//  mala-ios
//
//  Created by 王新宇 on 3/28/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class MalaDefaultPanel: UIView {

    // MARK: - Property
    /// 图片名称
    var imageName: String = "" {
        didSet {
            imageView.image = UIImage(named: imageName)
            imageView.sizeToFit()
        }
    }
    /// 描述文字
    var text: String = "" {
        didSet {
            label.text = text
            label.sizeToFit()
        }
    }
    
    
    // MARK: - Components
    /// 图片视图
    private lazy var imageView: UIImageView = {
        let imageView = UIImageView()
        return imageView
    }()
    /// 文字label
    private lazy var label: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(13)
        label.textColor = MalaColor_939393_0
        label.textAlignment = .Center
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
    
    
    // MARK: - Private
    private func setupUserInterface() {
        // Style
        
        
        // SubViews
        addSubview(imageView)
        addSubview(label)
        
        // Autolayout
        label.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(self.snp_centerY).offset(-50)
            make.centerX.equalTo(self.snp_centerX)
            make.height.equalTo(13)
        }
        imageView.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(self.snp_centerX)
            make.bottom.equalTo(label.snp_top).offset(-8)
        }
    }
}