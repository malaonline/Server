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
            println("当前支付渠道信息： \(model?.chargeChannel)")
            // 若订单状态为[待支付]或[已关闭]，隐藏支付渠道Cell
            self.shouldHiddenPaymentChannel = (model?.status == MalaOrderStatus.Canceled.rawValue) || (model?.status == MalaOrderStatus.Penging.rawValue)
            
            // 刷新数据渲染UI
            dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                self?.reloadData()
                delay(0.5) {
                    self?.shouldHiddenTimeSlots = false
                    self?.reloadSections(NSIndexSet(index: 1), withRowAnimation: .Fade)
                }
            })
        }
    }
    // 是否隐藏时间表
    var shouldHiddenTimeSlots: Bool = true
    // 是否隐藏支付渠道Cell
    var shouldHiddenPaymentChannel: Bool = false
    
    
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
        estimatedRowHeight = 500
        separatorStyle = .None
        contentInset = UIEdgeInsets(top: -25, left: 0, bottom: 4, right: 0)
        
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
        return shouldHiddenPaymentChannel ? OrderFormCellReuseId.count-1 : OrderFormCellReuseId.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        let sectionIndex = (indexPath.section >= 2 && shouldHiddenPaymentChannel) ? indexPath.section+1 : indexPath.section
        
        let reuseCell = tableView.dequeueReusableCellWithIdentifier(OrderFormCellReuseId[sectionIndex]!, forIndexPath: indexPath)
        reuseCell.selectionStyle = .None
        
        switch indexPath.section {
        case 0:
            let cell = reuseCell as! OrderFormStatusCell
            cell.model = self.model
            return cell
            
        case 1:
            let cell = reuseCell as! OrderFormTimeScheduleCell
            cell.classPeriod = self.model?.hours ?? 0
            cell.timeSchedules = self.model?.timeSlots
            cell.shouldHiddenTimeSlots = self.shouldHiddenTimeSlots
            return cell
            
        case 2:
            // 若隐藏支付渠道Cell，则显示支付信息Cell
            if shouldHiddenPaymentChannel {
                let cell = reuseCell as! OrderFormOtherInfoCell
                cell.model = self.model
                return cell
            }else {
                let cell = reuseCell as! OrderFormPaymentChannelCell
                cell.channel = (self.model?.channel ?? .Other)
                return cell
            }

        case 3:
            let cell = reuseCell as! OrderFormOtherInfoCell
            cell.model = self.model
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