//
//  SaveNameView.swift
//  mala-ios
//
//  Created by 王新宇 on 3/12/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class SaveNameView: UIView, UITextFieldDelegate {

    // MARK: - Property
    var controller: UIViewController?
    
    
    // MARK: - Components
    /// 输入区域背景
    private lazy var inputBackground: UIView = {
        let inputBackground = UIView()
        inputBackground.backgroundColor = UIColor.whiteColor()
        return inputBackground
    }()
    /// 输入控件
    private lazy var inputField: UITextField = {
        let inputField = UITextField()
        inputField.textAlignment = .Center
        inputField.placeholder = "请输入学生姓名"
        inputField.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        inputField.textColor = MalaColor_636363_0
        inputField.tintColor = MalaColor_82B4D9_0
        inputField.addTarget(self, action: #selector(SaveNameView.inputFieldDidChange), forControlEvents: .EditingChanged)
        return inputField
    }()
    /// 描述label
    private lazy var descLabel: UILabel = {
        let descLabel = UILabel()
        descLabel.textColor = MalaColor_939393_0
        descLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        descLabel.text = "请填写真实姓名，不得超过4个汉字"
        return descLabel
    }()
    /// [验证] 按钮
    private lazy var finishButton: UIButton = {
        let finishButton = UIButton()
        finishButton.layer.cornerRadius = 5
        finishButton.layer.masksToBounds = true
        finishButton.setTitle("完成", forState: .Normal)
        finishButton.setTitleColor(UIColor.whiteColor(), forState: .Normal)
        finishButton.setBackgroundImage(UIImage.withColor(MalaColor_8DBEDF_0), forState: .Disabled)
        finishButton.setBackgroundImage(UIImage.withColor(MalaColor_88BCDE_95), forState: .Normal)
        finishButton.addTarget(self, action: #selector(SaveNameView.finishButtonDidTap), forControlEvents: .TouchUpInside)
        finishButton.enabled = false
        return finishButton
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
        backgroundColor = MalaColor_F2F2F2_0
        
        // SubViews
        addSubview(inputBackground)
        addSubview(inputField)
        addSubview(descLabel)
        addSubview(finishButton)
        
        // Autolayout
        inputBackground.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.snp_top).offset(MalaLayout_Margin_12)
            make.left.equalTo(self.snp_left)
            make.right.equalTo(self.snp_right)
            make.height.equalTo(MalaLayout_ProfileModifyViewHeight)
        }
        inputField.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(inputBackground.snp_left)
            make.right.equalTo(inputBackground.snp_right)
            make.centerY.equalTo(inputBackground.snp_centerY)
        }
        descLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(inputBackground.snp_bottom).offset(MalaLayout_Margin_12)
            make.left.equalTo(self.snp_left).offset(MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_12)
        }
        finishButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(descLabel.snp_bottom).offset(MalaLayout_Margin_12)
            make.left.equalTo(self.snp_left).offset(MalaLayout_Margin_12)
            make.right.equalTo(self.snp_right).offset(-MalaLayout_Margin_12)
            make.height.equalTo(37)
        }
    }
    
    private func validateName(name: String) -> Bool {
        let nameRegex = "^[\\u4e00-\\u9fa5]{2,4}$"
        let nameTest = NSPredicate(format: "SELF MATCHES %@", nameRegex)
        return nameTest.evaluateWithObject(name)
    }
    
    
    // MARK: - Event Response
    @objc private func inputFieldDidChange() {
        guard let name = inputField.text else {
            return
        }
        finishButton.enabled = validateName(name)
    }
    
    @objc private func finishButtonDidTap() {
        let name = (inputField.text ?? "")
        
        
        ThemeHUD.showActivityIndicator()
        
        saveStudentName(name, failureHandler: { (reason, errorMessage) -> Void in
            ThemeHUD.hideActivityIndicator()
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("SaveNameView - saveStudentName Error \(errorMessage)")
            }
        }, completion: { [weak self] (bool) -> Void in
                println("学生姓名保存 - \(bool)")
                MalaUserDefaults.studentName.value = name
                getInfoWhenLoginSuccess()
                self?.closeButtonDidClick()
                ThemeHUD.hideActivityIndicator()
        })
    }
    
    @objc private func closeButtonDidClick() {
        controller?.dismissViewControllerAnimated(true, completion: nil)
    }
}