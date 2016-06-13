//
//  InfoModifyViewWindow.swift
//  mala-ios
//
//  Created by 王新宇 on 16/6/12.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

public class InfoModifyViewWindow: UIViewController, UITextViewDelegate {
    
    // MARK: - Property
    /// 姓名文字
    var nameString: String? = MalaUserDefaults.studentName.value
    /// 自身强引用
    var strongSelf: InfoModifyViewWindow?
    /// 遮罩层透明度
    let tBakcgroundTansperancy: CGFloat = 0.7
    /// 布局容器（窗口）
    var window = UIView()
    /// 内容视图
    var contentView: UIView?
    /// 单击背景close窗口
    var closeWhenTap: Bool = false
    
    
    // MARK: - Components
    /// 取消按钮.[取消]
    private lazy var cancelButton: UIButton = {
        let button = UIButton()
        button.setTitle("取消", forState: .Normal)
        // cancelButton.setTitleColor(MalaColor_8FBCDD_0, forState: .Normal)
        button.setTitleColor(MalaColor_B7B7B7_0, forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(MalaColor_FFFFFF_9), forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(MalaColor_F8F8F8_0), forState: .Highlighted)
        button.titleLabel?.font = UIFont.systemFontOfSize(15)
        button.addTarget(self, action: #selector(InfoModifyViewWindow.cancelButtonDidTap), forControlEvents: .TouchUpInside)
        return button
    }()
    /// 确认按钮.[去评价]
    private lazy var saveButton: UIButton = {
        let button = UIButton()
        button.setTitle("保存", forState: .Normal)
        button.setTitleColor(MalaColor_8FBCDD_0, forState: .Normal)
        button.setTitleColor(MalaColor_B7B7B7_0, forState: .Highlighted)
        button.setBackgroundImage(UIImage.withColor(MalaColor_FFFFFF_9), forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(MalaColor_F8F8F8_0), forState: .Highlighted)
        button.titleLabel?.font = UIFont.systemFontOfSize(15)
        button.addTarget(self, action: #selector(InfoModifyViewWindow.saveButtonDidTap), forControlEvents: .TouchUpInside)
        return button
    }()
    private lazy var contentContainer: UIView = {
        let contentContainer = UIView()
        return contentContainer
    }()
    /// 按钮顶部装饰线
    private lazy var buttonTopLine: UIView = {
        let buttonTopLine = UIView()
        buttonTopLine.backgroundColor = MalaColor_8FBCDD_0
        return buttonTopLine
    }()
    /// 按钮间隔装饰线
    private lazy var buttonSeparatorLine: UIView = {
        let buttonSeparatorLine = UIView()
        buttonSeparatorLine.backgroundColor = MalaColor_8FBCDD_0
        return buttonSeparatorLine
    }()
    /// 姓名文本框
    private lazy var nameLabel: UITextField = {
        let textField = UITextField()
        textField.textAlignment = .Center
        textField.textColor = MalaColor_636363_0
        textField.tintColor = MalaColor_82B4D9_0
        textField.text = self.nameString
        textField.font = UIFont.systemFontOfSize(14)
        textField.addTarget(self, action: #selector(InfoModifyViewWindow.inputFieldDidChange), forControlEvents: .EditingChanged)
        return textField
    }()
    /// 姓名底部装饰线
    private lazy var nameLine: UIView = {
        let view = UIView.separator(MalaColor_82B4D9_0)
        return view
    }()
    /// 提示文字标签
    private lazy var warningLabel: UILabel = {
        let label = UILabel(
            text: "* 请输入2-4位中文字符",
            fontSize: 11,
            textColor: MalaColor_E26254_0
        )
        return label
    }()
    
    
    // MARK: - Constructed
    init() {
        super.init(nibName: nil, bundle: nil)
        view.frame = UIScreen.mainScreen().bounds
        setupUserInterface()
        
        // 持有自己强引用，使自己在外界没有强引用时依然存在。
        strongSelf = self
    }
    
    convenience init(contentView: UIView) {
        self.init()
        self.view.alpha = 0
        
        // 显示Window
        let window: UIWindow = UIApplication.sharedApplication().keyWindow!
        window.addSubview(view)
        window.bringSubviewToFront(view)
        view.frame = window.bounds
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Life Cycle
    override public func viewDidLoad() {
        super.viewDidLoad()
    }
    
    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
    // MARK: - API
    public func show() {
        animateAlert()
    }
    
    public func close() {
        closeAlert(0)
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        view.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: tBakcgroundTansperancy)
        view.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(CoursePopupWindow.backgroundDidTap)))
        window.backgroundColor = UIColor.whiteColor()
        
        // SubViews
        view.addSubview(window)
        window.addSubview(contentContainer)
        window.addSubview(nameLine)
        window.addSubview(nameLabel)
        window.addSubview(warningLabel)
        window.addSubview(cancelButton)
        window.addSubview(saveButton)
        window.addSubview(buttonTopLine)
        window.addSubview(buttonSeparatorLine)
        
        // Autolayout
        window.snp_makeConstraints { (make) -> Void in
            make.center.equalTo(self.view.snp_center)
            make.width.equalTo(MalaLayout_CoursePopupWindowWidth)
            make.height.equalTo(MalaLayout_CoursePopupWindowWidth*0.588)
        }
        contentContainer.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.window.snp_top)
            make.left.equalTo(self.window.snp_left)
            make.right.equalTo(self.window.snp_right)
            make.bottom.equalTo(self.buttonTopLine.snp_top)
        }
        nameLine.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(2)
            make.centerX.equalTo(contentContainer.snp_centerX)
            make.centerY.equalTo(contentContainer.snp_bottom).multipliedBy(0.65)
            make.width.equalTo(window.snp_width).multipliedBy(0.8)
        }
        nameLabel.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(15)
            make.centerX.equalTo(contentContainer.snp_centerX)
            make.bottom.equalTo(nameLine.snp_top).offset(-15)
            make.width.equalTo(100)
        }
        warningLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(nameLine.snp_bottom).offset(10)
            make.right.equalTo(nameLine.snp_right)
        }
        cancelButton.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.window.snp_bottom)
            make.left.equalTo(self.window.snp_left)
            make.height.equalTo(44)
            make.width.equalTo(self.window.snp_width).multipliedBy(0.5)
        }
        saveButton.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.window.snp_bottom)
            make.right.equalTo(self.window.snp_right)
            make.height.equalTo(44)
            make.width.equalTo(self.window.snp_width).multipliedBy(0.5)
        }
        buttonTopLine.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(cancelButton.snp_top)
            make.height.equalTo(MalaScreenOnePixel)
            make.left.equalTo(self.window.snp_left)
            make.right.equalTo(self.window.snp_right)
        }
        buttonSeparatorLine.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.cancelButton.snp_top)
            make.bottom.equalTo(self.window.snp_bottom)
            make.width.equalTo(MalaScreenOnePixel)
            make.left.equalTo(cancelButton.snp_right)
        }
    }
    
    private func animateAlert() {
        view.alpha = 0;
        let originTransform = self.window.transform
        self.window.layer.transform = CATransform3DMakeScale(0.7, 0.7, 0.0);
        
        UIView.animateWithDuration(0.35) { () -> Void in
            self.view.alpha = 1.0
            self.window.transform = originTransform
        }
    }
    
    private func animateDismiss() {
        UIView.animateWithDuration(0.35, animations: { () -> Void in
            
            self.view.alpha = 0
            self.window.transform = CGAffineTransform()
            
            }, completion: { [weak self] (bool) -> Void in
                self?.closeAlert(0)
            })
    }
    
    private func closeAlert(buttonIndex: Int) {
        self.view.removeFromSuperview()
        // 释放自身强引用
        self.strongSelf = nil
    }
    
    ///  保存学生姓名
    private func saveStudentsName() {
        let name = nameLabel.text ?? ""
        
        ThemeHUD.showActivityIndicator()
        
        saveStudentName(name, failureHandler: { (reason, errorMessage) -> Void in
            ThemeHUD.hideActivityIndicator()
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("InfoModifyViewWindow - saveStudentName Error \(errorMessage)")
            }
        }, completion: { [weak self] (bool) -> Void in
            println("学生姓名保存 - \(bool)")
            
            MalaUserDefaults.studentName.value = name
            NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_RefreshStudentName, object: nil)
            
            ThemeHUD.hideActivityIndicator()
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                self?.animateDismiss()
            })
        })
    }
    
    
    // MARK: - Event Response
    @objc private func pressed(sender: UIButton!) {
        self.closeAlert(sender.tag)
    }
    
    @objc public  func backgroundDidTap() {
        if closeWhenTap {
            closeAlert(0)
        }
    }
    
    @objc private func cancelButtonDidTap() {
        close()
    }
    
    ///  验证姓名字符是否合规
    private func validateName(name: String) -> Bool {
        let nameRegex = "^[\\u4e00-\\u9fa5]{2,4}$"
        let nameTest = NSPredicate(format: "SELF MATCHES %@", nameRegex)
        return nameTest.evaluateWithObject(name)
    }
    
    ///  用户输入事件
    @objc private func inputFieldDidChange() {
        guard let name = nameLabel.text else {
            return
        }
        
        if nameLabel.text?.characters.count > 4 {
            nameLabel.text = nameLabel.text?.subStringToIndex(4)
            return
        }
        
        saveButton.enabled = validateName(name)
    }
    
    ///  保存按钮点击事件
    @objc private func saveButtonDidTap() {
        saveStudentsName()
    }
}