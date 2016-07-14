//
//  ProfileViewHeaderView.swift
//  mala-ios
//
//  Created by 王新宇 on 3/10/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

protocol ProfileViewHeaderViewDelegate: NSObjectProtocol {
    func avatarViewDidTap(sender: UIImageView)
    func nameEditButtonDidTap(sender: UIButton)
}

class ProfileViewHeaderView: UIView {

    // MARK: - Property
    /// 学生姓名
    var name: String = "学生姓名" {
        didSet {
            nameLabel.text = name
        }
    }
    /// 用户头像URL
    var avatarURL: String = "" {
        didSet {
            avatarView.ma_setImage(NSURL(string: avatarURL) ?? NSURL(), placeholderImage: UIImage(named: "profileAvatar_placeholder"))
        }
    }
    /// 用户头像
    var avatar: UIImage = UIImage(named: "profileAvatar_placeholder") ?? UIImage() {
        didSet {
            avatarView.image = avatar
        }
    }
    /// 头像刷新指示器
    var refreshAvatar: Bool = false {
        didSet {
            refreshAvatar ? activityIndicator.startAnimating() : activityIndicator.stopAnimating()
        }
    }
    weak var delegate: ProfileViewHeaderViewDelegate?
    
    
    // MARK: - Components
    /// 头像ImageView控件
    private lazy var avatarView: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "avatar_placeholder"))
        imageView.layer.cornerRadius = (MalaLayout_AvatarSize-5)*0.5
        imageView.layer.masksToBounds = true
        imageView.userInteractionEnabled = true
        imageView.contentMode = .ScaleAspectFill
        imageView.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(ProfileViewHeaderView.avatarViewDidTap(_:))))
        return imageView
    }()
    /// 头像背景
    private lazy var avatarBackground: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.whiteColor()
        view.layer.cornerRadius = MalaLayout_AvatarSize*0.5
        view.layer.masksToBounds = true
        return view
    }()
    /// 姓名label控件
    private lazy var nameLabel: UILabel = {
        let label = UILabel()
        label.textColor = MalaColor_82B4D9_0
        label.font = UIFont.systemFontOfSize(14)
        label.textAlignment = .Center
        label.userInteractionEnabled = true
        label.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(ProfileViewHeaderView.nameEditButtonDidTap(_:))))
        return label
    }()
    /// 姓名修改按钮
    private lazy var editButton: UIButton = {
        let button = UIButton()
        button.setBackgroundImage(UIImage(named: "edit_icon"), forState: .Normal)
        button.addTarget(self, action: #selector(ProfileViewHeaderView.nameEditButtonDidTap(_:)), forControlEvents: .TouchUpInside)
        return button
    }()
    /// 头像刷新指示器
    private lazy var activityIndicator: UIActivityIndicatorView = {
        let activityIndicator = UIActivityIndicatorView()
        activityIndicator.hidesWhenStopped = true
        activityIndicator.hidden = true
        return activityIndicator
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        setupUserInterface()
        setupNotification()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    // MARK: - Private
    private func setupUserInterface() {
        // Style
        backgroundColor = UIColor.clearColor()
        self.avatarURL = MalaUserDefaults.avatar.value ?? ""
        
        // SubViews
        addSubview(avatarBackground)
        addSubview(avatarView)
        addSubview(nameLabel)
        addSubview(editButton)
        avatarView.addSubview(activityIndicator)
        
        // Autolayout
        avatarBackground.snp_makeConstraints { (make) in
            make.top.equalTo(self.snp_top).offset(16)
            make.centerX.equalTo(self.snp_centerX)
            make.width.equalTo(MalaLayout_AvatarSize)
            make.height.equalTo(MalaLayout_AvatarSize)
        }
        avatarView.snp_makeConstraints(closure: { (make) -> Void in
            make.center.equalTo(self.avatarBackground.snp_center)
            make.size.equalTo(self.avatarBackground.snp_size).offset(-5)
        })
        nameLabel.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(avatarView.snp_bottom).offset(10)
            make.centerX.equalTo(avatarView.snp_centerX)
            make.height.equalTo(14)
        }
        editButton.snp_makeConstraints { (make) -> Void in
            make.centerY.equalTo(nameLabel.snp_centerY)
            make.left.equalTo(nameLabel.snp_right).offset(3)
            make.width.equalTo(9)
            make.height.equalTo(13)
        }
        activityIndicator.snp_makeConstraints { (make) -> Void in
            make.center.equalTo(avatarView.snp_center)
        }
    }
    
    private func setupNotification() {
        // 刷新学生姓名
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_RefreshStudentName,
            object: nil,
            queue: nil) { [weak self] (notification) -> Void in
                self?.nameLabel.text = MalaUserDefaults.studentName.value
        }
    }
    
    
    // MARK: - Event Response
    @objc private func avatarViewDidTap(sender: UIImageView) {
        self.delegate?.avatarViewDidTap(sender)
    }
    
    @objc private func nameEditButtonDidTap(sender: UIButton) {
        self.delegate?.nameEditButtonDidTap(sender)
    }
    
    // MARK: - Public Method
    ///  使用UserDefaults中的头像URL刷新头像
    func refreshDataWithUserDefaults() {
        avatarURL = MalaUserDefaults.avatar.value ?? ""
        name = MalaUserDefaults.studentName.value ?? ""
    }
    
    deinit {
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_RefreshStudentName, object: nil)
    }
}