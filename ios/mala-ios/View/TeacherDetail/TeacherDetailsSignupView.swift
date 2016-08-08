//
//  TeacherDetailsSignupView.swift
//  mala-ios
//
//  Created by Elors on 1/8/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

// MARK: - SignupButtonDelegate
protocol SignupButtonDelegate: class, NSObjectProtocol {
    func signupButtonDidTap(sender: UIButton)
    func likeButtonDidTap(sender: DOFavoriteButton)
}

// MARK: - TeacherDetailsSignupView
class TeacherDetailsSignupView: UIView {
    
    // MARK: - Property
    weak var delegate: SignupButtonDelegate?
    /// 是否已上架标识
    var isPublished: Bool = false {
        didSet {
            println("已上架标识 - \(isPublished)")
            adjustUIWithPublished()
        }
    }
    /// 是否已收藏标识
    var isFavorite: Bool = false {
        didSet {
            println("已收藏标识 - \(isFavorite)")
            adjustUIWithFavorite()
        }
    }
    
    
    // MARK: - Components
    /// 装饰线
    private lazy var topLine: UIView = {
        let view = UIView()
        view.backgroundColor = UIColor.blackColor()
        view.alpha = 0.25
        return view
    }()
    /// 收藏操作区域
    private lazy var likeView: UIView = {
        let view = UIView()
//        view.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(TeacherDetailsSignupView.likeButtonDidTap)))
//        view.userInteractionEnabled = true
        return view
    }()
    /// 收藏文字描述
    private lazy var likeString: UIButton = {
        let button = UIButton()
        button.setTitle("收藏", forState: .Normal)
        button.setTitle("已收藏", forState: .Selected)
        button.titleLabel?.font = UIFont.systemFontOfSize(10)
        button.setTitleColor(MalaColor_6C6C6C_0, forState: .Normal)
        button.userInteractionEnabled = false
        return button
    }()
    /// 收藏按钮
    private lazy var likeButton: DOFavoriteButton = {
        let button = DOFavoriteButton(frame: CGRectMake(0, 0, 44, 44), image: UIImage(named: "heart"))
        button.imageColorOn = MalaColor_F76E6D_0
        button.circleColor = MalaColor_F76E6D_0
        button.lineColor = MalaColor_F76E6D_0
        button.addTarget(self, action: #selector(TeacherDetailsSignupView.likeButtonDidTap(_:)), forControlEvents: .TouchUpInside)
        return  button
    }()
    /// 报名按钮
    private lazy var button: UIButton = {
        let button = UIButton()
        button.setTitle("马上报名", forState: .Normal)
        button.setTitle("该老师已下架", forState: .Disabled)
        button.setTitleColor(MalaColor_FFFFFF_9, forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(MalaColor_7FB4DC_0), forState: .Normal)
        button.setBackgroundImage(UIImage.withColor(MalaColor_E0E0E0_0), forState: .Disabled)
        button.setBackgroundImage(UIImage.withColor(MalaColor_B2CDE1_0), forState: .Highlighted)
        button.addTarget(self, action: #selector(TeacherDetailsSignupView.signupButtonDidTap(_:)), forControlEvents: .TouchUpInside)
        return button
    }()
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUserInterface()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        self.backgroundColor = MalaColor_F6F6F6_96

        // SubViews
        addSubview(topLine)
        addSubview(likeView)
        likeView.addSubview(likeButton)
        likeView.addSubview(likeString)
        addSubview(button)
        
        // Autolayout
        topLine.snp_makeConstraints(closure: { (make) -> Void in
            make.left.equalTo(0)
            make.width.equalTo(MalaScreenWidth)
            make.top.equalTo(self.snp_top)
            make.height.equalTo(MalaScreenOnePixel)
        })
        likeView.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(topLine.snp_bottom)
            make.left.equalTo(self.snp_left)
            make.bottom.equalTo(self.snp_bottom)
            make.right.equalTo(self.snp_right).multipliedBy(0.422)
        })
        likeButton.snp_makeConstraints(closure: { (make) -> Void in
            make.centerX.equalTo(likeView.snp_centerX)
            make.centerY.equalTo(likeView.snp_centerY).offset(-8)
            make.height.equalTo(44)
            make.width.equalTo(44)
        })
        likeString.snp_makeConstraints(closure: { (make) -> Void in
            make.centerX.equalTo(likeView.snp_centerX)
            make.centerY.equalTo(likeView.snp_centerY).offset(10)
            make.height.equalTo(10)
            make.width.equalTo(40)
        })
        button.snp_makeConstraints(closure: { (make) -> Void in
            make.top.equalTo(topLine.snp_bottom)
            make.left.equalTo(likeView.snp_right)
            make.bottom.equalTo(self.snp_bottom)
            make.right.equalTo(self.snp_right)
        })
    }
    
    ///  根据上架情况调整UI
    private func adjustUIWithPublished() {
        if isPublished {
            button.enabled = true
            button.userInteractionEnabled = true
        }else {
            button.enabled = false
            button.userInteractionEnabled = false
        }
    }
    
    ///  根据收藏情况调整UI
    private func adjustUIWithFavorite() {
        likeButton.selected = isFavorite
        if likeButton.selected  {
            likeButton.select()
        }else {
            likeButton.deselect()
        }
        likeString.selected = isFavorite
    }
    
    
    // MARK: - Event Response
    @objc private func signupButtonDidTap(sender: UIButton) {
        delegate?.signupButtonDidTap(sender)
    }
 
    @objc private func likeButtonDidTap(sender: DOFavoriteButton) {
        
        // 屏蔽1.25秒内操作，防止连续点击
        likeButton.userInteractionEnabled = false
        delay(1.25) {
            self.likeButton.userInteractionEnabled = true
        }
        
        delegate?.likeButtonDidTap(sender)
    }
    
    deinit {
        println("TeacherDetailsSignupView Deinit")
    }
}