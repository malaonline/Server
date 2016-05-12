//
//  CouponRulesPopupWindow.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/12.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit


public class CouponRulesPopupWindow: UIViewController, UITextViewDelegate {
    
    // MARK: - Property
    /// 自身强引用
    var strongSelf: CouponRulesPopupWindow?
    /// 遮罩层透明度
    let tBakcgroundTansperancy: CGFloat = 0.7
    /// 布局容器（窗口）
    var window = UIView()
    /// 内容视图
    var contentView: UIView?
    /// 单击背景close窗口
    var closeWhenTap: Bool = false
    
    
    // MARK: - Components
    /// 描述 文字背景
    private lazy var textBackground: UIImageView = {
        let textBackground = UIImageView(image: UIImage(named: "aboutText_Background"))
        return textBackground
    }()
    /// 描述标题
    private lazy var titleView: AboutTitleView = {
        let titleView = AboutTitleView()
        titleView.title = "奖学金使用规则"
        return titleView
    }()
    /// 关于描述label
    private lazy var aboutTextView: UILabel = {
        let aboutTextView = UILabel()
        aboutTextView.numberOfLines = 0
        aboutTextView.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        aboutTextView.textColor = MalaColor_939393_0
        aboutTextView.text = MalaConfig.couponRulesDescriptionString()
        aboutTextView.backgroundColor = MalaColor_F2F2F2_0
        aboutTextView
        return aboutTextView
    }()
    /// 提交按钮装饰线
    private lazy var buttonSeparatorLine: UIView = {
        let buttonSeparatorLine = UIView()
        buttonSeparatorLine.backgroundColor = MalaColor_8FBCDD_0
        return buttonSeparatorLine
    }()
    /// 提交按钮
    private lazy var confirmButton: UIButton = {
        let button = UIButton()
        button.setTitle("知道了", forState: .Normal)
        button.setTitleColor(MalaColor_8FBCDD_0, forState: .Normal)
        button.setTitleColor(MalaColor_B7B7B7_0, forState: .Disabled)
        button.setBackgroundImage(UIImage.withColor(MalaColor_FFFFFF_9), forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(MalaColor_F8F8F8_0), forState: .Highlighted)
        button.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_15)
        button.addTarget(self, action: #selector(CouponRulesPopupWindow.animateDismiss), forControlEvents: .TouchUpInside)
        return button
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
        // 设置属性
        self.contentView = contentView
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
    
    
    // MARK: - Override
    public override func touchesBegan(touches: Set<UITouch>, withEvent event: UIEvent?) {
        if closeWhenTap {
            // 若触摸点不位于Window视图，关闭弹窗
            if let point = touches.first?.locationInView(window) where !window.pointInside(point, withEvent: nil) {
                closeAlert(0)
            }
        }
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
        window.backgroundColor = UIColor.whiteColor()
        
        // SubViews
        view.addSubview(window)
        window.addSubview(buttonSeparatorLine)
        window.addSubview(confirmButton)
        window.addSubview(textBackground)
        window.addSubview(titleView)
        window.addSubview(aboutTextView)
        
        
        // Autolayout
        window.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.view.snp_top).offset(36)
            make.centerX.equalTo(self.view.snp_centerX)
            make.width.equalTo(MalaLayout_CommentPopupWindowWidth)
            make.height.equalTo(MalaLayout_CouponRulesPopupWindowHeight)
        }
        buttonSeparatorLine.snp_makeConstraints { (make) in
            make.height.equalTo(MalaScreenOnePixel)
            make.left.equalTo(self.window.snp_left)
            make.right.equalTo(self.window.snp_right)
            make.bottom.equalTo(self.confirmButton.snp_bottom)
        }
        confirmButton.snp_makeConstraints { (make) in
            make.bottom.equalTo(self.window.snp_bottom)
            make.left.equalTo(self.window.snp_left)
            make.right.equalTo(self.window.snp_right)
            make.height.equalTo(44)
        }
        textBackground.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.window.snp_top).offset(-MalaLayout_Margin_18)
            make.left.equalTo(self.window.snp_left).offset(MalaLayout_Margin_18)
            make.right.equalTo(self.window.snp_right).offset(-MalaLayout_Margin_18)
            make.bottom.equalTo(self.confirmButton.snp_top).offset(-MalaLayout_Margin_18)
        }
        titleView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(textBackground.snp_top).offset(MalaLayout_Margin_18)
            make.left.equalTo(textBackground.snp_left)
            make.right.equalTo(textBackground.snp_right)
        }
        aboutTextView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(titleView.snp_bottom).offset(MalaLayout_Margin_18)
            make.left.equalTo(textBackground.snp_left).offset(MalaLayout_Margin_18)
            make.right.equalTo(textBackground.snp_right).offset(-MalaLayout_Margin_18)
            make.bottom.equalTo(textBackground.snp_bottom).offset(-MalaLayout_Margin_18)
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
    
    @objc private func animateDismiss() {
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
    
    
    // MARK: - Event Response
    @objc private func pressed(sender: UIButton!) {
        self.closeAlert(sender.tag)
    }
    
    @objc private func closeButtonDidTap() {
        close()
    }
}