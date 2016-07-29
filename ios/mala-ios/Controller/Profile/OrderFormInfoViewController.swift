//
//  OrderFormInfoViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/13.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class OrderFormInfoViewController: BaseViewController, OrderFormOperatingViewDelegate {

    // MARK: - Property
    /// 订单id
    var id: Int = 0
    /// 订单详情模型
    var model: OrderForm? {
        didSet {
            /// 渲染订单UI样式
            tableView.model = model
            id = model?.id ?? 0
            
            /// 渲染底部视图UI
            confirmView.orderStatus = MalaOrderStatus(rawValue: model?.status ?? "d") ?? .Canceled
            confirmView.isTeacherPublished = model?.teacherPublished
            confirmView.price = isForConfirm ? MalaCourseChoosingObject.getAmount() ?? 0 : model?.amount ?? 0
        }
    }
    /// 标识是否为确认订单状态
    var isForConfirm: Bool = false
    
    
    // MARK: - Compontents
    /// 订单详情页面
    private lazy var tableView: OrderFormTableView = {
        let tableView = OrderFormTableView(frame: CGRectZero, style: .Grouped)
        return tableView
    }()
    /// 底部操作视图
    private lazy var confirmView: OrderFormOperatingView = {
        let view = OrderFormOperatingView()
        return view
    }()
    
    
    // MARK: - Life Cycle
    override func viewDidLoad() {
        super.viewDidLoad()
                
        setupUserInterface()
        if isForConfirm {
            loadOrderOverView()
        }else {
            loadOrderFormInfo()
        }
    }
    
    override func viewDidAppear(animated: Bool) {
        super.viewDidAppear(animated)
        if isForConfirm {
            sendScreenTrack(SAOrderViewName)
        }else {
            sendScreenTrack(SAOrderInfoViewName)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    
    // MARK: - Private method
    private func setupUserInterface() {
        // Style
        self.title = isForConfirm ? "确认订单" : "订单详情"
        
        // SubViews
        view.addSubview(confirmView)
        view.addSubview(tableView)
        
        confirmView.delegate = self
        
        // Autolayout
        confirmView.snp_makeConstraints { (make) -> Void in
            make.bottom.equalTo(self.view.snp_bottom)
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
            make.height.equalTo(47)
        }
        tableView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.view.snp_top)
            make.left.equalTo(self.view.snp_left)
            make.right.equalTo(self.view.snp_right)
            make.bottom.equalTo(confirmView.snp_top)
        }
    }
    
    /// 获取订单详情信息
    private func loadOrderFormInfo() {
        
        ThemeHUD.showActivityIndicator()
        
        // 获取订单信息
        getOrderInfo(id, failureHandler: { (reason, errorMessage) -> Void in
            ThemeHUD.hideActivityIndicator()
            
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("HandlePingppBehaviour - validateOrderStatus Error \(errorMessage)")
            }
            }, completion: { [weak self] order -> Void in
                println("订单获取成功 \(order)")
                
                dispatch_async(dispatch_get_main_queue()) { [weak self] in
                    self?.model = order
                }
        })
    }
    
    ///  作为订单预览展示时，加载伪数据
    private func loadOrderOverView() {
        
        println("订单预览")
        ///  课时
        guard MalaCourseChoosingObject.classPeriod != 0 else {
            ShowTost("订单信息有误")
            return
        }
        
        // 上课时间
        let timeslots = MalaCourseChoosingObject.selectedTime.map{$0.id}
        guard timeslots.count != 0 else {
            ShowTost("订单信息有误")
            return
        }
        
        // 课时
        let hours = MalaCourseChoosingObject.classPeriod
        
        ThemeHUD.showActivityIndicator()
        
        // 请求上课时间表
        getConcreteTimeslots(MalaOrderOverView.teacher, hours: hours, timeSlots: timeslots, failureHandler: { (reason, errorMessage) in
            ThemeHUD.hideActivityIndicator()
            
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("OrderFormInfoViewController - loadOrderOverView Error \(errorMessage)")
            }
            
        }, completion: { [weak self] (timeSlots) in
            ThemeHUD.hideActivityIndicator()
            
            guard let timesSchedule = timeSlots else {
                self?.ShowTost("上课时间获取有误，请重试！")
                return
            }
            
            MalaOrderOverView.timeSlots = timesSchedule
            MalaOrderOverView.hours = MalaCourseChoosingObject.classPeriod
            MalaOrderOverView.schoolName = MalaCourseChoosingObject.school?.name
            MalaOrderOverView.status = "c"
            
            dispatch_async(dispatch_get_main_queue()) { [weak self] in
                self?.model = MalaOrderOverView
            }
        })
    }
    
    ///  取消订单
    private func cancelOrder() {
        
        println("取消订单")
        ThemeHUD.showActivityIndicator()
        
        cancelOrderWithId(id, failureHandler: { (reason, errorMessage) in
            ThemeHUD.hideActivityIndicator()
            
            defaultFailureHandler(reason, errorMessage: errorMessage)
            // 错误处理
            if let errorMessage = errorMessage {
                println("OrderFormInfoViewController - cancelOrder Error \(errorMessage)")
            }
            }, completion:{ [weak self] (result) in
                ThemeHUD.hideActivityIndicator()
                println("取消订单结果 - \(result)")
                dispatch_async(dispatch_get_main_queue(), { () -> Void in
                    if result {
                        MalaUnpaidOrderCount -= 1
                        self?.ShowTost("订单取消成功")
                        self?.confirmView.orderStatus = .Canceled
                    }else {
                        self?.ShowTost("订单取消失败")
                    }
                })
            })
    }
    
    private func createOrder() {
        
        println("创建订单")
        ThemeHUD.showActivityIndicator()
        
        ///  创建订单
        createOrderWithForm(MalaOrderObject.jsonDictionary(), failureHandler: { [weak self] (reason, errorMessage) -> Void in
            
            ThemeHUD.hideActivityIndicator()
            defaultFailureHandler(reason, errorMessage: errorMessage)
            
            // 错误处理
            if let errorMessage = errorMessage {
                println("PaymentViewController - CreateOrder Error \(errorMessage)")
            }
            
            dispatch_async(dispatch_get_main_queue(), { () -> Void in
                self?.ShowTost("创建订单失败, 请重试！")
            })
            
            }, completion: { [weak self] (order) -> Void in
                
                ThemeHUD.hideActivityIndicator()
                
                if let errorCode = order.code {
                    if errorCode == -1 {
                        dispatch_async(dispatch_get_main_queue(), { () -> Void in
                            self?.ShowTost("该老师部分时段已被占用，请重新选择上课时间")
                        })
                    }else if errorCode == -2 {
                        dispatch_async(dispatch_get_main_queue(), { () -> Void in
                            self?.ShowTost("奖学金使用信息有误，请重新选择")
                            self?.navigationController?.popViewControllerAnimated(true)
                        })
                    }
                }else {
                    ThemeHUD.hideActivityIndicator()
                    println("创建订单成功:\(order)")
                    ServiceResponseOrder = order
                    dispatch_async(dispatch_get_main_queue(), { () -> Void in
                        self?.launchPaymentController()
                    })
                }
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
    ///  立即支付
    func OrderFormPayment() {
        
        // 订单详情 － 支付
        if isForConfirm {
            createOrder()
            return
        }
        
        // 订单详情 － 支付
        if let order = self.model {
            ServiceResponseOrder = order
            launchPaymentController()
        }
    }
    
    ///  再次购买
    func OrderFormBuyAgain() {
        
        // 跳转到课程购买页
        let viewController = CourseChoosingViewController()
        if let id = model?.teacher  {
            viewController.teacherModel?.subject = model?.subjectName
            viewController.teacherId = id
            viewController.hidesBottomBarWhenPushed = true
            self.navigationController?.pushViewController(viewController, animated: true)
        }else {
            self.ShowTost("订单信息有误，请刷新后重试")
        }
    }
    
    ///  取消订单
    func OrderFormCancel() {
        
        MalaAlert.confirmOrCancel(
            title: "取消订单",
            message: "确认取消订单吗？",
            confirmTitle: "取消订单",
            cancelTitle: "暂不取消",
            inViewController: self,
            withConfirmAction: { [weak self] () -> Void in
                self?.cancelOrder()
            }, cancelAction: { () -> Void in
        })
    }
}