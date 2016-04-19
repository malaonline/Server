//
//  BaseViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/4/11.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

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

    // MARK: - Life Cycle
    override public func viewDidLoad() {
        super.viewDidLoad()
        configure()
    }

    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    

    // MARK: - Private 
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
}