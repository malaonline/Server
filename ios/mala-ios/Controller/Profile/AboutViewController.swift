//
//  AboutViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 3/15/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class AboutViewController: BaseViewController, UIScrollViewDelegate {

    // MARK: - Components
    /// 容器
    private lazy var scrollView: UIScrollView = {
        let scrollView = UIScrollView(frame: UIScreen.mainScreen().bounds)
        scrollView.scrollEnabled = true
        return scrollView
    }()
    /// 应用logoView
    private lazy var appLogoView: UIImageView = {
        let imageView = UIImageView()
        imageView.image = UIImage(named: "AppIcon60x60")
        return imageView
    }()
    /// 应用版本号label
    private lazy var appVersionLabel: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(14)
        label.textColor = MalaColor_333333_0
        label.text = MalaConfig.aboutAPPVersion()
        return label
    }()
    /// 版权信息label
    private lazy var copyrightLabel: UILabel = {
        let label = UILabel()
        label.textAlignment = .Center
        label.numberOfLines = 2
        label.font = UIFont.systemFontOfSize(12)
        label.textColor = MalaColor_939393_0
        label.text = MalaConfig.aboutCopyRightString()
        return label
    }()
    /// 描述 文字背景
    private lazy var textBackground: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "aboutText_Background"))
        return imageView
    }()
    /// 描述标题
    private lazy var titleView: AboutTitleView = {
        let view = AboutTitleView()
        view.title = MalaCommonString_Malalaoshi
        return view
    }()
    /// 关于描述label
    private lazy var aboutTextView: UILabel = {
        let label = UILabel()
        label.numberOfLines = 0
        label.font = UIFont.systemFontOfSize(13)
        label.textColor = MalaColor_939393_0
        label.text = MalaConfig.aboutDescriptionHTMLString()
        label.backgroundColor = MalaColor_F2F2F2_0
        return label
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        setupUserInterface()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    
    // MARK: - Private
    private func setupUserInterface() {
        // Style
        scrollView.backgroundColor = MalaColor_F2F2F2_0
        
        
        // SubViews
        view.addSubview(scrollView)
        scrollView.addSubview(appLogoView)
        scrollView.addSubview(appVersionLabel)
        scrollView.addSubview(copyrightLabel)
        scrollView.addSubview(textBackground)
        
        textBackground.addSubview(titleView)
        textBackground.addSubview(aboutTextView)
        
        // Autolayout
        scrollView.snp_makeConstraints { (make) -> Void in
            make.size.equalTo(view.snp_size)
            make.top.equalTo(view.snp_top)
            make.left.equalTo(view.snp_left)
        }
        appLogoView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(scrollView.snp_top).offset(24)
            make.centerX.equalTo(scrollView.snp_centerX)
            make.width.equalTo(MalaLayout_AboutAPPLogoViewHeight)
            make.height.equalTo(MalaLayout_AboutAPPLogoViewHeight)
        }
        appVersionLabel.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(scrollView.snp_centerX)
            make.top.equalTo(appLogoView.snp_bottom).offset(12)
            make.height.equalTo(14)
        }
        copyrightLabel.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(scrollView.snp_centerX)
            make.top.equalTo(appVersionLabel.snp_bottom).offset(12)
        }
        textBackground.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(copyrightLabel.snp_bottom).offset(12)
            make.centerX.equalTo(scrollView.snp_centerX)
            make.left.equalTo(scrollView.snp_left).offset(12)
            make.right.equalTo(scrollView.snp_right).offset(-12)
        }
        titleView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(textBackground.snp_top).offset(18)
            make.left.equalTo(textBackground.snp_left)
            make.right.equalTo(textBackground.snp_right)
        }
        aboutTextView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(titleView.snp_bottom).offset(18)
            make.left.equalTo(textBackground.snp_left).offset(18)
            make.right.equalTo(textBackground.snp_right).offset(-18)
            make.bottom.equalTo(textBackground.snp_bottom).offset(-18)
        }
        
        upateContentSize()
    }
    
    private func upateContentSize() {
        scrollView.contentSize = CGSize(width: 0, height: CGRectGetMaxY(aboutTextView.frame))
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        scrollView.frame = UIScreen.mainScreen().bounds
        scrollView.contentSize = CGSize(width: 0, height: CGRectGetMaxY(aboutTextView.frame))
    }
}