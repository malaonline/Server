//
//  CommentPopupWindow.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit


public class CommentPopupWindow: UIViewController {

    // MARK: - Property
    var model: CourseModel = CourseModel() {
        didSet {
            println("评论视图 - 放置模型: \(model)")
            avatarView.kf_setImageWithURL(model.teacher?.avatar ?? NSURL())
            teacherNameLabel.text = model.teacher?.name
            subjectLabel.text = model.subject
        }
    }
    
    /// 自身强引用
    var strongSelf: CommentPopupWindow?
    /// 遮罩层透明度
    let tBakcgroundTansperancy: CGFloat = 0.7
    /// 布局容器（窗口）
    var window = UIView()
    /// 内容视图
    var contentView: UIView?
    /// 单击背景close窗口
    var closeWhenTap: Bool = false
    
    
    // MARK: - Components
    /// 标题视图
    private lazy var titleView: UILabel = {
        let titleView = UILabel(title: "评价")
        titleView.textColor = MalaColor_8FBCDD_0
        titleView.font = UIFont.systemFontOfSize(MalaLayout_FontSize_16)
        titleView.textAlignment = .Center
        return titleView
    }()
    /// 关闭按钮
    private lazy var closeButton: UIButton = {
        let cancelButton = UIButton()
        cancelButton.setBackgroundImage(UIImage(named: "close"), forState: .Normal)
        cancelButton.addTarget(self, action: "closeButtonDidTap", forControlEvents: .TouchUpInside)
        return cancelButton
    }()
    /// 顶部装饰线
    private lazy var titleLine: UIView = {
        let titleLine = UIView()
        titleLine.backgroundColor = MalaColor_8FBCDD_0
        return titleLine
    }()
    /// 老师头像
    private lazy var avatarView: UIImageView = {
        let avatarView = UIImageView(image: UIImage(named: "avatar_placeholder"))
        avatarView.layer.cornerRadius = avatarView.frame.width/2
        avatarView.layer.masksToBounds = true
        return avatarView
    }()
    /// 老师姓名label
    private lazy var teacherNameLabel: UILabel = {
        let teacherNameLabel = UILabel()
        teacherNameLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        teacherNameLabel.textColor = MalaColor_939393_0
        teacherNameLabel.text = "老师姓名"
        return teacherNameLabel
    }()
    /// 教授科目label
    private lazy var subjectLabel: UILabel = {
        let subjectLabel = UILabel()
        subjectLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        subjectLabel.textColor = MalaColor_BEBEBE_0
        subjectLabel.text = "教授科目"
        return subjectLabel
    }()
    /// 评分面板
    private lazy var floatRating: FloatRatingView = {
        let floatRating = FloatRatingView()
        return floatRating
    }()
    /// 输入文本框
    private lazy var textView: UITextView = {
        let textView = UITextView()
        textView.backgroundColor = MalaColor_BEBEBE_0
        return textView
    }()
    /// 提交按钮装饰线
    private lazy var buttonSeparatorLine: UIView = {
        let buttonSeparatorLine = UIView()
        buttonSeparatorLine.backgroundColor = MalaColor_8FBCDD_0
        return buttonSeparatorLine
    }()
    /// 提交按钮
    private lazy var commitButton: UIButton = {
        let commitButton = UIButton()
        commitButton.setTitle("提  交", forState: .Normal)
        commitButton.setTitleColor(MalaColor_BCD7EB_0, forState: .Normal)
        commitButton.setBackgroundImage(UIImage.withColor(MalaColor_FFFFFF_9), forState: .Normal)
        commitButton.setBackgroundImage(UIImage.withColor(MalaColor_F8F8F8_0), forState: .Highlighted)
        commitButton.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_15)
        commitButton.addTarget(self, action: "commitButtonDidTap", forControlEvents: .TouchUpInside)
        return commitButton
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
//        if let view = contentView as? CourseContentView {
//            view.container = self
//        }
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
        view.addGestureRecognizer(UITapGestureRecognizer(target: self, action: "backgroundDidTap"))
        window.backgroundColor = UIColor.whiteColor()
        
