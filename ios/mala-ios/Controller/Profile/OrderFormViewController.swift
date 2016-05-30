//
//  OrderFormViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/6.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

private let OrderFormViewCellReuseId = "OrderFormViewCellReuseId"
private let OrderFormViewLoadmoreCellReusedId = "OrderFormViewLoadmoreCellReusedId"

class OrderFormViewController: BaseTableViewController {
    
    private enum Section: Int {
        case Teacher
        case LoadMore
    }
    
    // MARK: - Property
    /// 优惠券模型数组
    var models: [OrderForm] = [] {
        didSet {
            dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                self?.tableView.reloadData()
            })
        }
    }
    /// 当前选择项IndexPath标记
    /// 缺省值为不存在的indexPath，有效的初始值将会在CellForRow方法中设置
    private var currentSelectedIndexPath: NSIndexPath = NSIndexPath(forItem: 0, inSection: 1)
    /// 是否仅用于展示（例如[个人中心]）
    var justShow: Bool = true
    /// 是否正在拉取数据
    var isFetching: Bool = false
    /// 当前显示页数
    var currentPageIndex = 1
    /// 所有老师数据总量
    var allOrderFormCount = 0
    
    
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
        setupNotification()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override func viewWillAppear(animated: Bool) {
        super.viewWillAppear(animated)
        loadOrderForm()
    }
    
    
    // MARK: - Private Method
    private func configure() {
        title = "我的订单"
        
        tableView.backgroundColor = MalaColor_EDEDED_0
        tableView.separatorStyle = .None
        refreshControl = refresher
        
        tableView.registerClass(OrderFormViewCell.self, forCellReuseIdentifier: OrderFormViewCellReuseId)
        tableView.registerClass(ThemeReloadView.self, forCellReuseIdentifier: OrderFormViewLoadmoreCellReusedId)
    }
    
    ///  获取用户订单列表
    @objc private func loadOrderForm(page: Int = 1, isLoadMore: Bool = false, finish: (()->())? = nil) {
        
        // 屏蔽[正在刷新]时的操作
        guard isFetching == false else {
            return
        }
        isFetching = true
        refreshControl?.beginRefreshing()
        
        if isLoadMore {
            currentPageIndex += 1
        }else {
            currentPageIndex = 1
        }
        
        ///  获取用户订单列表
        getOrderList(currentPageIndex, failureHandler: { [weak self] (reason, errorMessage) in
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("OrderFormViewController - loadOrderForm Error \(errorMessage)")
            }
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                self?.refreshControl?.endRefreshing()
                self?.isFetching = false
            })
        }, completion: { [weak self] (orderList, count) in
            
            /// 记录数据量
            if count != 0 {
                self?.allOrderFormCount = count
            }
            
            ///  加载更多
            if isLoadMore {
                for order in orderList {
                    self?.models.append(order)
                }
            ///  如果不是加载更多，则刷新数据
            }else {
                self?.models = orderList
            }
            
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                finish?()
                self?.refreshControl?.endRefreshing()
                self?.isFetching = false
            })
        })
    }
    
    private func setupNotification() {
        
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_PushToPayment,
            object: nil,
            queue: nil
        ) { [weak self] (notification) -> Void in
            // 支付页面
            if let order = notification.object as? OrderForm {
                ServiceResponseOrder = order
                self?.launchPaymentController()
            }
            
        }
        
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_PushTeacherDetailView,
            object: nil,
            queue: nil
        ) { [weak self] (notification) -> Void in
            // 跳转到课程购买页
            let viewController = CourseChoosingViewController()
            if let id = notification.object as? Int {
                viewController.teacherId = id
                viewController.hidesBottomBarWhenPushed = true
                self?.navigationController?.pushViewController(viewController, animated: true)
            }else {
                self?.ShowTost("订单信息有误，请刷新后重试")
            }
        }
        
        NSNotificationCenter.defaultCenter().addObserverForName(
            MalaNotification_CancelOrderForm,
            object: nil,
            queue: nil
        ) { [weak self] (notification) -> Void in
            // 取消订单
            if let id = notification.object as? Int {
                
                MalaAlert.confirmOrCancel(
                    title: "取消订单",
                    message: "确认取消订单吗？",
                    confirmTitle: "取消订单",
                    cancelTitle: "暂不取消",
                    inViewController: self,
                    withConfirmAction: { [weak self] () -> Void in
                        self?.cancelOrder(id)
                    }, cancelAction: { () -> Void in
                })
                
            }else {
                self?.ShowTost("订单信息有误，请刷新后重试")
            }
        }
    }
    
    private func cancelOrder(orderId: Int) {
        
        println("取消订单")
        ThemeHUD.showActivityIndicator()
        
        cancelOrderWithId(orderId, failureHandler: { (reason, errorMessage) in
            ThemeHUD.hideActivityIndicator()
            
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("OrderFormViewController - cancelOrder Error \(errorMessage)")
            }
            }, completion:{ [weak self] (result) in
                ThemeHUD.hideActivityIndicator()
                println("取消订单结果 - \(result)")
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    if result {
                        MalaUnpaidOrderCount -= 1
                        self?.ShowTost("订单取消成功")
                        self?.loadOrderForm()
                    }else {
                        self?.ShowTost("订单取消失败")
                    }
                })
            })
    }
    
    private func launchPaymentController() {
        
        // 跳转到支付页面
        let viewController = PaymentViewController()
        viewController.popAction = {
            MalaIsPaymentIn = false
        }
        
        self.navigationController?.pushViewController(viewController, animated: true)
    }
    
    
    // MARK: - Delegate
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return MalaLayout_CardCellWidth*0.6
    }
    
    override func tableView(tableView: UITableView, willDisplayCell cell: UITableViewCell, forRowAtIndexPath indexPath: NSIndexPath) {
        
        switch indexPath.section {
            
        case Section.Teacher.rawValue:
            break
            
        case Section.LoadMore.rawValue:
            if let cell = cell as? ThemeReloadView {
                println("load more orderForm")
                
                if !cell.activityIndicator.isAnimating() {
                    cell.activityIndicator.startAnimating()
                }
                
                loadOrderForm(isLoadMore: true, finish: { 
                    cell.activityIndicator.stopAnimating()
                })
            }
            
        default:
            break
        }
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        let viewController = OrderFormInfoViewController()
        viewController.id = models[indexPath.row].id
        self.navigationController?.pushViewController(viewController, animated: true)
    }
    
    
    // MARK: - DataSource
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 2
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        switch section {
            
        case Section.Teacher.rawValue:
            return models.count ?? 0
            
        case Section.LoadMore.rawValue:
            if allOrderFormCount == models.count {
                return 0
            }else {
                return models.isEmpty ? 0 : 1
            }
            
        default:
            return 0
        }
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        
        switch indexPath.section {
            
        case Section.Teacher.rawValue:
            let cell = tableView.dequeueReusableCellWithIdentifier(OrderFormViewCellReuseId, forIndexPath: indexPath) as! OrderFormViewCell
            cell.selectionStyle = .None
            cell.model = self.models[indexPath.row]
            return cell
            
        case Section.LoadMore.rawValue:
            let cell = tableView.dequeueReusableCellWithIdentifier(OrderFormViewLoadmoreCellReusedId, forIndexPath: indexPath) as! ThemeReloadView
            return cell
            
        default:
            return UITableViewCell()
        }
    }
    
    
    deinit {
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_PushToPayment, object: nil)
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_PushTeacherDetailView, object: nil)
        NSNotificationCenter.defaultCenter().removeObserver(self, name: MalaNotification_CancelOrderForm, object: nil)
    }
}