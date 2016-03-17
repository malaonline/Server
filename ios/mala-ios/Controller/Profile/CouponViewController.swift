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
    var models: [CouponModel] = MalaUserCoupons {
        didSet {
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
        refresher.addTarget(self, action: "loadCoupons", forControlEvents: .ValueChanged)
        return refresher
    }()
    
    
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
        tableView.backgroundColor = MalaColor_EDEDED_0
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
        refreshControl = refresher
        
        tableView.registerClass(CouponViewCell.self, forCellReuseIdentifier: CouponViewCellReuseId)
    }
    
    ///  获取优惠券信息
    @objc private func loadCoupons() {
        
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
                println("CouponViewController - loadCoupons Error \(errorMessage)")
            }
            // 显示缺省值
            self?.models = MalaUserCoupons
            self?.refreshControl?.endRefreshing()
            self?.isFetching = false
        }, completion: { [weak self] (coupons) -> Void in
                println("优惠券列表 \(coupons)")
                MalaUserCoupons = coupons
                self?.models = MalaUserCoupons
                self?.refreshControl?.endRefreshing()
                self?.isFetching = false
        })

    }
    
    
    // MARK: - Delegate
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return MalaLayout_CardCellWidth*0.35
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        ///  若只用于显示，直接return
        if justShow {
            return
        }
        
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