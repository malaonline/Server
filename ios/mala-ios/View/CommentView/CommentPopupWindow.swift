//
//  CommentPopupWindow.swift
//  mala-ios
//
//  Created by Elors on 1/16/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit


public class CommentPopupWindow: UIViewController, UITextViewDelegate {

    // MARK: - Property
    var model: CourseModel = CourseModel() {
        didSet {
            println("评论视图 - 放置模型: \(model)")
            
            avatarView.kf_setImageWithURL(model.teacher?.avatar ?? NSURL())
            teacherNameLabel.text = model.teacher?.name
            subjectLabel.text = model.subject
            textView.text = model.comment?.content
            floatRating.rating = Float((model.comment?.score) ?? 0)
        }
    }
    /// 是否仅为展示标记
    var isJustShow: Bool = true {
        didSet {
            // 若仅为展示用图，调整UI及交互样式
            switchingModeWithJustShow(isJustShow: isJustShow)
        }
    }
    /// 评价编辑模式标记
    var isEditingMode: Bool = false {
        didSet {
            // 切换为 [评价编辑模式] 或 [普通评价模式]
            if isEditingMode && (isEditingMode != oldValue) {
                changeToEditingMode()
            }else {
                changeToNormalMode(animated: true)
            }
        }
    }
    private var TextViewMaximumLength: Int = 200
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
    /// 老师信息及评分控件容器
    private lazy var teacherContainer: UIView = {
        let teacherContainer = UIView()
        return teacherContainer
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
        floatRating.delegate = self
        return floatRating
    }()
    /// 描述 文字背景
    private lazy var textBackground: UIImageView = {
        let textBackground = UIImageView(image: UIImage(named: "aboutText_Background"))
        return textBackground
    }()
    /// 输入文本框
    private lazy var textView: UITextView = {
        let textView = UITextView()
        textView.delegate = self
        textView.textContainerInset = UIEdgeInsets(top: 0, left: 0, bottom: 0, right: 0)
        textView.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        textView.textColor = MalaColor_D4D4D4_0
        textView.text = "请写下对老师的感受吧，对他人的帮助很大哦~最多可输入200字"
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
        window.addSubview(textBackground)
        window.addSubview(textView)
        window.addSubview(buttonSeparatorLine)
        window.addSubview(commitButton)
        
        // Autolayout
        changeToNormalMode(animated: false)
    }
    
    ///  设置UI为普通模式
    private func changeToNormalMode(animated animated: Bool) {
        
        if animated {
            removeAllConstraints()
        }
        
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
        textBackground.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(floatRating.snp_bottom).offset(MalaLayout_Margin_8)
            make.left.equalTo(self.window.snp_left).offset(MalaLayout_Margin_18)
            make.right.equalTo(self.window.snp_right).offset(-MalaLayout_Margin_18)
            make.bottom.equalTo(buttonSeparatorLine.snp_top).offset(-MalaLayout_Margin_12)
        }
        textView.snp_makeConstraints { (make) -> Void in
            make.edges.equalTo(textBackground).inset(
                UIEdgeInsets(
                    top: MalaLayout_Margin_12,
                    left: MalaLayout_Margin_5,
                    bottom: MalaLayout_Margin_12,
                    right: MalaLayout_Margin_5
                )
            )
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
        
        // Animate
        if animated {
            self.window.setNeedsUpdateConstraints()
            UIView.animateWithDuration(0.35) { [weak self] () -> Void in
                self?.avatarView.alpha = 1
                self?.teacherNameLabel.alpha = 1
                self?.subjectLabel.alpha = 1
                self?.floatRating.alpha = 1
                self?.window.layoutIfNeeded()
            }
        }
    }
    
