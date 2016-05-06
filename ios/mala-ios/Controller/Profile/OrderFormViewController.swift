//
//  OrderFormViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/6.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

private let OrderFormViewCellReuseId = "OrderFormViewCellReuseId"

class OrderFormViewController: BaseTableViewController {
    
    // MARK: - Property
    /// 优惠券模型数组
    var models: [OrderForm] = TestFactory.testOrderForms() {
        didSet {
            models = TestFactory.testOrderForms() //TODO: 删除测试数据
            self.tableView.reloadData()
        }
    }
    /// 当前选择项IndexPath标记
    /// 缺省值为不存在的indexPath，有效的初始值将会在CellForRow方法中设置
    private var currentSelectedIndexPath: NSIndexPath = NSIndexPath(forItem: 0, inSection: 1)
    /// 是否仅用于展示（例如[个人中心]）
    var justShow: Bool = true
    /// 是否正在拉取数据
    var isFetching: Bool = false
    
    // MARK: - Components
    /// 下拉刷新视图
    private lazy var refresher: UIRefreshControl = {
        let refresher = UIRefreshControl()
        refresher.addTarget(self, action: #selector(OrderFormViewController.loadOrderForm), forControlEvents: .ValueChanged)
        return refresher
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
        
        configure()
//        loadOrderForm()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Private Method
    private func configure() {
        tableView.backgroundColor = MalaColor_EDEDED_0
        tableView.separatorStyle = .None
        refreshControl = refresher
        tableView.registerClass(OrderFormViewCell.self, forCellReuseIdentifier: OrderFormViewCellReuseId)
    }
    
    ///  获取优惠券信息
    @objc private func loadOrderForm() {
        
        // 屏蔽[正在刷新]时的操作
        guard isFetching == false else {
            return
        }
        isFetching = true
        
        refreshControl?.beginRefreshing()
        
        ///  获取优惠券信息
        getCouponList({ [weak self] (reason, errorMessage) -> Void in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("OrderFormViewController - loadOrderForm Error \(errorMessage)")
            }
            // 显示缺省值
            // self?.models = MalaUserCoupons
            self?.refreshControl?.endRefreshing()
            self?.isFetching = false
            }, completion: { [weak self] (coupons) -> Void in
                println("优惠券列表 \(coupons)")
                MalaUserCoupons = coupons
                // self?.models = MalaUserCoupons
                self?.refreshControl?.endRefreshing()
                self?.isFetching = false
            })
        
    }
    
    
    // MARK: - Delegate
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return MalaLayout_CardCellWidth*0.6
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        
    }
    
    
    // MARK: - DataSource
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.models.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(OrderFormViewCellReuseId, forIndexPath: indexPath) as! OrderFormViewCell
        cell.selectionStyle = .None
        cell.model = self.models[indexPath.row]
        return cell
    }
}