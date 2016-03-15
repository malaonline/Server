//
//  AboutViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 3/15/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class AboutViewController: UIViewController, UIScrollViewDelegate {

    // MARK: - Property
    
    
    
    // MARK: - Components
    /// 容器
    private lazy var scrollView: UIScrollView = {
        let scrollView = UIScrollView(frame: UIScreen.mainScreen().bounds)
        scrollView.scrollEnabled = true
        return scrollView
    }()
    /// 应用logoView
    private lazy var appLogoView: UIImageView = {
        let appLogoView = UIImageView()
        appLogoView.image = UIImage(named: "applogo")
        return appLogoView
    }()
    /// 应用版本号label
    private lazy var appVersionLabel: UILabel = {
        let appVersionLabel = UILabel()
        appVersionLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_14)
        appVersionLabel.textColor = MalaDetailsCellTitleColor
        appVersionLabel.text = String(format: "版本 V%.1f", MalaConfig.aboutAPPVersion())
        return appVersionLabel
    }()
    /// 版权信息label
    private lazy var copyrightLabel: UILabel = {
        let copyrightLabel = UILabel()
        copyrightLabel.textAlignment = .Center
        copyrightLabel.numberOfLines = 2
        copyrightLabel.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        copyrightLabel.textColor = MalaDetailsCellSubTitleColor
        copyrightLabel.text = MalaConfig.aboutCopyRightString()
        return copyrightLabel
    }()
    /// 关于描述label
    private lazy var aboutTextView: UITextView = {
        let aboutTextView = UITextView()
        aboutTextView.editable = false
        aboutTextView.font = UIFont.systemFontOfSize(MalaLayout_FontSize_13)
        aboutTextView.textColor = MalaDetailsCellSubTitleColor
        aboutTextView.text = MalaConfig.aboutDescriptionHTMLString()
        aboutTextView.backgroundColor = MalaProfileBackgroundColor
        return aboutTextView
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
        scrollView.backgroundColor = MalaProfileBackgroundColor
        
        
        // SubViews
        view.addSubview(scrollView)
        scrollView.addSubview(appLogoView)
        scrollView.addSubview(appVersionLabel)
        scrollView.addSubview(copyrightLabel)
        scrollView.addSubview(aboutTextView)
        
        // Autolayout
        scrollView.snp_makeConstraints { (make) -> Void in
            make.size.equalTo(view.snp_size)
            make.top.equalTo(view.snp_top)
            make.left.equalTo(view.snp_left)
        }
        appLogoView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(scrollView.snp_top).offset(MalaLayout_Margin_24)
            make.centerX.equalTo(scrollView.snp_centerX)
            make.width.equalTo(MalaLayout_AboutAPPLogoViewHeight)
            make.height.equalTo(MalaLayout_AboutAPPLogoViewHeight)
        }
        appVersionLabel.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(scrollView.snp_centerX)
            make.top.equalTo(appLogoView.snp_bottom).offset(MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_14)
        }
        copyrightLabel.snp_makeConstraints { (make) -> Void in
            make.centerX.equalTo(scrollView.snp_centerX)
            make.top.equalTo(appVersionLabel.snp_bottom).offset(MalaLayout_Margin_12)
        }
        aboutTextView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(copyrightLabel.snp_bottom).offset(MalaLayout_Margin_12)
            make.centerX.equalTo(scrollView.snp_centerX)
            make.left.equalTo(scrollView.snp_left).offset(MalaLayout_Margin_12)
            make.right.equalTo(scrollView.snp_right).offset(-MalaLayout_Margin_12)
            make.height.equalTo(200)
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