    ///  设置UI为编辑模式
    private func changeToEditingMode() {
        
        // Autolayout
        window.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.view.snp_top).offset(36)
            make.centerX.equalTo(self.view.snp_centerX)
            make.width.equalTo(MalaLayout_CommentPopupWindowWidth)
            make.height.equalTo(MalaLayout_CommentPopupWindowHeight)
        }
        textBackground.snp_updateConstraints(closure: { (make) -> Void in
            make.top.equalTo(titleLine.snp_bottom).offset(MalaLayout_Margin_12)
            make.left.equalTo(self.window.snp_left).offset(MalaLayout_Margin_18)
            make.right.equalTo(self.window.snp_right).offset(-MalaLayout_Margin_18)
            make.bottom.equalTo(buttonSeparatorLine.snp_top).offset(-MalaLayout_Margin_12)
        })
        avatarView.snp_updateConstraints { (make) -> Void in
            make.centerX.equalTo(self.window.snp_centerX)
            make.top.equalTo(titleLine.snp_bottom)
            make.width.equalTo(0)
            make.height.equalTo(0)
        }
        teacherNameLabel.snp_updateConstraints { (make) -> Void in
            make.right.equalTo(avatarView.snp_centerX).offset(-MalaLayout_Margin_5)
            make.top.equalTo(avatarView.snp_bottom).offset(MalaLayout_Margin_10)
            make.height.equalTo(0)
        }
        subjectLabel.snp_updateConstraints { (make) -> Void in
            make.left.equalTo(avatarView.snp_centerX).offset(MalaLayout_Margin_5)
            make.top.equalTo(avatarView.snp_bottom).offset(MalaLayout_Margin_10)
            make.height.equalTo(0)
        }
        floatRating.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(subjectLabel.snp_bottom)
            make.centerX.equalTo(avatarView.snp_centerX)
            make.height.equalTo(0)
            make.width.equalTo(0)
        }
        
        // Animate
        self.window.setNeedsUpdateConstraints()
        UIView.animateWithDuration(0.35) { [weak self] () -> Void in
            self?.avatarView.alpha = 0
            self?.teacherNameLabel.alpha = 0
            self?.subjectLabel.alpha = 0
            self?.floatRating.alpha = 0
            self?.window.layoutIfNeeded()
        }
    }
    
    ///  删除所有约束
    private func removeAllConstraints() {
        window.snp_removeConstraints()
        titleView.snp_removeConstraints()
        closeButton.snp_removeConstraints()
        titleLine.snp_removeConstraints()
        avatarView.snp_removeConstraints()
        teacherNameLabel.snp_removeConstraints()
        subjectLabel.snp_removeConstraints()
        floatRating.snp_removeConstraints()
        textView.snp_removeConstraints()
        buttonSeparatorLine.snp_removeConstraints()
        commitButton.snp_removeConstraints()
    }
    
    ///  根据 [是否仅为展示标记] 调整 UI、UX
    private func switchingModeWithJustShow(isJustShow justShow: Bool) {
        if justShow {
            // 提交按钮
            commitButton.removeTarget(self, action: "commitButtonDidTap", forControlEvents: .TouchUpInside)
            commitButton.addTarget(self, action: "closeButtonDidTap", forControlEvents: .TouchUpInside)
            commitButton.setTitle("知道了", forState: .Normal)
            // 评价文本框
            textView.userInteractionEnabled = false
            textView.textColor = MalaColor_939393_0
            // 评分组件
            floatRating.editable = false
        }else {
            
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
    
    private func adjustTextViewPlaceholder(isShow isShow: Bool) {
        if isShow {
            textView.textColor = MalaColor_D4D4D4_0
            textView.text = "请写下对老师的感受吧，对他人的帮助很大哦~最多可输入200字"
        }else {
            textView.textColor = MalaColor_939393_0
        }
    }
    
    
    // MARK: - Delegate
    public func textViewDidBeginEditing(textView: UITextView) {
        // 用户开始输入时，展开输入区域
        changeToEditingMode()
        adjustTextViewPlaceholder(isShow: false)
    }
    
    public func textViewDidChange(textView: UITextView) {

        // 保证用户联想词汇同样会被捕捉
        if textView.text.characters.count > TextViewMaximumLength {
            textView.text = textView.text.substringToIndex(textView.text.startIndex.advancedBy(TextViewMaximumLength-1))
        }
    }
    
    /*
    public func textView(textView: UITextView, shouldChangeTextInRange range: NSRange, replacementText text: String) -> Bool {
        
        // 限制字数为200
        if text == "" && range.length > 0 {
            return true
        }else {
            if (textView.text.characters.count - range.length + text.characters.count > TextViewMaximumLength) {
                // 超出最大输入字数
                return false
            }else {
                return true
            }
        }
    }
    */
    
    public func textViewDidEndEditing(textView: UITextView) {
        // 用户停止输入时，恢复初始布局
        changeToNormalMode(animated: true)
        
        // 结束编辑时若没有输入，则显示占位文字
        if textView.text == "" {
            adjustTextViewPlaceholder(isShow: true)
        }
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