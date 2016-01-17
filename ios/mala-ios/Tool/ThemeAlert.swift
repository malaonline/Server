//
//  ThemeAlert.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

public class ThemeAlert: UIViewController {

    // MARK: - Property
    /// 自身强引用
    var strongSelf: ThemeAlert?
    /// 遮罩层透明度
    let tBakcgroundTansperancy: CGFloat = 0.7
    /// 布局容器（窗口）
    var window = UIView()
    /// 标题文字
    var tTitle: String = " 默认标题 "
    /// 图标名称
    var tIcon: String = "grade"
    var contentView: UIView?
    
    // MARK: - Components
    private lazy var themeIcon: UIImageView = {
        let themeIcon = UIImageView()
        themeIcon.frame = CGRect(x: 0, y: 0, width: 64, height: 64)
        themeIcon.layer.cornerRadius = 32
        themeIcon.layer.masksToBounds = true
        themeIcon.backgroundColor = UIColor.lightGrayColor()
        themeIcon.image = UIImage(named: self.tIcon)
        return themeIcon
    }()
    private lazy var closeButton: UIButton = {
        let closeButton = UIButton()
        closeButton.setBackgroundImage(UIImage(named: "close_normal"), forState: .Normal)
        closeButton.setBackgroundImage(UIImage(named: "close_press"), forState: .Selected)
        closeButton.addTarget(self, action: "pressed:", forControlEvents: .TouchUpInside)
        return closeButton
    }()
    private lazy var confirmButton: UIButton = {
        let confirmButton = UIButton()
        confirmButton.setBackgroundImage(UIImage(named: "confirm_normal"), forState: .Normal)
        confirmButton.setBackgroundImage(UIImage(named: "confirm_press"), forState: .Selected)
        return confirmButton
    }()
    private lazy var themeTitle: UILabel = {
        let themeTitle = UILabel()
        themeTitle.font = UIFont(name: "PingFangSC", size: MalaLayout_FontSize_15)
        themeTitle.backgroundColor = UIColor.whiteColor()
        themeTitle.textColor = MalaDetailsCellSubTitleColor
        themeTitle.text = self.tTitle
        return themeTitle
    }()
    private lazy var separator: UIView = {
        let separator = UIView()
        separator.backgroundColor = MalaTeacherCellSeparatorColor
        return separator
    }()
    private lazy var contentContainer: UIView = {
        let contentContainer = UIView()
        return contentContainer
    }()
    
    
    
    // MARK: - Constructed
    init() {
        super.init(nibName: nil, bundle: nil)
        view.frame = UIScreen.mainScreen().bounds
        setupUserInterface()

        // 持有自己强引用，使自己在外界没有强引用时依然存在。
        strongSelf = self
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        view.backgroundColor = UIColor(red: 0, green: 0, blue: 0, alpha: tBakcgroundTansperancy)
        window.backgroundColor = UIColor.whiteColor()
        
        // SubViews
        view.addSubview(window)
        window.addSubview(themeIcon)
        window.addSubview(closeButton)
        window.addSubview(confirmButton)
        window.addSubview(themeTitle)
        window.insertSubview(separator, belowSubview: themeTitle)
        window.addSubview(contentContainer)
        
        // Autolayout
        window.snp_makeConstraints { (make) -> Void in
            make.center.equalTo(self.view.snp_center)
            make.width.equalTo(MalaLayout_FilterWindowWidth)
            make.height.equalTo(MalaLayout_FilterWindowHeight)
        }
        themeIcon.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(self.window.snp_centerX)
            make.top.equalTo(self.window.snp_top).offset(-MalaLayout_FontSize_20)
            make.width.equalTo(64)
            make.height.equalTo(64)
        }
        closeButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.window.snp_top).offset(MalaLayout_Margin_7)
            make.left.equalTo(self.window.snp_left).offset(MalaLayout_Margin_5)
        }
        confirmButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.window.snp_top).offset(MalaLayout_Margin_7)
            make.right.equalTo(self.window.snp_right).offset(-MalaLayout_Margin_5)
        }
        themeTitle.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.themeIcon.snp_bottom).offset(MalaLayout_Margin_16)
            make.centerX.equalTo(self.themeIcon.snp_centerX)
            make.height.equalTo(MalaLayout_FontSize_15)
        }
        separator.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(self.themeTitle.snp_centerY)
            make.left.equalTo(self.window.snp_left).offset(MalaLayout_FontSize_26)
            make.right.equalTo(self.window.snp_right).offset(-MalaLayout_FontSize_26)
            make.height.equalTo(MalaScreenOnePixel)
        }
        contentContainer.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.themeTitle.snp_bottom).offset(MalaLayout_Margin_12)
            make.left.equalTo(self.window.snp_left).offset(MalaLayout_FontSize_26)
            make.right.equalTo(self.window.snp_right).offset(-MalaLayout_FontSize_26)
            make.bottom.equalTo(self.window.snp_bottom).offset(-MalaLayout_Margin_12)
        }
    }
    
    private func updateUserInterface() {
        // SubViews
        contentContainer.addSubview(self.contentView!)
        
        // Autolayout
        contentView!.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentContainer.snp_top)
            make.left.equalTo(self.contentContainer.snp_left)
            make.right.equalTo(self.contentContainer.snp_right)
            make.bottom.equalTo(self.contentContainer.snp_bottom)
        }
    }
    
    
    // MARK: - Life Cycle
    override public func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    

    // MARK: - API
    public func show(icon: String, contentView: UIView) {
        
        // 显示Window
        let window: UIWindow = UIApplication.sharedApplication().keyWindow!
        window.addSubview(view)
        window.bringSubviewToFront(view)
        view.frame = window.bounds
        
        // 设置属性
        self.tIcon = icon
        self.contentView = contentView
        
        // 更新UI
        updateUserInterface()
    }
    
    public func animateAlert() {
        view.alpha = 0;
        UIView.animateWithDuration(0.1, animations: { () -> Void in
            self.view.alpha = 1.0;
        })
        
        self.window.layer.transform = CATransform3DMakeScale(0.9, 0.9, 0.0);
        
        UIView.animateWithDuration(0.2, animations: { () -> Void in
            self.window.layer.transform = CATransform3DMakeScale(1.1, 1.1, 0.0);
            }) { (Bool) -> Void in

                UIView.animateWithDuration(0.1, animations: { () -> Void in
                    self.window.layer.transform = CATransform3DMakeScale(0.9, 0.9, 0.0);
                    })
                
                
        }
    }
    
    public func pressed(sender: UIButton!) {
        self.closeAlert(sender.tag)
    }
    
    private func closeAlert(buttonIndex: Int) {
        UIView.animateWithDuration(0.5, delay: 0.0, options: UIViewAnimationOptions.CurveEaseOut, animations: { () -> Void in
            self.view.alpha = 0.0
            }) { (Bool) -> Void in
                self.view.removeFromSuperview()
                
                // 释放自身强引用
                self.strongSelf = nil
        }
    }
    
}
