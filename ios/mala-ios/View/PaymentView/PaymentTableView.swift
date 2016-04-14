//
//  PaymentTableView.swift
//  mala-ios
//
//  Created by 王新宇 on 2/29/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class PaymentTableView: UITableView, UITableViewDataSource, UITableViewDelegate {
    
    // MARK: - ReuseId
    private let paymentAmountCellIdentifier = "paymentAmountCellIdentifier"
    private let paymentChannelCellIdentifier = "paymentChannelCellIdentifier"
    private let malaBaseCellIdentifier = "malaBaseCellIdentifier"
    
    
    // MARK: - Property
    private var currentSelectedIndexPath: NSIndexPath = NSIndexPath(forRow: 0, inSection: 0)
    
    
    // MARK: - Constructed
    override init(frame: CGRect, style: UITableViewStyle) {
        super.init(frame: frame, style: style)
        
        configure()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configure() {
        dataSource = self
        delegate = self
        bounces = false
        separatorStyle = .None
        
        registerClass(PaymentAmountCell.self, forCellReuseIdentifier: paymentAmountCellIdentifier)
        registerClass(PaymentChannelCell.self, forCellReuseIdentifier: paymentChannelCellIdentifier)
        registerClass(MalaBaseCell.self, forCellReuseIdentifier: malaBaseCellIdentifier)
    }
    
    
    // MARK: - DataSource
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 2
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return section == 0 ? 1 : MalaConfig.paymentChannelAmount()
    }

    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        // 模式匹配
        switch (indexPath.section, indexPath.row) {
        case (0, 0):
            // 应付金额
            let cell = (tableView.dequeueReusableCellWithIdentifier(paymentAmountCellIdentifier, forIndexPath: indexPath)) as! PaymentAmountCell
            return cell
            
        case (1, 0):
            // 支付宝
            let cell = (tableView.dequeueReusableCellWithIdentifier(paymentChannelCellIdentifier, forIndexPath: indexPath)) as! PaymentChannelCell
            cell.model = MalaPaymentChannels[0]
            cell.selected = true
            currentSelectedIndexPath = indexPath
            return cell
            
        case (1, 1):
            // 微信支付
            let cell = (tableView.dequeueReusableCellWithIdentifier(paymentChannelCellIdentifier, forIndexPath: indexPath)) as! PaymentChannelCell
            cell.model = MalaPaymentChannels[1]
            return cell
            
        default:
            let cell = (tableView.dequeueReusableCellWithIdentifier(malaBaseCellIdentifier, forIndexPath: indexPath)) as! MalaBaseCell
            return cell
        }
    }

    
    // MARK: - Delegate
    func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return indexPath.section == 0 ? 47 : 66
    }
    
    func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return section == 1 ? 33 : 8
    }
    
    func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return 0.01
    }
    
    func tableView(tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        return section == 1 ? PaymentChannelSectionHeaderView() : UIView()
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        // 所有操作结束弹栈时，取消选中项
        defer {
            tableView.deselectRowAtIndexPath(indexPath, animated: true)
        }
        
        // 当选择支付方式时
        guard indexPath.section == 1 else {
            return
        }
        
        // 切换支付方式
        tableView.cellForRowAtIndexPath(currentSelectedIndexPath)?.selected = false
        currentSelectedIndexPath = indexPath
        let cell = tableView.cellForRowAtIndexPath(indexPath) as? PaymentChannelCell
        cell?.selected = true
        
        // 更改订单模型 - 支付方式
        MalaOrderObject.channel = (cell?.model?.channel)!
    }
}