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
    /// 按钮描述文字
    var buttonTitle: String = "" {
        didSet {
            button.setTitle(buttonTitle, forState: .Normal)
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
    /// 按钮
    private lazy var button: UIButton = {
        let button = UIButton()
        button.layer.cornerRadius = 18
        button.layer.masksToBounds = true
        button.layer.borderColor = MalaColor_8DBEDE_0.CGColor
        button.layer.borderWidth = 1
        button.setTitleColor(MalaColor_8DBEDE_0, forState: .Normal)
        return button
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
    
    ///  按钮点击事件
    func addTarget(target: AnyObject?, action: Selector) {
        
        addSubview(button)
        button.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(self.snp_centerX)
            make.top.equalTo(label.snp_bottom).offset(25)
            make.height.equalTo(36)
            make.width.equalTo(164)
        }
        
        button.addTarget(target, action: action, forControlEvents: .TouchUpInside)
    }
}