//
//  BaseTableViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/4/11.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

public class BaseTableViewController: UITableViewController {

    // MARK: - Components
    /// 无筛选结果缺省面板
    lazy var defaultView: MalaDefaultPanel = {
        let defaultView = MalaDefaultPanel()
        defaultView.hidden = true
        return defaultView
    }()
    
    
    // MARK: - Life Cycle
    override public func viewDidLoad() {
        super.viewDidLoad()
        setupUserInterface()
        configure()
    }
    
    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Private
    private func setupUserInterface() {
        // SubViews
        view.addSubview(defaultView)
        
        // AutoLayout
        defaultView.snp_makeConstraints { (make) -> Void in
            make.size.equalTo(view.snp_size)
            make.center.equalTo(view.snp_center)
        }
    }
    
    private func configure() {
        
        // 设置BarButtomItem间隔
        let spacer = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
        spacer.width = -2
        
        // leftBarButtonItem
        let leftBarButtonItem = UIBarButtonItem(customView:
            UIButton(
                imageName: "leftArrow_black",
                highlightImageName: "leftArrow_black",
                target: self,
                action: #selector(BaseTableViewController.popSelf)
            )
        )
        navigationItem.leftBarButtonItems = [spacer, leftBarButtonItem]
    }
    
    
    // MARK: - Event Response
    @objc func popSelf() {
        self.navigationController?.popViewControllerAnimated(true)
    }
    
    
    // MARK: - API
    func showDefaultView() {
        defaultView.hidden = false
    }
    
    func hideDefaultView() {
        defaultView.hidden = true
    }
}