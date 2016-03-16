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
}

// MARK: - TeacherDetailsSignupView
class TeacherDetailsSignupView: UIView {
    
    // MARK: - Property
    weak var delegate: SignupButtonDelegate?
    
    
    // MARK: - Components
    private lazy var topLine: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.blackColor()
        view.alpha = 0.25
        return view
    }()
    private lazy var button: UIButton = {
        let button = UIButton(
            title: "马上报名",
            titleColor: MalaColor_82B4D9_0,
            backgroundColor: UIColor(rgbHexValue: 0xFFFFFF, alpha: 0.95)
        )
        button.layer.cornerRadius = 5.0
        button.layer.masksToBounds = true
        button.layer.borderColor = MalaColor_E5E5E5_0.CGColor
        button.layer.borderWidth = 1.0
        button.addTarget(self, action: "buttonDidTap", forControlEvents: .TouchUpInside)
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
        self.addSubview(button)
        
        // Autolayout
        topLine.snp_makeConstraints(closure: { (make) -> Void in
            make.left.equalTo(0)
            make.width.equalTo(MalaScreenWidth)
            make.top.equalTo(self.snp_top)
            make.height.equalTo(MalaScreenOnePixel)
        })
        button.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(self.snp_top).offset(MalaLayout_Margin_6)
            make.left.equalTo(self.snp_left).offset(MalaLayout_Margin_12)
            make.bottom.equalTo(self.snp_bottom).offset(-MalaLayout_Margin_6)
            make.right.equalTo(self.snp_right).offset(-MalaLayout_Margin_12)
        })
    }
    
    
    // MARK: - Event Response
    @objc private func buttonDidTap() {
        delegate?.signupButtonDidTap(self.button)
    }
}
