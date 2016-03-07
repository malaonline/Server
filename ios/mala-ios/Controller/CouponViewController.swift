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
    /// 缺省值为不存在的indexPath，有效的初始值将会在CellForRow方法中设置
    private var currentSelectedIndexPath: NSIndexPath = NSIndexPath(forItem: 0, inSection: 1)
    
    
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
        if indexPath == currentSelectedIndexPath {
            // 取消选中项
            cell?.showSelectedIndicator = false
            currentSelectedIndexPath = NSIndexPath(forItem: 0, inSection: 1)
            MalaCourseChoosingObject.coupon = CouponModel(id: 0, name: "不使用奖学金", amount: 0, expired_at: 0, used: false)
        }else {
            (tableView.cellForRowAtIndexPath(currentSelectedIndexPath) as? CouponViewCell)?.showSelectedIndicator = false
            cell?.showSelectedIndicator = true
            currentSelectedIndexPath = indexPath
            MalaCourseChoosingObject.coupon = cell?.model
        }
    }

    // MARK: - DataSource
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.models.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(CouponViewCellReuseId, forIndexPath: indexPath) as! CouponViewCell
        cell.selectionStyle = .None
        cell.model = self.models[indexPath.row]
        // 如果是默认选中的优惠券，则设置选中样式
        if models[indexPath.row].id == MalaCourseChoosingObject.coupon?.id {
            cell.showSelectedIndicator = true
            currentSelectedIndexPath = indexPath
        }
        return cell
    }
    
    
    @objc private func popSelf() {
        self.navigationController?.popViewControllerAnimated(true)
    }
}