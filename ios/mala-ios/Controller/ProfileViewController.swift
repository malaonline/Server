//
//  ProfileViewController.swift
//  mala-ios
//
//  Created by Elors on 12/18/15.
//  Copyright © 2015 Mala Online. All rights reserved.
//

import UIKit

private let ProfileViewTableViewCellReuseID = "ProfileViewTableViewCellReuseID"

class ProfileViewController: UITableViewController {
    
    // MARK: - Property
    /// [个人中心结构数据]
    private var model: [[ProfileElementModel]] = MalaConfig.profileData()
    
    
    // MARK: - Components
    /// [个人中心]头部视图
    private lazy var profileHeaderView: ProfileViewHeaderView = {
        let profileHeaderView = ProfileViewHeaderView(frame: CGRect(x: 0, y: 0, width: MalaScreenWidth, height: MalaLayout_ProfileHeaderViewHeight))
        profileHeaderView.name = MalaUserDefaults.studentName.value ?? "学生姓名"
        return profileHeaderView
    }()
    /// [个人中心]底部视图
    private lazy var profileFooterView: UIView = {
        let profileFooterView = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: 55))
        return profileFooterView
    }()
    /// 顶部背景图
    private lazy var headerBackground: UIImageView = {
        let image = UIImageView(image: UIImage(named: "profile_headerBackground"))
        image.contentMode = .ScaleAspectFill
        return image
    }()
    /// [退出登录] 按钮
    private lazy var logoutButton: UIButton = {
        let logoutButton = UIButton()
        
        logoutButton.layer.cornerRadius = 5
        logoutButton.layer.masksToBounds = true
        logoutButton.layer.borderColor = MalaDetailsButtonBorderColor.CGColor
        logoutButton.layer.borderWidth = MalaScreenOnePixel
        
        logoutButton.setTitle("退出登录", forState: .Normal)
        logoutButton.setTitleColor(MalaDetailsButtonBlueColor, forState: .Normal)
        logoutButton.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Normal)
         logoutButton.setBackgroundImage(UIImage.withColor(UIColor(rgbHexValue: 0xE5E5E5, alpha: 0.3)), forState: .Highlighted)
        logoutButton.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_16)
        
        logoutButton.addTarget(self, action: "logoutButtonDidTap", forControlEvents: .TouchUpInside)
        return logoutButton
    }()
    
    
    

    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        println("UserToken is \(MalaUserDefaults.userAccessToken.value)")
        println("profileID is \(MalaUserDefaults.profileID.value)")
        println("parentID is \(MalaUserDefaults.parentID.value)")
        println("studentName is \(MalaUserDefaults.studentName.value)")
        configure()
        setupUserInterface()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }

    
    // MARK: - Private Method
    private func configure() {
        tableView.registerClass(ProfileViewCell.self, forCellReuseIdentifier: ProfileViewTableViewCellReuseID)
    }
    
    private func setupUserInterface() {
        // Style
        tableView.tableHeaderView = profileHeaderView
        tableView.tableFooterView = profileFooterView
        tableView.backgroundColor = MalaProfileBackgroundColor
        
        // SubViews
        tableView.insertSubview(headerBackground, atIndex: 0)
        profileFooterView.addSubview(logoutButton)
        
        
        // Autolayout
        headerBackground.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(0)
            make.centerX.equalTo(self.tableView.snp_centerX)
            make.width.equalTo(MalaScreenWidth)
            make.height.equalTo(MalaLayout_ProfileHeaderViewHeight)
        }
        logoutButton.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(profileFooterView.snp_bottom)
            make.left.equalTo(profileFooterView.snp_left).offset(MalaLayout_FontSize_12)
            make.right.equalTo(profileFooterView.snp_right).offset(-MalaLayout_FontSize_12)
            make.height.equalTo(37)
        }
    }
    
    
    // MARK: - DataSource
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return model.count
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return model[section].count
    }
    
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(ProfileViewTableViewCellReuseID, forIndexPath: indexPath) as! ProfileViewCell
        cell.model =  model[indexPath.section][indexPath.row]
        return cell
    }
    
    
    // MARK: - TableView Delegate
    override func tableView(tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let view = UIView(frame: CGRect(x: 0, y: 0, width: 0, height: 8))
        view.backgroundColor = MalaProfileBackgroundColor
        return view
    }
    
    override func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 8
    }
    
    override func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.01
    }
    
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return 44
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let cell = tableView.cellForRowAtIndexPath(indexPath) as! ProfileViewCell
        let model = cell.model
        // 跳转到对应的ViewController
        if let type = model.controller as? UIViewController.Type {
            let viewController = type.init()
            viewController.title = model.controllerTitle
            self.navigationController?.pushViewController(viewController, animated: true)
        }
    }
    
    
    // MARK: - ScrollView Delegate
    override func scrollViewDidScroll(scrollView: UIScrollView) {
        let displacement = scrollView.contentOffset.y
        
        // 向下滑动页面时，使顶部图片跟随放大
        if displacement < 0 && headerBackground.superview != nil{
            headerBackground.snp_updateConstraints(closure: { (make) -> Void in
                make.top.equalTo(0).offset(displacement)
                // 1.1为放大速率
                make.height.equalTo(MalaLayout_ProfileHeaderViewHeight + abs(displacement)*1.1)
            })
        }
    }
    
    
    // MARK: - Event Response
    @objc private func logoutButtonDidTap() {
        MalaAlert.confirmOrCancel(
            title: "注意",
            message: "您确认要退出登录吗？",
            confirmTitle: "退出登录",
            cancelTitle: "取消",
            inViewController: self,
            withConfirmAction: { () -> Void in
                
                unregisterThirdPartyPush()
                cleanCaches()
                MalaUserDefaults.cleanAllUserDefaults()
                
                if let appDelegate = UIApplication.sharedApplication().delegate as? AppDelegate {
                    appDelegate.switchToStart()
                }
                
            }, cancelAction: { () -> Void in
        })
    }
}