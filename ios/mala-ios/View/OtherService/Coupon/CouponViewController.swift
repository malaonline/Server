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
    var models: [CouponModel] = [] {
        didSet {
            self.tableView.reloadData()
        }
    }
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()

        configure()
        loadCoupons()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Delegate
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return MalaLayout_CardCellWidth*0.35
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
    
    // MARK: - Private Method
    private func configure() {
        tableView.backgroundColor = MalaTeacherCellBackgroundColor
        tableView.separatorStyle = .None
        
        tableView.registerClass(CouponViewCell.self, forCellReuseIdentifier: CouponViewCellReuseId)
    }
    
    private func loadCoupons() {
        ///  获取优惠券信息
        getCouponList({ (reason, errorMessage) -> Void in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("CouponViewController - VerifyCode Error \(errorMessage)")
            }
            }, completion: { [weak self] (coupons) -> Void in
                println("优惠券列表 \(coupons)")
                self?.models = coupons
        })
    }
}