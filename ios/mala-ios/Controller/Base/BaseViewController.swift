//
//  BaseViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/4/11.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit
import Kingfisher

public class BaseViewController: UIViewController {
    
    // MARK: - Components
    /// 导航栏返回按钮
    lazy var backBarButton: UIButton = {
        let backBarButton = UIButton(
            imageName: "leftArrow_black",
            highlightImageName: "leftArrow_black",
            target: self,
            action: #selector(BaseViewController.popSelf)
        )
        return backBarButton
    }()
    /// 无筛选结果缺省面板
    lazy var defaultView: MalaDefaultPanel = {
        let defaultView = MalaDefaultPanel()
        defaultView.hidden = true
        return defaultView
    }()
    

    // MARK: - Life Cycle
    override public func viewDidLoad() {
        super.viewDidLoad()
        configure()
    }

    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        KingfisherManager.sharedManager.cache.clearMemoryCache()
    }
    

    // MARK: - Private
    private func setupDefaultViewIfNeed() {
        if defaultView.superview == nil {
            // SubViews
            view.addSubview(defaultView)
            
            // AutoLayout
            defaultView.snp_makeConstraints { (make) -> Void in
                make.size.equalTo(view.snp_size)
                make.center.equalTo(view.snp_center)
            }
        }
    }
    
    private func configure() {
        
        // 设置BarButtomItem间隔
        let spacer = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
        spacer.width = -2
        
        // leftBarButtonItem
        let leftBarButtonItem = UIBarButtonItem(customView: backBarButton)
        navigationItem.leftBarButtonItems = [spacer, leftBarButtonItem]
    }

    
    // MARK: - Event Response
    @objc func popSelf() {
        self.navigationController?.popViewControllerAnimated(true)
    }
    
    @objc func turnBackButtonBlack() {
        backBarButton.setImage(UIImage(named: "leftArrow_black"), forState: .Normal)
    }
    
    @objc func turnBackButtonWhite() {
        backBarButton.setImage(UIImage(named: "leftArrow"), forState: .Normal)
    }
    
    
    // MARK: - API
    func showDefaultView() {
        setupDefaultViewIfNeed()
        defaultView.hidden = false
    }
    
    func hideDefaultView() {
        setupDefaultViewIfNeed()
        defaultView.hidden = true
    }
}