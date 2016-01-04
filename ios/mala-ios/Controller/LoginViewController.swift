//
//  LoginViewController.swift
//  mala-ios
//
//  Created by Erdi on 12/31/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController {
    
    // MARK: - Variables

    // MARK: - Components
    private lazy var dismissButton: UIButton = {
        let button = UIButton()
        button.setTitle(MalaCommonString_Cancel, forState: .Normal)
        button.setTitleColor(UIColor.blackColor(), forState: .Normal)
        button.addTarget(self, action: "dismiss", forControlEvents: .TouchUpInside)
        return button
    }()
    private lazy var numberLabel: UILabel = UILabel(title: MalaCommonString_PhoneNumber)
    private lazy var numberTextField: UITextField = {
        let textfield = UITextField()
        textfield.keyboardType = .NumberPad
        return textfield
    }()
    private lazy var numberSeparator: UIView = UIView.separator()
    private lazy var checkLabel: UILabel = UILabel(title: MalaCommonString_VerifyCode)
    private lazy var checkTextField: UITextField = {
        let textfield = UITextField()
        textfield.keyboardType = .NumberPad
        return textfield
    }()
    private lazy var verifyCodeGetButton: UIButton = {
        let button = UIButton()
        button.setTitle("获取验证码", forState: .Normal)
        button.titleLabel?.font = UIFont.systemFontOfSize(16)
        button.setTitleColor(UIColor.grayColor(), forState: .Normal)
        button.addTarget(self, action: "verifyCodeGetButtonDidTap", forControlEvents: .TouchUpInside)
        return button
    }()
    private lazy var checkSeparator: UIView = UIView.separator()
    private lazy var verifyButton: UIButton = {
        let button = UIButton(title: "验证", titleColor: UIColor.whiteColor(), selectedTitleColor: UIColor.lightGrayColor(), bgColor: UIColor.redColor(), selectedBgColor: UIColor.whiteColor())
        button.addTarget(self, action: "verifyButtonDidTap", forControlEvents: .TouchUpInside)
        return button
    }()
    

    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUI()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    // MARK: - Private Method
    private func setupUI() {
        
        // setup style
        self.view.backgroundColor = UIColor.whiteColor()
        
        // setup SubView
        view.addSubview(dismissButton)
        view.addSubview(numberLabel)
        view.addSubview(numberTextField)
        view.addSubview(numberSeparator)
        view.addSubview(checkLabel)
        view.addSubview(checkTextField)
        view.addSubview(verifyCodeGetButton)
        view.addSubview(checkSeparator)
        view.addSubview(verifyButton)

        // setup Autolayout
        let margin: CGFloat = 10.0
        
        dismissButton.snp_makeConstraints { (make) -> Void in
            //make.height.equalTo(30)
            //make.width.equalTo(100)
            make.left.equalTo(self.view.snp_left)//.offset(20)
            make.top.equalTo(self.view.snp_top)//.offset(20)
        }
        
        numberLabel.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(self.dismissButton.snp_width)
            make.width.equalTo(self.dismissButton.snp_height)
            make.left.equalTo(self.view.snp_left).offset(margin)
            make.centerY.equalTo(self.view.snp_centerY).offset(-200)
        }
        
        numberTextField.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(35)
            make.left.equalTo(self.numberLabel.snp_right).offset(margin)
            make.right.equalTo(self.view.snp_right).offset(-margin)
            make.centerY.equalTo(self.numberLabel.snp_centerY)
        }
        
        numberSeparator.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.view.snp_left).offset(margin)
            make.right.equalTo(self.view.snp_right).offset(-margin)
            make.top.equalTo(self.numberTextField.snp_bottom).offset(5)
            make.height.equalTo(1/UIScreen.mainScreen().scale)
        }
        
        checkLabel.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(self.numberLabel.snp_width)
            make.width.equalTo(self.numberLabel.snp_height)
            make.centerX.equalTo(self.numberLabel.snp_centerX)
            make.top.equalTo(self.numberLabel.snp_bottom).offset(margin)
        }
        
        checkTextField.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.checkLabel.snp_right).offset(margin)
            make.right.equalTo(self.verifyCodeGetButton.snp_left).offset(-margin)
            make.centerY.equalTo(self.checkLabel.snp_centerY)
        }
        
        verifyCodeGetButton.snp_makeConstraints { (make) -> Void in
            make.width.equalTo(100)
            make.right.equalTo(self.view.snp_right).offset(-margin)
            make.centerY.equalTo(self.checkTextField.snp_centerY)
        }
    
        checkSeparator.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.view.snp_left).offset(margin)
            make.right.equalTo(self.view.snp_right).offset(-margin)
            make.top.equalTo(self.checkTextField.snp_bottom).offset(5)
            make.height.equalTo(1/UIScreen.mainScreen().scale)
        }
        
        verifyButton.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.view.snp_left).offset(margin)
            make.right.equalTo(self.view.snp_right).offset(-margin)
            make.top.equalTo(self.checkSeparator.snp_bottom).offset(15)
            make.height.equalTo(50)
        }

    }
    
    @objc private func verifyCodeGetButtonDidTap() {
        print("verifyCodeGetButton DidTao")
    }
    
    @objc private func verifyButtonDidTap() {
        print("verifyButton DidTap")
    }
    
    @objc private func dismiss() {
        dismissViewControllerAnimated(true, completion: nil)
    }

}