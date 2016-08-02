//
//  FavoriteViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/8/2.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class FavoriteViewController: BaseViewController {

    // MARK: - Components
    /// 老师信息tableView
    private lazy var tableView: TeacherTableView = {
        let tableView = TeacherTableView(frame: self.view.frame, style: .Plain)
        tableView.controller = self
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
    

    // MARK: - Private Method
    private func setupUserInterface() {
        // Style 
        view.backgroundColor = UIColor.whiteColor()
        
        // SubViews
        view.addSubview(tableView)
        
        // AutoLayout
        tableView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.view.snp_top)
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
            make.bottom.equalTo(self.view.snp_bottom)
        }
    }
}
