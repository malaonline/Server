//
//  TeacherFilterPopupWindow.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

public class TeacherFilterPopupWindow: UIViewController {

    // MARK: - Property
    /// 自身强引用
    var strongSelf: TeacherFilterPopupWindow?
    /// 遮罩层透明度
    let tBakcgroundTansperancy: CGFloat = 0.7
    /// 布局容器（窗口）
    var window = UIView()
    /// 标题文字
    var tTitle: String = "  筛选年级  " {
        didSet {
            self.themeTitle.text = "  "+tTitle+"  "
        }
    }
    /// 图标名称
    var tIcon: String = "grade" {
        didSet {
            self.themeIcon.image = UIImage(named: tIcon)
        }
    }
    /// 内容视图
    var contentView: UIView?
    /// 单击背景close窗口
    var closeWhenTap: Bool = false
    
    
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
        closeButton.addTarget(self, action: #selector(TeacherFilterPopupWindow.pressed(_:)), forControlEvents: .TouchUpInside)
        return closeButton
    }()
    private lazy var cancelButton: UIButton = {
        let cancelButton = UIButton()
        cancelButton.setBackgroundImage(UIImage(named: "leftArrow_normal"), forState: .Normal)
        cancelButton.setBackgroundImage(UIImage(named: "leftArrow_press"), forState: .Selected)
        cancelButton.addTarget(self, action: #selector(TeacherFilterPopupWindow.cancelButtonDidTap), forControlEvents: .TouchUpInside)
        cancelButton.hidden = true
        return cancelButton
    }()
    private lazy var confirmButton: UIButton = {
        let confirmButton = UIButton()
        confirmButton.setBackgroundImage(UIImage(named: "confirm_normal"), forState: .Normal)
        confirmButton.setBackgroundImage(UIImage(named: "confirm_press"), forState: .Selected)
        confirmButton.addTarget(self, action: #selector(TeacherFilterPopupWindow.confirmButtonDidTap), forControlEvents: .TouchUpInside)
        confirmButton.hidden = true
        return confirmButton
    }()
    private lazy var themeTitle: UILabel = {
        let themeTitle = UILabel()
        themeTitle.font = UIFont(name: "HelveticaNeue", size: 15)
        themeTitle.backgroundColor = UIColor.whiteColor()
        themeTitle.textColor = MalaColor_939393_0
        themeTitle.text = self.tTitle
        return themeTitle
    }()
    private lazy var separator: UIView = {
        let separator = UIView()
        separator.backgroundColor = MalaColor_DADADA_0
        return separator
    }()
    private lazy var contentContainer: UIView = {
        let contentContainer = UIView()
        return contentContainer
    }()
    /// 页标指示器
    private lazy var pageControl: UIPageControl = {
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
        if let view = contentView as? FilterView {
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
    
    public func setButtonStatus(showClose showClose: Bool, showCancel: Bool, showConfirm: Bool) {
        closeButton.hidden = !showClose
        cancelButton.hidden = !showCancel
        confirmButton.hidden = !showConfirm
    }
    
    public func setPageControl(number: Int) {
        self.pageControl.currentPage = number
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
        window.addSubview(themeIcon)
        window.addSubview(closeButton)
        window.addSubview(cancelButton)
        window.addSubview(confirmButton)
        window.addSubview(themeTitle)
        window.insertSubview(separator, belowSubview: themeTitle)
        window.addSubview(pageControl)
        window.addSubview(contentContainer)
        
        // Autolayout
        window.snp_makeConstraints { (make) -> Void in
            make.center.equalTo(self.view.snp_center)
            make.width.equalTo(MalaLayout_FilterWindowWidth)
            make.height.equalTo(MalaLayout_FilterWindowHeight)
        }
        themeIcon.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(self.window.snp_centerX)
            make.top.equalTo(self.window.snp_top).offset(-20)
            make.width.equalTo(64)
            make.height.equalTo(64)
        }
        closeButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.window.snp_top).offset(7)
            make.left.equalTo(self.window.snp_left).offset(5)
        }
        cancelButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.window.snp_top).offset(7)
            make.left.equalTo(self.window.snp_left).offset(5)
        }
        confirmButton.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.window.snp_top).offset(7)
            make.right.equalTo(self.window.snp_right).offset(-5)
        }
        themeTitle.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.themeIcon.snp_bottom).offset(16)
            make.centerX.equalTo(self.themeIcon.snp_centerX)
            make.height.equalTo(15)
        }
        separator.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(self.themeTitle.snp_centerY)
            make.left.equalTo(self.window.snp_left).offset(26)
            make.right.equalTo(self.window.snp_right).offset(-26)
            make.height.equalTo(MalaScreenOnePixel)
        }
        pageControl.snp_makeConstraints { (make) -> Void in
            make.width.equalTo(36)
            make.height.equalTo(6)
            make.bottom.equalTo(self.window.snp_bottom).offset(-10)
            make.centerX.equalTo(self.window.snp_centerX)
        }
        contentContainer.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.themeTitle.snp_bottom).offset(12)
            make.left.equalTo(self.window.snp_left).offset(26)
            make.right.equalTo(self.window.snp_right).offset(-26)
            make.bottom.equalTo(self.pageControl.snp_top).offset(-10)
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
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_PopFilterView, object: nil)
    }
    
    @objc private func confirmButtonDidTap() {
        confirmButton.userInteractionEnabled = false
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_ConfirmFilterView, object: nil)
    }
    
    deinit{
        println("TeacherFilterPopupWindow - Deinit")
    }
}