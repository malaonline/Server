//
//  TeacherDetailsSignupView.swift
//  mala-ios
//
//  Created by Elors on 1/8/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

// MARK: - SignupButtonDelegate
protocol SignupButtonDelegate: class, NSObjectProtocol {
    func signupButtonDidTap(sender: UIButton)
    func likeButtonDidTap(sender: UIButton)
}

// MARK: - TeacherDetailsSignupView
class TeacherDetailsSignupView: UIView {
    
    // MARK: - Property
    weak var delegate: SignupButtonDelegate?
    
    
    // MARK: - Components
    /// 装饰线
    private lazy var topLine: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.blackColor()
        view.alpha = 0.25
        return view
    }()
    /// 收藏按钮
    private lazy var likeButton: UIButton = {
        let button = UIButton()
        button.titleLabel?.font = UIFont.systemFontOfSize(10)
        button.setImage(UIImage(named: "like_icon"), forState: .Normal)
        button.setTitle("收藏", forState: .Normal)
        button.setTitleColor(MalaColor_6C6C6C_0, forState: .Normal)
        button.imageEdgeInsets = UIEdgeInsets(top: -8, left: 10.35, bottom: 8, right: -10.35)
        button.titleEdgeInsets = UIEdgeInsets(top: 8, left: -10.25, bottom: -8, right: 10.25)
        button.addTarget(self, action: #selector(TeacherDetailsSignupView.likeButtonDidTap), forControlEvents: .TouchUpInside)
        return button
    }()
    /// 报名按钮
    private lazy var button: UIButton = {
        let button = UIButton(
            title: "马上报名",
            titleColor: MalaColor_FFFFFF_9,
            backgroundColor: MalaColor_7FB4DC_0
        )
        // button.setBackgroundImage(UIImage.withColor(MalaColor_E5E5E5_3), forState: .Highlighted)
        button.addTarget(self, action: #selector(TeacherDetailsSignupView.signupButtonDidTap), forControlEvents: .TouchUpInside)
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
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        self.backgroundColor = MalaColor_F6F6F6_96

        // SubViews
        self.addSubview(topLine)
         self.addSubview(likeButton)
        self.addSubview(button)
        
        // Autolayout
        topLine.snp_makeConstraints(closure: { (make) -> Void in
            make.left.equalTo(0)
            make.width.equalTo(MalaScreenWidth)
            make.top.equalTo(self.snp_top)
            make.height.equalTo(MalaScreenOnePixel)
        })
         likeButton.snp_makeConstraints(closure: { (make) -> Void in
             make.top.equalTo(topLine.snp_bottom)
             make.left.equalTo(self.snp_left)
             make.bottom.equalTo(self.snp_bottom)
             make.right.equalTo(self.snp_right).multipliedBy(0.422)
         })
        button.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(topLine.snp_bottom)
            make.left.equalTo(likeButton.snp_right)
            make.bottom.equalTo(self.snp_bottom)
            make.right.equalTo(self.snp_right)
        })
    }
    
    
    // MARK: - Event Response
    @objc private func signupButtonDidTap() {
        delegate?.signupButtonDidTap(self.button)
    }

    @objc private func likeButtonDidTap() {
        delegate?.likeButtonDidTap(self.button)
    }
    
    deinit {
        println("TeacherDetailsSignupView Deinit")
    }
}