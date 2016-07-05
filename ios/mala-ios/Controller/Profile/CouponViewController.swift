//
//  CouponViewController.swift
//  mala-ios
//
//  Created by 王新宇 on 2/19/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let CouponViewCellReuseId = "CouponViewCellReuseId"

class CouponViewController: BaseTableViewController {

    // MARK: - Property
    /// 优惠券模型数组
    var models: [CouponModel] = MalaUserCoupons {
        didSet {
            dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                if self?.models.count == 0 {
                    self?.showDefaultView()
                }else {
                    self?.hideDefaultView()
                    self?.tableView.reloadData()
                }
            })
        }
    }
    /// 当前选择项IndexPath标记
    /// 缺省值为不存在的indexPath，有效的初始值将会在CellForRow方法中设置
    private var currentSelectedIndexPath: NSIndexPath = NSIndexPath(forItem: 0, inSection: 1)
    /// 是否仅用于展示（例如[个人中心]）
    var justShow: Bool = true
    /// 是否只获取可用的奖学金（[选课页面]）
    var onlyValid: Bool = false
    /// 是否正在拉取数据
    var isFetching: Bool = false
    
    // MARK: - Components
    /// 下拉刷新视图
    private lazy var refresher: UIRefreshControl = {
        let refresher = UIRefreshControl()
        refresher.addTarget(self, action: #selector(CouponViewController.loadCoupons), forControlEvents: .ValueChanged)
        return refresher
    }()
    /// 保存按钮
    private lazy var rulesButton: UIButton = {
        let button = UIButton(
            title: "使用规则",
            titleColor: MalaColor_82B4D9_0,
            target: self,
            action: #selector(CouponViewController.showCouponRules)
        )
        button.setTitleColor(MalaColor_E0E0E0_95, forState: .Disabled)
        return button
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
        refreshControl = refresher
        tableView.registerClass(CouponViewCell.self, forCellReuseIdentifier: CouponViewCellReuseId)
        defaultView.imageName = "no_coupons"
        defaultView.text = "您当前没有奖学金哦！"
        
        // rightBarButtonItem
        let spacerRight = UIBarButtonItem(barButtonSystemItem: .FixedSpace, target: nil, action: nil)
        spacerRight.width = -5
        let rightBarButtonItem = UIBarButtonItem(customView: rulesButton)
        navigationItem.rightBarButtonItems = [rightBarButtonItem, spacerRight]
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
        getCouponList(onlyValid, failureHandler: { [weak self] (reason, errorMessage) -> Void in
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
            if self?.justShow == true {
                MalaUserCoupons = coupons
            }else {
                MalaUserCoupons = parseCouponlist(coupons)
            }
            self?.models = MalaUserCoupons
            self?.refreshControl?.endRefreshing()
            self?.isFetching = false
        })

    }
    
    
    // MARK: - Delegate
    override func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return MalaLayout_CardCellWidth*0.273
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        ///  若只用于显示，直接return
        if justShow {
            return
        }
        
        let cell = tableView.cellForRowAtIndexPath(indexPath) as? CouponViewCell

        // 不可选择被冻结的奖学金
        guard cell?.disabled == false else {
            return
        }
        
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
        if models[indexPath.row].id == MalaCourseChoosingObject.coupon?.id && !justShow {
            cell.showSelectedIndicator = true
            currentSelectedIndexPath = indexPath
        }
        return cell
    }
    
    
    // MARK: - Events Response
    @objc private func showCouponRules() {
        CouponRulesPopupWindow(title: "奖学金使用规则", desc: MalaConfig.couponRulesDescriptionString()).show()
    }
}