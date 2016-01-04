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
        textfield.addTarget(self, action: "textDidChange:", forControlEvents: .EditingChanged)
        textfield.clearButtonMode = .Always
        return textfield
    }()
    private lazy var numberSeparator: UIView = UIView.separator()
    private lazy var checkLabel: UILabel = UILabel(title: MalaCommonString_VerifyCode)
    private lazy var checkTextField: UITextField = {
        let textfield = UITextField()
        textfield.keyboardType = .NumberPad
        textfield.addTarget(self, action: "textDidChange:", forControlEvents: .EditingChanged)
        return textfield
    }()
    private lazy var checkSeparator: UIView = UIView.separator()
    private lazy var verifyCodeGetButton: UIButton = {
        let button = UIButton()
        button.setTitle(MalaCommonString_GetVerifyCode, forState: .Normal)
        button.titleLabel?.font = UIFont.systemFontOfSize(16)
        button.setTitleColor(UIColor.redColor(), forState: .Normal)
        button.setTitleColor(UIColor.grayColor(), forState: .Disabled)
        button.addTarget(self, action: "verifyCodeGetButtonDidTap", forControlEvents: .TouchUpInside)
        button.titleLabel?.textAlignment = .Right
        return button
    }()
    private lazy var verifyButton: UIButton = {
        let button = UIButton(title: "验证", titleColor: UIColor.whiteColor(), selectedTitleColor: UIColor.lightGrayColor(), bgColor: UIColor.redColor(), selectedBgColor: UIColor.whiteColor())
        button.setBackgroundImage(UIImage.withColor(UIColor.lightGrayColor()), forState: .Disabled)
        button.addTarget(self, action: "verifyButtonDidTap", forControlEvents: .TouchUpInside)
        button.enabled = false
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
            make.width.equalTo(140)
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
    
    private func validateMobile(mobile: String) -> Bool {
        let mobileRegex = "^((13[0-9])|(15[^4,\\D])|(18[0,0-9]))\\d{8}$"
        let mobileTest = NSPredicate(format: "SELF MATCHES %@", mobileRegex)
        return mobileTest.evaluateWithObject(mobile)
    }
    
    
    // MARK: - Event Response
    @objc private func textDidChange(textField: UITextField) {
        // when number is validated and verifycode not empty, commit button will show
        self.verifyButton.enabled = validateMobile(self.numberTextField.text ?? "") && (self.checkTextField.text != "")
    }
    
    @objc private func verifyCodeGetButtonDidTap() {
        //TODO: request verify code 
        
        // count down
        var timeout = 60.0 // 60s
        let queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)
        let timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, queue)
        dispatch_source_set_timer(timer, dispatch_walltime(nil, 0), UInt64(NSTimeInterval(NSEC_PER_SEC)), 0)
        dispatch_source_set_event_handler(timer) {[weak self] () -> Void in
                        
            if timeout <= 0 { // count down finished
                dispatch_source_cancel(timer)
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    self?.verifyCodeGetButton.setTitle(MalaCommonString_GetVerifyCode, forState: .Normal)
                    self?.verifyCodeGetButton.enabled = true
                })
            }else { // countinue count down
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    print("\(timeout)")
                    self?.verifyCodeGetButton.setTitle(String(format: "%02d秒后重新发送", Int(timeout)), forState: .Normal)
                    self?.verifyCodeGetButton.enabled = false
                })
                timeout--
            }
        }
        dispatch_resume(timer)
        
    }
    
    @objc private func verifyButtonDidTap() {
        print("phone: \(self.numberTextField.text)")
        print("code : \(self.checkTextField.text)")
    }
    
    @objc private func dismiss() {
        dismissViewControllerAnimated(true, completion: nil)
    }

}