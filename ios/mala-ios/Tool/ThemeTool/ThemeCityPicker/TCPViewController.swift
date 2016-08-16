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
    var models: [BaseObjectModel] = [] {
        didSet {
            tableView.models = models
        }
    }
    
    
    // MARK: - Components
    // 城市列表
    private lazy var tableView: CityTableView = {
        let tableView = CityTableView(frame: CGRectZero, style: .Grouped)        
        return tableView
    }()
    
    
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
        view.backgroundColor = UIColor.whiteColor()
        let leftBarButtonItem = UIBarButtonItem(customView:UIButton(imageName: "close", target: self, action: #selector(TCPViewController.closeButtonDidClick)))
        navigationItem.leftBarButtonItem = leftBarButtonItem
        UIApplication.sharedApplication().statusBarStyle = UIStatusBarStyle.Default
        navigationController?.navigationBar.shadowImage = UIImage()
        
        // SubViews
        self.view.addSubview(tableView)
        
        // AutoLayout
        tableView.snp_makeConstraints { (make) in
            make.top.equalTo(self.view.snp_top)
            make.bottom.equalTo(self.view.snp_bottom)
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
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
            self?.models = cities
            println("城市列表 - \(cities)")
        })
    }
    
    
    @objc private func closeButtonDidClick() {
        dismissViewControllerAnimated(true, completion: nil)
    }
}