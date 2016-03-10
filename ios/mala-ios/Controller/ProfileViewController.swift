//
//  ProfileViewController.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

class ProfileViewController: UITableViewController {
    
    
    // MARK: - Components
    /// [个人中心]头部视图
    private lazy var profileHeaderView: ProfileViewHeaderView = {
        let profileHeaderView = ProfileViewHeaderView(frame: CGRect(x: 0, y: 0, width: MalaScreenWidth, height: MalaLayout_ProfileHeaderViewHeight))
        profileHeaderView.name = "----"
        return profileHeaderView
    }()
    /// 顶部背景图
    private lazy var headerBackground: UIImageView = {
        let image = UIImageView(image: UIImage(named: "profile_headerBackground"))
        image.contentMode = .ScaleAspectFill
        return image
    }()
    

    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        println("UserToken is \(MalaUserDefaults.userAccessToken.value)")
        setupUserInterface()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        tableView.tableHeaderView = profileHeaderView
        
        // SubViews
        tableView.insertSubview(headerBackground, atIndex: 0)
        
        
        // Autolayout
        headerBackground.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(0).offset(-MalaScreenNaviHeight)
            make.centerX.equalTo(self.tableView.snp_centerX)
            make.width.equalTo(MalaScreenWidth)
            make.height.equalTo(MalaLayout_DetailHeaderLayerHeight)
        }
    }
    
    
    // MARK: - DataSource
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 0
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 0
    }
    
    
    // MARK: - ScrollView Delegate
    override func scrollViewDidScroll(scrollView: UIScrollView) {
        let displacement = scrollView.contentOffset.y
        
        // 向下滑动页面时，使顶部图片跟随放大
        if displacement < -MalaScreenNaviHeight {
            headerBackground.snp_updateConstraints(closure: { (make) -> Void in
                make.top.equalTo(0).offset(displacement)
                // 1.1为放大速率
                make.height.equalTo(MalaLayout_ProfileHeaderViewHeight + abs(displacement+MalaScreenNaviHeight)*1.1)
            })
        }
    }
}
