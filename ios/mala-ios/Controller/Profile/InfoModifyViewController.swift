//
//  InfoModifyViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 3/11/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class InfoModifyViewController: BaseViewController {
    
    // MARK: - Property
    /// 修改信息类型
    var infoType: userInfoType?
    /// 文本框缺省值
    var defaultString: String? {
        didSet {
            inputField.text = defaultString
        }
    }
    
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
        inputField.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        inputField.textColor = MalaColor_636363_0
        inputField.tintColor = MalaColor_82B4D9_0
        inputField.addTarget(self, action: #selector(InfoModifyViewController.inputFieldDidChange), forControlEvents: .EditingChanged)
        return inputField
    }()
    /// 保存按钮
    private lazy var saveButton: UIButton = {
        let saveButton = UIButton(
            title: "保存",
            titleColor: MalaColor_82B4D9_0,
            target: self,
            action: #selector(InfoModifyViewController.saveChange)
        )
        saveButton.setTitleColor(MalaColor_E0E0E0_95, forState: .Disabled)
        return saveButton
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        setupUserInterface()
        configure()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        inputField.becomeFirstResponder()
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        self.view.backgroundColor = MalaColor_F2F2F2_0
        
        // SubViews
        view.addSubview(inputBackground)
        view.addSubview(inputField)
        
        // Autolayout
        inputBackground.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(view.snp_top)
            make.left.equalTo(view.snp_left)
            make.right.equalTo(view.snp_right)
            make.height.equalTo(MalaLayout_ProfileModifyViewHeight)
        }
        inputField.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(inputBackground.snp_left)
            make.right.equalTo(inputBackground.snp_right)
            make.centerY.equalTo(inputBackground.snp_centerY)
        }
    }
    
    
    private func configure() {
        // rightBarButtonItem
        let spacerRight = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
        spacerRight.width = -MalaLayout_Margin_5
        let rightBarButtonItem = UIBarButtonItem(customView: saveButton)
        navigationItem.rightBarButtonItems = [rightBarButtonItem, spacerRight]
    }

    ///  保存学生姓名
    private func saveStudentsName() {
        let name = inputField.text ?? ""
        
        ThemeHUD.showActivityIndicator()
        
        saveStudentName(name, failureHandler: { (reason, errorMessage) -> Void in
            ThemeHUD.hideActivityIndicator()
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("InfoModifyViewController - saveStudentName Error \(errorMessage)")
            }
        }, completion: { [weak self] (bool) -> Void in
            println("学生姓名保存 - \(bool)")
            MalaUserDefaults.studentName.value = name
            getInfoWhenLoginSuccess()
            ThemeHUD.hideActivityIndicator()
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                self?.popSelf()
            })
        })
        
    }
    
    ///  保存学生学校名称
    private func saveStudentsSchool() {
        let name = inputField.text ?? ""
        
        ThemeHUD.showActivityIndicator()
        
        saveStudentSchoolName(name, failureHandler: { (reason, errorMessage) -> Void in
            ThemeHUD.hideActivityIndicator()
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("InfoModifyViewController - saveStudentSchool Error \(errorMessage)")
            }
        }, completion: { [weak self] (bool) -> Void in
            println("学校名称保存 - \(bool)")
            MalaUserDefaults.schoolName.value = name
            getInfoWhenLoginSuccess()
            ThemeHUD.hideActivityIndicator()
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                self?.popSelf()
            })
        })
    }
    
    // MARK: - Event Response
    private func validateName(name: String) -> Bool {
        let maxLength = (infoType == .StudentSchoolName ? 10 : 4)
        let nameRegex = "^[\\u4e00-\\u9fa5]{2,\(maxLength)}$"
        let nameTest = NSPredicate(format: "SELF MATCHES %@", nameRegex)
        return nameTest.evaluateWithObject(name)
    }
    
    
    // MARK: - Event Response
    @objc private func inputFieldDidChange() {
        guard let name = inputField.text else {
            return
        }
        saveButton.enabled = validateName(name)
    }
    
    @objc private func saveChange() {
        guard let type = infoType else {
            return
        }
        
        ///  根据信息类型进行保存
        switch type {
        case .StudentName:
            saveStudentsName()
            break
        case .StudentSchoolName:
            saveStudentsSchool()
            break
        }
    }
}