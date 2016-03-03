//
//  CouponViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 2/19/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let CouponViewCellReuseId = "ScholarshipTableViewCellReuseId"

class CouponViewController: UITableViewController {

    // MARK: - Property
    /// 优惠券模型数组
    var models: [CouponModel] = [] {
        didSet {
            self.tableView.reloadData()
        }
    }
    /// 当前选择项IndexPath标记
    private var currentSelectedIndexPath: NSIndexPath = NSIndexPath(forItem: 0, inSection: 0)
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        configure()
        loadCoupons()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Private Method
    private func configure() {
        title = "奖学金"
        tableView.backgroundColor = MalaTeacherCellBackgroundColor
        tableView.separatorStyle = .None
        
        // leftBarButtonItem
        let spacer = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
        spacer.width = -MalaLayout_Margin_12
        let leftBarButtonItem = UIBarButtonItem(customView:
            UIButton(
                imageName: "leftArrow_normal",
                highlightImageName: "leftArrow_press",
                target: self,
                action: "popSelf"
            )
        )
        navigationItem.leftBarButtonItems = [spacer, leftBarButtonItem]
        
        
        tableView.registerClass(CouponViewCell.self, forCellReuseIdentifier: CouponViewCellReuseId)
    }
    
    ///  获取优惠券信息
    private func loadCoupons() {
        self.models = MalaUserCoupons
    }
    
    
    // MARK: - Delegate
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return MalaLayout_CardCellWidth*0.35
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let cell = tableView.cellForRowAtIndexPath(indexPath) as? CouponViewCell
        
        // 只有未使用的才可选中
        guard cell?.model?.status == .Unused else {
            return
        }
        
        // 选中当前选择Cell，并取消其他Cell选择
        cell?.selected = true
        (tableView.cellForRowAtIndexPath(currentSelectedIndexPath)?.selected = false)
        currentSelectedIndexPath = indexPath
    }

    // MARK: - DataSource
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.models.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(CouponViewCellReuseId, forIndexPath: indexPath) as! CouponViewCell
        cell.selectionStyle = .None
        cell.model = self.models[indexPath.row]
        return cell
    }
    
    
    @objc private func popSelf() {
        self.navigationController?.popViewControllerAnimated(true)
    }
}