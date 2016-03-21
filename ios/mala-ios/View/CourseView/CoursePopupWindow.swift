//
//  CoursePopupWindow.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

public class CoursePopupWindow: UIViewController {

    // MARK: - Property
    /// 自身强引用
    var strongSelf: CoursePopupWindow?
    /// 遮罩层透明度
    let tBakcgroundTansperancy: CGFloat = 0.7
    /// 布局容器（窗口）
    var window = UIView()
    /// 标题文字
    var titleDate: NSDate = NSDate() {
        didSet {
            self.courseDateLabel.text = titleDate.formattedDateWithFormat("yyyy.MM.dd")
        }
    }
    /// 课程已上标识
    var isPassed: Bool = false {
        didSet {
            if isPassed {
                iconView.text = "已上"
                iconView.backgroundColor = MalaColor_D0D0D0_0
            }else {
                iconView.text = "待上"
                iconView.backgroundColor = MalaColor_A5C9E4_0
            }
        }
    }
    /// 内容视图
    var contentView: UIView?
    /// 单击背景close窗口
    var closeWhenTap: Bool = false
    
    
    // MARK: - Components
    /// 图标
    private lazy var iconView: UILabel = {
        let iconView = UILabel()
        iconView.frame = CGRect(
            x: 0,
            y: 0,
            width: MalaLayout_CoursePopupWindowTitleViewHeight,
            height: MalaLayout_CoursePopupWindowTitleViewHeight
        )
        iconView.layer.cornerRadius = MalaLayout_CoursePopupWindowTitleViewHeight/2
        iconView.layer.masksToBounds = true
        iconView.backgroundColor = MalaColor_A5C9E4_0
        iconView.text = "待上"
        iconView.textAlignment = .Center
        iconView.textColor = MalaColor_FFFFFF_9
        return iconView
    }()
    /// 取消按钮.[知道了][取消]
    private lazy var cancelButton: UIButton = {
        let cancelButton = UIButton()
        cancelButton.setTitle("取消", forState: .Normal)
        cancelButton.setTitleColor(MalaColor_8FBCDD_0, forState: .Normal)
        cancelButton.setBackgroundImage(UIImage.withColor(MalaColor_FFFFFF_9), forState: .Normal)
        cancelButton.setTitleColor(MalaColor_B7B7B7_0, forState: .Highlighted)
        cancelButton.setBackgroundImage(UIImage.withColor(MalaColor_F8F8F8_0), forState: .Highlighted)
        cancelButton.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_15)
        cancelButton.addTarget(self, action: "cancelButtonDidTap", forControlEvents: .TouchUpInside)
        return cancelButton
    }()
    /// 确认按钮.[去评价]
    private lazy var confirmButton: UIButton = {
        let confirmButton = UIButton()
        confirmButton.setTitle("去评价", forState: .Normal)
        confirmButton.setTitleColor(MalaColor_8FBCDD_0, forState: .Normal)
        confirmButton.setBackgroundImage(UIImage.withColor(MalaColor_FFFFFF_9), forState: .Normal)
        confirmButton.setTitleColor(MalaColor_B7B7B7_0, forState: .Highlighted)
        confirmButton.setBackgroundImage(UIImage.withColor(MalaColor_F8F8F8_0), forState: .Highlighted)
        confirmButton.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_15)
        confirmButton.addTarget(self, action: "confirmButtonDidTap", forControlEvents: .TouchUpInside)
        return confirmButton
    }()
    /// 上课日期
    private lazy var courseDateLabel: UILabel = {
        let courseDateLabel = UILabel()
        courseDateLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        courseDateLabel.textColor = MalaColor_B7B7B7_0
        courseDateLabel.text = ""
        return courseDateLabel
    }()
    private lazy var contentContainer: UIView = {
        let contentContainer = UIView()
        return contentContainer
    }()
    /// 页标指示器
    lazy var pageControl: UIPageControl = {
        let pageControl = UIPageControl()
        pageControl.currentPage = 0
        pageControl.numberOfPages = 3
        pageControl.pageIndicatorTintColor = MalaColor_C7DEEE_0
        pageControl.currentPageIndicatorTintColor = MalaColor_82B4D9_0
        
        // 添加横线
        let view = UIView()
        pageControl.addSubview(view)
        view.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(pageControl.snp_left)
            make.right.equalTo(pageControl.snp_right)
            make.height.equalTo(MalaScreenOnePixel)
            make.centerY.equalTo(pageControl.snp_centerY)
        }
        view.backgroundColor = MalaColor_C7DEEE_0
        return pageControl
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
        if let view = contentView as? CourseContentView {
            view.container = self
        }
        updateUserInterface()
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
            closeAlert(0)
        }
    }
    

    // MARK: - API
    public func show() {
        animateAlert()
    }
    
    public func setButtonStatus(showClose showClose: Bool, showCancel: Bool, showConfirm: Bool) {
        cancelButton.hidden = !showCancel
        confirmButton.hidden = !showConfirm
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
        window.addSubview(iconView)
        window.addSubview(cancelButton)
        window.addSubview(confirmButton)
        window.addSubview(courseDateLabel)
        window.addSubview(pageControl)
        window.addSubview(contentContainer)
        window.addSubview(buttonTopLine)
        window.addSubview(buttonSeparatorLine)
        
        // Autolayout
        window.snp_makeConstraints { (make) -> Void in
            make.center.equalTo(self.view.snp_center)
            make.width.equalTo(MalaLayout_CoursePopupWindowWidth)
            make.height.equalTo(MalaLayout_CoursePopupWindowHeight)
        }
        iconView.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(self.window.snp_centerX)
            make.top.equalTo(self.window.snp_top).offset(-MalaLayout_FontSize_20)
            make.width.equalTo(MalaLayout_CoursePopupWindowTitleViewHeight)
            make.height.equalTo(MalaLayout_CoursePopupWindowTitleViewHeight)
        }
        courseDateLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.iconView.snp_bottom).offset(MalaLayout_Margin_10)
            make.centerX.equalTo(self.iconView.snp_centerX)
            make.height.equalTo(MalaLayout_FontSize_15)
        }
        contentContainer.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.courseDateLabel.snp_bottom).offset(MalaLayout_Margin_12)
            make.left.equalTo(self.window.snp_left).offset(MalaLayout_Margin_26)
            make.right.equalTo(self.window.snp_right).offset(-MalaLayout_Margin_26)
            make.bottom.equalTo(self.pageControl.snp_top).offset(-MalaLayout_Margin_10)
        }
        pageControl.snp_makeConstraints { (make) -> Void in
            make.width.equalTo(36)
            make.height.equalTo(6)
            make.bottom.equalTo(self.cancelButton.snp_top).offset(-MalaLayout_Margin_10)
            make.centerX.equalTo(self.window.snp_centerX)
        }
        cancelButton.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.window.snp_bottom)
            make.left.equalTo(self.window.snp_left)
            make.height.equalTo(44)
            make.width.equalTo(self.window.snp_width).multipliedBy(0.5)
        }
        confirmButton.snp_makeConstraints { (make) -> Void in
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
    
    private func animateAlert() {
        view.alpha = 0;
        let originTransform = self.window.transform
        self.window.layer.transform = CATransform3DMakeScale(0.7, 0.7, 0.0);
        
        UIView.animateWithDuration(0.35) { () -> Void in
            self.view.alpha = 1.0
            self.window.transform = originTransform
        }
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
    
    @objc private func cancelButtonDidTap() {
        close()
    }
    
    @objc private func confirmButtonDidTap() {
        
    }
}