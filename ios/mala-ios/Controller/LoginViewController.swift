//
//  LoginViewController.swift
//  mala-ios
//
//  Created by Erdi on 12/31/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class LoginViewController: UIViewController {
    
    // MARK: - Components
    /// 主要布局容器
    private lazy var contentView: UIView = {
        let contentView = UIView()
        contentView.backgroundColor = UIColor.whiteColor()
        return contentView
    }()
    /// 容器顶部装饰线
    private lazy var topSeparator: UIView = {
        let topSeparator = UIView()
        topSeparator.backgroundColor = MalaDetailsButtonBorderColor
        return topSeparator
    }()
    /// 容器中部装饰线
    private lazy var middleSeparator: UIView = {
        let middleSeparator = UIView()
        middleSeparator.backgroundColor = MalaDetailsButtonBorderColor
        return middleSeparator
    }()
    /// 容器底部装饰线
    private lazy var bottomSeparator: UIView = {
        let bottomSeparator = UIView()
        bottomSeparator.backgroundColor = MalaDetailsButtonBorderColor
        return bottomSeparator
    }()
    /// 手机图标
    private lazy var phoneIcon: UIImageView = {
        let phoneIcon = UIImageView(image: UIImage(named: "phone"))
        return phoneIcon
    }()
    /// [获取验证码] 按钮
    private lazy var codeGetButton: UIButton = {
        let codeGetButton = UIButton()
        codeGetButton.layer.borderColor = MalaLoginCodeGetButtonColor.CGColor
        codeGetButton.layer.borderWidth = 1.0
        codeGetButton.layer.cornerRadius = 3.0
        codeGetButton.layer.masksToBounds = true
        codeGetButton.setTitle(" 获取验证码 ", forState: .Normal)
        codeGetButton.setTitleColor(UIColor.whiteColor(), forState: .Normal)
        codeGetButton.setTitleColor(MalaLoginCodeGetButtonColor, forState: .Disabled)
        codeGetButton.setBackgroundImage(UIImage.withColor(MalaLoginCodeGetButtonColor), forState: .Normal)
        codeGetButton.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Disabled)
        codeGetButton.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        codeGetButton.addTarget(self, action: "codeGetButtonDidTap", forControlEvents: .TouchUpInside)
        return codeGetButton
    }()
    /// [手机号错误] 提示
    private lazy var phoneError: UIButton = {
        let phoneError = UIButton()
        phoneError.setImage(UIImage(named: "error"), forState: .Normal)
        phoneError.setTitleColor(MalaDetailsPriceRedColor, forState: .Normal)
        phoneError.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        phoneError.setTitle("手机号错误", forState: .Normal)
        phoneError.imageEdgeInsets = UIEdgeInsets(top: 0, left: -2, bottom: 0, right: 2)
        phoneError.hidden = true
        return phoneError
    }()
    /// 手机号码输入框
    private lazy var phoneTextField: UITextField = {
        let phoneTextField = UITextField()
        phoneTextField.keyboardType = .NumberPad
        phoneTextField.placeholder = "请输入手机号"
        phoneTextField.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        phoneTextField.textColor = MalaAppearanceTextColor
        phoneTextField.addTarget(self, action: "textDidChange:", forControlEvents: .EditingChanged)
        phoneTextField.clearButtonMode = .Never
        return phoneTextField
    }()
    /// 验证码图标
    private lazy var codeIcon: UIImageView = {
        let codeIcon = UIImageView(image: UIImage(named: "verifyCode"))
        return codeIcon
    }()
    /// [验证码错误] 提示
    private lazy var codeError: UIButton = {
        let codeError = UIButton()
        codeError.setImage(UIImage(named: "error"), forState: .Normal)
        codeError.setTitleColor(MalaDetailsPriceRedColor, forState: .Normal)
        codeError.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        codeError.setTitle("验证码错误", forState: .Normal)
        codeError.imageEdgeInsets = UIEdgeInsets(top: 0, left: -2, bottom: 0, right: 2)
        codeError.hidden = true
        return codeError
    }()
    /// 验证码输入框
    private lazy var codeTextField: UITextField = {
        let codeTextField = UITextField()
        codeTextField.keyboardType = .NumberPad
        codeTextField.textColor = MalaAppearanceTextColor
        codeTextField.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        codeTextField.addTarget(self, action: "textDidChange:", forControlEvents: .EditingChanged)
        return codeTextField
    }()
    /// [验证] 按钮
    private lazy var verifyButton: UIButton = {
        let verifyButton = UIButton()
        verifyButton.layer.cornerRadius = 5
        verifyButton.layer.masksToBounds = true
        verifyButton.setTitle("验证", forState: .Normal)
        verifyButton.setTitleColor(UIColor.whiteColor(), forState: .Normal)
        verifyButton.setBackgroundImage(UIImage.withColor(MalaLoginVerifyButtonDisableColor), forState: .Disabled)
        verifyButton.setBackgroundImage(UIImage.withColor(MalaLoginVerifyButtonNormalColor), forState: .Normal)
        verifyButton.addTarget(self, action: "verifyButtonDidTap", forControlEvents: .TouchUpInside)
        return verifyButton
    }()
    // 协议文字
    private lazy var protocolLabel: UILabel = {
        let protocolLabel = UILabel()
        protocolLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        return protocolLabel
    }()
    
    private var callMeInSeconds = MalaConfig.callMeInSeconds()
    

    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUserInterface()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        self.title = "验证"
        self.navigationController!.navigationBar.shadowImage = UIImage()
        self.view.backgroundColor = MalaTeacherCellBackgroundColor
        let leftBarButtonItem = UIBarButtonItem(customView:UIButton(imageName: "close", target: self, action: "closeButtonDidClick"))
        navigationItem.leftBarButtonItem = leftBarButtonItem
        
        // SubView
        view.addSubview(contentView)
        contentView.addSubview(topSeparator)
        contentView.addSubview(middleSeparator)
        contentView.addSubview(bottomSeparator)
        contentView.addSubview(phoneIcon)
        contentView.addSubview(codeGetButton)
        contentView.addSubview(phoneError)
        contentView.addSubview(phoneTextField)
        contentView.addSubview(codeIcon)
        contentView.addSubview(codeError)
        contentView.addSubview(codeTextField)
        view.addSubview(verifyButton)
        view.addSubview(protocolLabel)
        
        // Autolayout
        contentView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.view.snp_top).offset(MalaLayout_Margin_12)
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
            make.height.equalTo(93)
        }
        topSeparator.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top)
            make.height.equalTo(MalaScreenOnePixel)
            make.left.equalTo(self.contentView.snp_left)
            make.right.equalTo(self.contentView.snp_right)
        }
        middleSeparator.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(self.contentView.snp_centerY)
            make.height.equalTo(MalaScreenOnePixel)
            make.left.equalTo(self.contentView.snp_left)
            make.right.equalTo(self.contentView.snp_right)
        }
        bottomSeparator.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.contentView.snp_bottom)
            make.height.equalTo(MalaScreenOnePixel)
            make.left.equalTo(self.contentView.snp_left)
            make.right.equalTo(self.contentView.snp_right)
        }
        phoneIcon.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_15)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_14)
            make.width.equalTo(10)
            make.height.equalTo(15)
        }
        codeGetButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_9)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_FontSize_12)
            make.width.equalTo(67)
            make.height.equalTo(27)
        }
        phoneError.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(self.codeGetButton)
            make.right.equalTo(self.codeGetButton.snp_left).offset(-MalaLayout_Margin_4)
            make.width.equalTo(67)
            make.height.equalTo(15)
        }
        phoneTextField.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.phoneIcon.snp_right).offset(MalaLayout_Margin_10)
            make.right.equalTo(self.phoneError.snp_left).offset(-MalaLayout_Margin_5)
            make.centerY.equalTo(self.phoneIcon.snp_centerY)
            make.height.equalTo(25)
        }
        codeIcon.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_15)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_14)
        }
        codeError.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_9)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_FontSize_12)
            make.width.equalTo(67)
            make.height.equalTo(27)
        }
        codeTextField.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(self.codeIcon.snp_right).offset(MalaLayout_Margin_7)
            make.right.equalTo(self.codeError.snp_left).offset(-MalaLayout_Margin_5)
            make.centerY.equalTo(self.codeIcon.snp_centerY)
            make.height.equalTo(25)
        }
        verifyButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_bottom).offset(MalaLayout_Margin_12)
            make.left.equalTo(self.view.snp_left).offset(MalaLayout_Margin_12)
            make.right.equalTo(self.view.snp_right).offset(-MalaLayout_Margin_12)
            make.height.equalTo(37)
        }
        protocolLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.verifyButton.snp_bottom).offset(MalaLayout_Margin_12)
            make.left.equalTo(self.view.snp_left).offset(MalaLayout_Margin_12)
            make.right.equalTo(self.view.snp_right).offset(MalaLayout_Margin_12)
        }
    }
    
    private func validateMobile(mobile: String) -> Bool {
        let mobileRegex = "^((13[0-9])|(15[^4,\\D])|(18[0,0-9]))\\d{8}$"
        let mobileTest = NSPredicate(format: "SELF MATCHES %@", mobileRegex)
        return mobileTest.evaluateWithObject(mobile)
    }
    
    ///  倒计时
    private func countDown() {
        let queue = dispatch_get_global_queue(DISPATCH_QUEUE_PRIORITY_DEFAULT, 0)
        let timer = dispatch_source_create(DISPATCH_SOURCE_TYPE_TIMER, 0, 0, queue)
        dispatch_source_set_timer(timer, dispatch_walltime(nil, 0), UInt64(NSTimeInterval(NSEC_PER_SEC)), 0)
        dispatch_source_set_event_handler(timer) {[weak self] () -> Void in
            
            if self?.callMeInSeconds <= 0 { // 倒计时完成
                dispatch_source_cancel(timer)
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    self?.codeGetButton.setTitle(" 获取验证码 ", forState: .Normal)
                    self?.codeGetButton.enabled = true
                })
            }else { // 继续倒计时
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    self?.codeGetButton.setTitle(String(format: " %02ds后获取 ", Int((self?.callMeInSeconds)!)), forState: .Normal)
                    self?.codeGetButton.enabled = false
                })
                self?.callMeInSeconds--
            }
        }
        dispatch_resume(timer)
    }
    
    
    // MARK: - Event Response
    @objc private func textDidChange(textField: UITextField) {
        // 若当前有错误信息出现，用户开始编辑时移除错误显示
        if !phoneError.hidden {
            phoneError.hidden = true
        }else if !codeError.hidden {
            codeError.hidden = true
        }
    }
    
    @objc private func codeGetButtonDidTap() {        
        // 验证手机号
        if !validateMobile(phoneTextField.text ?? "") {
            self.phoneError.hidden = false
            self.phoneTextField.text = ""
            self.phoneTextField.becomeFirstResponder()
            return
        }
                
        countDown()
        ThemeHUD.showActivityIndicator()
        
        // 发送SMS
        sendVerifyCodeOfMobile(self.phoneTextField.text!, failureHandler: { reason, errorMessage in
            
            ThemeHUD.hideActivityIndicator()
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("LoginViewController - SendCode Error \(errorMessage)")
            }
        }, completion: { bool in
            ThemeHUD.hideActivityIndicator()
            println("send Verifycode -  \(bool)")
        })
    }

    @objc private func verifyButtonDidTap() {
        // 验证信息
        if !validateMobile(phoneTextField.text ?? "") {
            self.phoneError.hidden = false
            self.phoneTextField.text = ""
            self.phoneTextField.becomeFirstResponder()
            return
        }
        if (codeTextField.text ?? "") == "" {
            self.codeError.hidden = false
            self.codeTextField.text = ""
            self.codeTextField.becomeFirstResponder()
            return
        }
        
        ThemeHUD.showActivityIndicator()
        
        // 验证SMS
        verifyMobile(self.phoneTextField.text!, verifyCode: self.codeTextField.text!, failureHandler: { [weak self] (reason, errorMessage) -> Void in
            
            ThemeHUD.hideActivityIndicator()
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("LoginViewController - VerifyCode Error \(errorMessage)")
            }
            
            dispatch_async(dispatch_get_main_queue()) {
                self?.codeError.hidden = false
                self?.codeTextField.text = ""
            }
        }, completion: { [weak self] (loginUser) -> Void in
            ThemeHUD.hideActivityIndicator()
            println("SMS验证成功，用户Token：\(loginUser.accessToken)")
            saveTokenAndUserInfo(loginUser)
            self?.closeButtonDidClick()
        })
    }
    
    @objc private func closeButtonDidClick() {
        dismissViewControllerAnimated(true, completion: nil)
    }
}