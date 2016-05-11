//
//  OrderFormViewCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/6.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class OrderFormViewCell: UITableViewCell {
    
    // MARK: - Property
    /// 订单模型
    var model: OrderForm? {
        didSet {
            // 加载订单数据
            orderIdString.text = model?.order_id
            teacherNameString.text = model?.teacherName
            subjectString.text = (model?.gradeName ?? "") + " " + (model?.subjectName ?? "")
            schoolString.text = model?.schoolName
            amountString.text = model?.amount.moneyCNY
            
            // 老师头像
            if let url = NSURL(string: (model?.avatarURL ?? "")) {
                avatarView.kf_setImageWithURL(url, placeholderImage: UIImage(named: "profileAvatar_placeholder"))
            }
            
            // 设置订单状态
            if let status = model?.status, orderStatus = MalaOrderStatus(rawValue: status) {
                self.orderStatus = orderStatus
            }
        }
    }
    /// 订单状态
    private var orderStatus: MalaOrderStatus = .Canceled {
        didSet {
            dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                self?.changeDisplayMode()
            })
        }
    }
    
    
    // MARK: - Components
    /// 顶部分隔视图
    private lazy var separatorView: UIView = {
        let view = UIView()
        return view
    }()
    /// 父布局容器
    private lazy var content: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "orderForm_background"))
        imageView.userInteractionEnabled = true
        return imageView
    }()
    /// 顶部订单编号布局容器
    private lazy var topLayoutView: UIView = {
        let view = UIView()
        view.backgroundColor = MalaColor_B1D0E8_0
        return view
    }()
    /// "订单编号"文字
    private lazy var orderIdLabel: UILabel = {
        let label = UILabel()
        label.text = "订单编号："
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = UIColor.whiteColor()
        return label
    }()
    /// 订单编号
    private lazy var orderIdString: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = UIColor.whiteColor()
        return label
    }()
    /// 中部订单信息布局容器
    private lazy var middleLayoutView: UIView = {
        let view = UIView()
        return view
    }()
    /// "老师姓名"文字
    private lazy var teacherNameLabel: UILabel = {
        let label = UILabel()
        label.text = "教师姓名："
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = MalaColor_636363_0
        return label
    }()
    /// 老师姓名
    private lazy var teacherNameString: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = MalaColor_939393_0
        return label
    }()
    /// "课程名称"文字
    private lazy var subjectLabel: UILabel = {
        let label = UILabel()
        label.text = "课程名称："
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = MalaColor_636363_0
        return label
    }()
    /// 课程名称
    private lazy var subjectString: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = MalaColor_939393_0
        return label
    }()
    /// "上课地点"文字
    private lazy var schoolLabel: UILabel = {
        let label = UILabel()
        label.text = "上课地点："
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = MalaColor_636363_0
        return label
    }()
    /// 课程名称
    private lazy var schoolString: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_11)
        label.textColor = MalaColor_939393_0
        return label
    }()
    /// 订单状态
    private lazy var statusString: UILabel = {
        let label = UILabel()
        label.text = "订单状态"
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        label.textColor = MalaColor_939393_0
        return label
    }()
    /// 老师头像
    private lazy var avatarView: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "profileAvatar_placeholder"))
        imageView.contentMode = .ScaleAspectFill
        imageView.layer.cornerRadius = 55/2
        imageView.layer.masksToBounds = true
        return imageView
    }()
    /// 中部分割线
    private lazy var separatorLine: UIView = {
        let view = UIView.line(MalaColor_DADADA_0)
        return view
    }()
    
    /// 底部价格及操作布局容器
    private lazy var bottomLayoutView: UIView = {
        let view = UIView()
        return view
    }()
    /// "共计"文字
    private lazy var amountLabel: UILabel = {
        let label = UILabel()
        label.text = "共计："
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        label.textColor = MalaColor_636363_0
        return label
    }()
    /// 共计金额
    private lazy var amountString: UILabel = {
        let label = UILabel()
        label.font = UIFont.systemFontOfSize(MalaLayout_FontSize_16)
        label.textColor = MalaColor_333333_0
        return label
    }()
    /// 确定按钮（确认支付、再次购买、重新购买）
    private lazy var confirmButton: UIButton = {
        let button = UIButton()
        
        button.layer.borderColor = MalaColor_E26254_0.CGColor
        button.layer.borderWidth = MalaScreenOnePixel
        button.layer.cornerRadius = 3
        button.layer.masksToBounds = true
        
        button.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        button.setTitle("再次购买", forState: .Normal)
        button.setTitleColor(MalaColor_E26254_0, forState: .Normal)
        button.addTarget(self, action: #selector(OrderFormViewCell.buyAgain), forControlEvents: .TouchUpInside)
        return button
    }()
    /// 取消按钮（取消订单）
    private lazy var cancelButton: UIButton = {
        let button = UIButton()
        
        button.layer.borderColor = MalaColor_939393_0.CGColor
        button.layer.borderWidth = MalaScreenOnePixel
        button.layer.cornerRadius = 3
        button.layer.masksToBounds = true
        
        button.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        button.setTitle("取消订单", forState: .Normal)
        button.setTitleColor(MalaColor_939393_0, forState: .Normal)
        button.addTarget(self, action: #selector(OrderFormViewCell.cancelOrderForm), forControlEvents: .TouchUpInside)
        return button
    }()
    
    
    // MARK: - Constructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        contentView.backgroundColor = MalaColor_EDEDED_0
        
        // SubViews
        contentView.addSubview(separatorView)
        contentView.addSubview(content)
        
        content.addSubview(topLayoutView)
        topLayoutView.addSubview(orderIdLabel)
        topLayoutView.addSubview(orderIdString)
        
        content.addSubview(middleLayoutView)
        middleLayoutView.addSubview(teacherNameLabel)
        middleLayoutView.addSubview(teacherNameString)
        middleLayoutView.addSubview(subjectLabel)
        middleLayoutView.addSubview(subjectString)
        middleLayoutView.addSubview(schoolLabel)
        middleLayoutView.addSubview(schoolString)
        middleLayoutView.addSubview(separatorLine)
        middleLayoutView.addSubview(statusString)
        middleLayoutView.addSubview(avatarView)
        
        content.addSubview(bottomLayoutView)
        bottomLayoutView.addSubview(amountLabel)
        bottomLayoutView.addSubview(amountString)
        bottomLayoutView.addSubview(confirmButton)
        bottomLayoutView.addSubview(cancelButton)
        
        
        // Autolayout
        separatorView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_6)
            make.bottom.equalTo(content.snp_top)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_6)
            make.height.equalTo(MalaLayout_Margin_6)
        }
        content.snp_makeConstraints { (make) in
            make.top.equalTo(separatorView.snp_bottom)
            make.left.equalTo(contentView.snp_left).offset(MalaLayout_Margin_6)
            make.bottom.equalTo(contentView.snp_bottom)
            make.right.equalTo(contentView.snp_right).offset(-MalaLayout_Margin_6)
        }
        
        topLayoutView.snp_makeConstraints { (make) in
            make.top.equalTo(content.snp_top)
            make.left.equalTo(content.snp_left).offset(MalaScreenOnePixel)
            make.right.equalTo(content.snp_right).offset(-MalaScreenOnePixel)
            make.height.equalTo(content.snp_height).multipliedBy(0.15)
        }
        orderIdLabel.snp_makeConstraints { (make) in
            make.height.equalTo(MalaLayout_FontSize_11)
            make.centerY.equalTo(topLayoutView.snp_centerY)
            make.left.equalTo(topLayoutView.snp_left).offset(MalaLayout_Margin_12)
        }
        orderIdString.snp_makeConstraints { (make) in
            make.height.equalTo(MalaLayout_FontSize_11)
            make.centerY.equalTo(orderIdLabel.snp_centerY)
            make.left.equalTo(orderIdLabel.snp_right)
        }
        
        middleLayoutView.snp_makeConstraints { (make) in
            make.top.equalTo(topLayoutView.snp_bottom)
            make.left.equalTo(content.snp_left).offset(MalaLayout_Margin_12)
            make.right.equalTo(content.snp_right).offset(-MalaLayout_Margin_12)
            make.height.equalTo(content.snp_height).multipliedBy(0.55)
        }
        teacherNameLabel.snp_makeConstraints { (make) in
            make.top.equalTo(middleLayoutView.snp_top).offset(MalaLayout_Margin_14)
            make.left.equalTo(middleLayoutView.snp_left)
            make.height.equalTo(MalaLayout_FontSize_11)
        }
        teacherNameString.snp_makeConstraints { (make) in
            make.top.equalTo(teacherNameLabel.snp_top)
            make.left.equalTo(teacherNameLabel.snp_right)
            make.height.equalTo(MalaLayout_FontSize_11)
        }
        subjectLabel.snp_makeConstraints { (make) in
            make.top.equalTo(teacherNameLabel.snp_bottom).offset(MalaLayout_Margin_14)
            make.left.equalTo(middleLayoutView.snp_left)
            make.height.equalTo(MalaLayout_FontSize_11)
        }
        subjectString.snp_makeConstraints { (make) in
            make.top.equalTo(subjectLabel.snp_top)
            make.left.equalTo(subjectLabel.snp_right)
            make.height.equalTo(MalaLayout_FontSize_11)
        }
        schoolLabel.snp_makeConstraints { (make) in
            make.top.equalTo(subjectLabel.snp_bottom).offset(MalaLayout_Margin_14)
            make.left.equalTo(middleLayoutView.snp_left)
            make.height.equalTo(MalaLayout_FontSize_11)
        }
        schoolString.snp_makeConstraints { (make) in
            make.top.equalTo(schoolLabel.snp_top)
            make.left.equalTo(schoolLabel.snp_right)
            make.height.equalTo(MalaLayout_FontSize_11)
        }
        statusString.snp_makeConstraints { (make) in
            make.centerX.equalTo(confirmButton.snp_centerX)
            make.top.equalTo(middleLayoutView.snp_top).offset(MalaLayout_Margin_14)
            make.height.equalTo(MalaLayout_FontSize_12)
        }
        avatarView.snp_makeConstraints { (make) in
            make.centerX.equalTo(statusString.snp_centerX)
            make.bottom.equalTo(middleLayoutView.snp_bottom).offset(-MalaLayout_Margin_14)
            make.height.equalTo(55)
            make.width.equalTo(55)
        }
        separatorLine.snp_makeConstraints { (make) in
            make.left.equalTo(middleLayoutView.snp_left).offset(-MalaLayout_Margin_3)
            make.right.equalTo(middleLayoutView.snp_right).offset(MalaLayout_Margin_3)
            make.bottom.equalTo(middleLayoutView.snp_bottom)
            make.height.equalTo(MalaScreenOnePixel)
        }
        
        bottomLayoutView.snp_makeConstraints { (make) in
            make.top.equalTo(middleLayoutView.snp_bottom)
            make.left.equalTo(content.snp_left).offset(MalaLayout_Margin_12)
            make.right.equalTo(content.snp_right).offset(-MalaLayout_Margin_12)
            make.height.equalTo(content.snp_height).multipliedBy(0.24)
        }
        amountLabel.snp_makeConstraints { (make) in
            make.centerY.equalTo(bottomLayoutView.snp_centerY)
            make.left.equalTo(bottomLayoutView.snp_left)
            make.height.equalTo(MalaLayout_FontSize_12)
        }
        amountString.snp_makeConstraints { (make) in
            make.centerY.equalTo(bottomLayoutView.snp_centerY)
            make.left.equalTo(amountLabel.snp_right)
            make.height.equalTo(MalaLayout_FontSize_16)
        }
        confirmButton.snp_makeConstraints { (make) in
            make.width.equalTo(content.snp_width).multipliedBy(0.23)
            make.height.equalTo(24)
            make.centerY.equalTo(bottomLayoutView.snp_centerY)
            make.right.equalTo(bottomLayoutView.snp_right)
        }
        cancelButton.snp_makeConstraints { (make) in
            make.width.equalTo(content.snp_width).multipliedBy(0.23)
            make.height.equalTo(24)
            make.centerY.equalTo(bottomLayoutView.snp_centerY)
            make.right.equalTo(confirmButton.snp_left).offset(-MalaLayout_Margin_14)
        }
    }
    
    /// 根据当前订单状态，渲染对应UI样式
    private func changeDisplayMode() {
        
        println("orderStatus : \(orderStatus)")
        
        // 解除绑定事件
        cancelButton.removeTarget(self, action: #selector(OrderFormViewCell.cancelOrderForm), forControlEvents: .TouchUpInside)
        confirmButton.removeTarget(self, action: #selector(OrderFormViewCell.pay), forControlEvents: .TouchUpInside)
        confirmButton.removeTarget(self, action: #selector(OrderFormViewCell.buyAgain), forControlEvents: .TouchUpInside)
        
        // 渲染UI样式
        switch orderStatus {
        case .Penging:
            println("orderStatus : Penging")
            // 待付款
            topLayoutView.backgroundColor = MalaColor_8FBCDD_0
            statusString.text = "订单待支付"
            statusString.textColor = MalaColor_E26254_0
            
            cancelButton.hidden = false
            confirmButton.hidden = false
            
            confirmButton.setTitle("立即支付", forState: .Normal)
            confirmButton.setBackgroundImage(UIImage.withColor(MalaColor_E26254_0), forState: .Normal)
            confirmButton.setTitleColor(UIColor.whiteColor(), forState: .Normal)
            
            cancelButton.addTarget(self, action: #selector(OrderFormViewCell.cancelOrderForm), forControlEvents: .TouchUpInside)
            confirmButton.addTarget(self, action: #selector(OrderFormViewCell.pay), forControlEvents: .TouchUpInside)
            break
        
        case .Paid:
            println("orderStatus : Paid")
            // 已付款
            topLayoutView.backgroundColor = MalaColor_B1D0E8_0
            statusString.text = "交易完成"
            statusString.textColor = MalaColor_8FBCDD_0
            
            cancelButton.hidden = true
            confirmButton.hidden = false
            
            confirmButton.setTitle("再次购买", forState: .Normal)
            confirmButton.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Normal)
            confirmButton.setTitleColor(MalaColor_E26254_0, forState: .Normal)
            
            confirmButton.addTarget(self, action: #selector(OrderFormViewCell.buyAgain), forControlEvents: .TouchUpInside)
            break
        
        case .Canceled:
            println("orderStatus : Canceled")
            // 已取消
            topLayoutView.backgroundColor = MalaColor_CFCFCF_0
            statusString.text = "订单已关闭"
            statusString.textColor = MalaColor_939393_0
            
            cancelButton.hidden = true
            confirmButton.hidden = false
            
            confirmButton.setTitle("重新购买", forState: .Normal)
            confirmButton.setBackgroundImage(UIImage.withColor(UIColor.whiteColor()), forState: .Normal)
            confirmButton.setTitleColor(MalaColor_E26254_0, forState: .Normal)
            
            confirmButton.addTarget(self, action: #selector(OrderFormViewCell.buyAgain), forControlEvents: .TouchUpInside)
            break
        
        case .Refund:
            println("orderStatus : Refund")
            // 已退款
            topLayoutView.backgroundColor = MalaColor_B1D0E8_0
            statusString.text = "退款成功"
            statusString.textColor = MalaColor_83B84F_0
            
            cancelButton.hidden = true
            confirmButton.hidden = true
            break
        }
    }
    
    
    // MARK: - Event Response
    /// 立即支付
    @objc private func pay() {
        print("立即支付")
    }
    
    /// 再次购买（重复购买）
    @objc private func buyAgain() {
        print("再次购买")
        MalaIsHasBeenEvaluatedThisSubject = model?.evaluated
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_PushTeacherDetailView, object: self.model?.teacher)
    }
    
    /// 取消订单
    @objc private func cancelOrderForm() {
        print("取消订单")
    }
    
    
    // MARK: - Override
    override func prepareForReuse() {
        super.prepareForReuse()
    }
}