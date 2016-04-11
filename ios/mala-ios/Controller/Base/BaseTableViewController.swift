//
//  BaseTableViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/4/11.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class BaseTableViewController: UITableViewController {

    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        configure()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Private
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
}