        // SubViews
        view.addSubview(window)
        window.addSubview(titleView)
        window.addSubview(closeButton)
        window.addSubview(titleLine)
        window.addSubview(avatarView)
        window.addSubview(teacherNameLabel)
        window.addSubview(subjectLabel)
        window.addSubview(floatRating)
        window.addSubview(textView)
        window.addSubview(buttonSeparatorLine)
        window.addSubview(commitButton)
        
        // Autolayout
        window.snp_makeConstraints { (make) -> Void in
            make.center.equalTo(self.view.snp_center)
            make.width.equalTo(MalaLayout_CommentPopupWindowWidth)
            make.height.equalTo(MalaLayout_CommentPopupWindowHeight)
        }
        titleView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.window.snp_top)
            make.left.equalTo(self.window.snp_left)
            make.right.equalTo(self.window.snp_right)
            make.height.equalTo(44)
        }
        closeButton.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(titleView.snp_centerY)
            make.right.equalTo(self.window.snp_right).offset(-MalaLayout_Margin_12)
        }
        titleLine.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(titleView.snp_bottom)
            make.height.equalTo(MalaScreenOnePixel)
            make.left.equalTo(self.window.snp_left)
            make.right.equalTo(self.window.snp_right)
        }
        avatarView.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(self.window.snp_centerX)
            make.top.equalTo(titleLine.snp_bottom).offset(MalaLayout_Margin_10)
            make.width.equalTo(MalaLayout_CoursePopupWindowTitleViewHeight)
            make.height.equalTo(MalaLayout_CoursePopupWindowTitleViewHeight)
        }
        teacherNameLabel.snp_makeConstraints { (make) -> Void in
            make.right.equalTo(avatarView.snp_centerX).offset(-MalaLayout_Margin_5)
            make.top.equalTo(avatarView.snp_bottom).offset(MalaLayout_Margin_10)
            make.height.equalTo(MalaLayout_FontSize_13)
        }
        subjectLabel.snp_makeConstraints { (make) -> Void in
            make.left.equalTo(avatarView.snp_centerX).offset(MalaLayout_Margin_5)
            make.top.equalTo(avatarView.snp_bottom).offset(MalaLayout_Margin_10)
            make.height.equalTo(MalaLayout_FontSize_13)
        }
        floatRating.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(subjectLabel.snp_bottom).offset(MalaLayout_Margin_12)
            make.centerX.equalTo(avatarView.snp_centerX)
            make.height.equalTo(30)
            make.width.equalTo(120)
        }
        textView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(floatRating.snp_bottom).offset(MalaLayout_Margin_8)
            make.left.equalTo(self.window.snp_left).offset(MalaLayout_Margin_26)
            make.right.equalTo(self.window.snp_right).offset(-MalaLayout_Margin_26)
            make.bottom.equalTo(buttonSeparatorLine.snp_top).offset(-MalaLayout_Margin_12)
        }
        buttonSeparatorLine.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(commitButton.snp_top)
            make.left.equalTo(self.window.snp_left)
            make.right.equalTo(self.window.snp_right)
            make.height.equalTo(MalaScreenOnePixel)
        }
        commitButton.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.window.snp_bottom)
            make.left.equalTo(self.window.snp_left)
            make.right.equalTo(self.window.snp_right)
            make.height.equalTo(44)
        }
    }
    
    private func updateUserInterface() {
        // SubViews
        
        // Autolayout

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
    
    @objc public  func backgroundDidTap() {
        if closeWhenTap {
            closeAlert(0)
        }
    }
    
    @objc private func closeButtonDidTap() {
        close()
    }
    
    @objc private func commitButtonDidTap() {
        
    }
}