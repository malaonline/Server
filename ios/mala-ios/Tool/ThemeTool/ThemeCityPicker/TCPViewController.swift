//
//  TCPViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/8/16.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class TCPViewController: UIViewController {

    // MARK: - Property
    // 城市数据模型
    var model: [BaseObjectModel] = [] {
        didSet {
            
        }
    }
    
    
    // MARK: - Components
    // 城市列表
    var tableView: CityTableView {
        let view = CityTableView()
        return view
    }
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        setupUserInterface()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        loadCitylist()
    }
    
    
    // MARK: - Private
    private func setupUserInterface() {
        // Style
        title = "选择城市"
        view.backgroundColor = MalaColor_EDEDED_0
        let leftBarButtonItem = UIBarButtonItem(customView:UIButton(imageName: "close", target: self, action: #selector(TCPViewController.closeButtonDidClick)))
        navigationItem.leftBarButtonItem = leftBarButtonItem
        UIApplication.sharedApplication().statusBarStyle = UIStatusBarStyle.Default
        
        // SubViews
        view.addSubview(tableView)
        
        
        // AutoLayout
        tableView.snp_makeConstraints { (make) in
            make.width.equalTo(view)
            make.height.equalTo(view)
            make.center.equalTo(view)
        }
    }
    
    // 获取城市列表
    private func loadCitylist() {
        
        loadRegions({ (reason, errorMessage) in
            ThemeHUD.hideActivityIndicator()
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("TCPViewController - loadCitylist Error \(errorMessage)")
            }
        }, completion:{ [weak self] (cities) in
            self?.model = cities
            println("城市列表 - \(cities)")
        })
    }
    
    
    @objc private func closeButtonDidClick() {
        dismissViewControllerAnimated(true, completion: nil)
    }
}