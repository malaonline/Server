//
//  FavoriteViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/8/2.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class FavoriteViewController: BaseViewController {

    // MARK: - Property
    var model: [TeacherModel] = [] {
        didSet {
            tableView.teachers = model
        }
    }
    
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
        loadFavoriteTeachers()
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
    
    private func loadFavoriteTeachers() {
        println("获取收藏列表")
        ///  获取学生课程信息
        getFavoriteTeachers({ [weak self] (reason, errorMessage) in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("FavoriteViewController - loadFavoriteTeachers Error \(errorMessage)")
            }
            // 显示缺省值
            self?.model = []
        }, completion: { [weak self] (teachers) in
            println("收藏列表 － \(teachers)")
            self?.model = teachers
        })
    }
    
}