//
//  OrderFormTableView.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/13.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

let OrderFormCellReuseId = [
    0: "OrderFormStatusCellReuseId",            // 订单状态及信息
    1: "OrderFormTimeScheduleCellReuseId",      // 上课时间
    2: "OrderFormPaymentChannelCellReuseId",    // 支付方式
    3: "OrderFormOtherInfoCellReuseId"          // 其他信息
]

class OrderFormTableView: UITableView, UITableViewDelegate, UITableViewDataSource {

    // MARK: - Property
    /// 订单详情模型
    var model: OrderForm? {
        didSet {
            
        }
    }
    
    
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
        delegate = self
        dataSource = self
        backgroundColor = MalaColor_EDEDED_0
        estimatedRowHeight = 400
        separatorStyle = .None
        contentInset = UIEdgeInsets(top: -40, left: 0, bottom: 4, right: 0)
        
        registerClass(OrderFormStatusCell.self, forCellReuseIdentifier: OrderFormCellReuseId[0]!)
        registerClass(OrderFormTimeScheduleCell.self, forCellReuseIdentifier: OrderFormCellReuseId[1]!)
        registerClass(OrderFormPaymentChannelCell.self, forCellReuseIdentifier: OrderFormCellReuseId[2]!)
        registerClass(OrderFormOtherInfoCell.self, forCellReuseIdentifier: OrderFormCellReuseId[3]!)
    }
    
    // MARK: - Delegate
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        
    }
    
    func tableView(tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return section == 0 ? 0 : MalaLayout_Margin_4
    }
    
    func tableView(tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return MalaLayout_Margin_4
    }
    
    
    // MARK: - DataSource
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return OrderFormCellReuseId.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let reuseCell = tableView.dequeueReusableCellWithIdentifier(OrderFormCellReuseId[indexPath.section]!, forIndexPath: indexPath)
        reuseCell.selectionStyle = .None
        
        switch indexPath.section {
        case 0:
            let cell = reuseCell as! OrderFormStatusCell
            
            return cell
            
        case 1:
            let cell = reuseCell as! OrderFormTimeScheduleCell

            return cell
            
        case 2:
            let cell = reuseCell as! OrderFormPaymentChannelCell

            return cell
            
        case 3:
            let cell = reuseCell as! OrderFormOtherInfoCell

            return cell
            
        default:
            break
        }
        
        return reuseCell
    }
    
    
    deinit {
        println("OrderFormTableView deinit")
    }
}