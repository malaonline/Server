//
//  OrderFormTimeScheduleCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/13.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class OrderFormTimeScheduleCell: UITableViewCell {

    // MARK: - Property
    /// 课时
    var classPeriod: Int = 0 {
        didSet {
            periodLabel.text = String(format: "%d", classPeriod)
        }
    }
    /// 上课时间列表
    var timeSchedules: [[NSTimeInterval]]? {
        didSet {
            println("newValue: \(timeSchedules)")
            println("oldValue: \(oldValue)")
            
            if (timeSchedules ?? []) != (oldValue ?? []) && timeSchedules != nil {
                parseTimeSchedules()
            }
        }
    }
    /// 是否隐藏时间表（默认隐藏）
    var shouldHiddenTimeSlots: Bool = true {
        didSet {
            self.timeLineView?.hidden = shouldHiddenTimeSlots
        }
    }
    
    
    // MARK: - Components
    /// 顶部布局容器
    private lazy var topLayoutView: UIView = {
        let view = UIView()
        return view
    }()
    /// 分割线
    private lazy var separatorLine: UIView = {
        let view = UIView.separator(MalaColor_E5E5E5_0)
        return view
    }()
    /// 图标
    private lazy var iconView: UIView = {
        let view = UIView.separator(MalaColor_82B4D9_0)
        return view
    }()
    /// cell标题
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "上课时间",
            fontSize: MalaLayout_FontSize_15,
            textColor: MalaColor_333333_0
        )
        return label
    }()
    /// 课时
    private lazy var periodLabel: UILabel = {
        let label = UILabel(
            text: "0",
            fontSize: MalaLayout_FontSize_13,
            textColor: MalaColor_333333_0
        )
        return label
    }()
    private lazy var periodLeftLabel: UILabel = {
        let label = UILabel(
            text: "共计",
            fontSize: MalaLayout_FontSize_13,
            textColor: MalaColor_6C6C6C_0
        )
        return label
    }()
    private lazy var periodRightLabel: UILabel = {
        let label = UILabel(
            text: "课时",
            fontSize: MalaLayout_FontSize_13,
            textColor: MalaColor_6C6C6C_0
        )
        return label
    }()
    /// 上课时间表控件
    private  var timeLineView: ThemeTimeLine?
    
    
    // MARK: - Contructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        
        // SubViews
        contentView.addSubview(topLayoutView)
        topLayoutView.addSubview(separatorLine)
        topLayoutView.addSubview(iconView)
        topLayoutView.addSubview(titleLabel)
        
        topLayoutView.addSubview(periodRightLabel)
        topLayoutView.addSubview(periodLabel)
        topLayoutView.addSubview(periodLeftLabel)
        
        // Autolayout
        topLayoutView.snp_makeConstraints { (make) in
            make.top.equalTo(self.contentView.snp_top)
            make.left.equalTo(self.contentView.snp_left)
            make.right.equalTo(self.contentView.snp_right)
            make.height.equalTo(35)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-12)
        }
        separatorLine.snp_makeConstraints { (make) in
            make.bottom.equalTo(topLayoutView.snp_bottom)
            make.left.equalTo(topLayoutView.snp_left)
            make.right.equalTo(topLayoutView.snp_right)
            make.height.equalTo(MalaScreenOnePixel)
        }
        iconView.snp_makeConstraints { (make) in
            make.left.equalTo(topLayoutView.snp_left)
            make.centerY.equalTo(topLayoutView.snp_centerY)
            make.height.equalTo(19)
            make.width.equalTo(3)
        }
        titleLabel.snp_updateConstraints { (make) -> Void in
            make.centerY.equalTo(topLayoutView.snp_centerY)
            make.left.equalTo(topLayoutView.snp_left).offset(MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_15)
        }
        periodRightLabel.snp_makeConstraints { (make) in
            make.centerY.equalTo(topLayoutView.snp_centerY)
            make.right.equalTo(topLayoutView.snp_right).offset(-MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_13)
        }
        periodLabel.snp_makeConstraints { (make) in
            make.centerY.equalTo(topLayoutView.snp_centerY)
            make.right.equalTo(periodRightLabel.snp_left).offset(-MalaLayout_Margin_5)
            make.height.equalTo(MalaLayout_FontSize_13)
        }
        periodLeftLabel.snp_makeConstraints { (make) in
            make.centerY.equalTo(topLayoutView.snp_centerY)
            make.right.equalTo(periodLabel.snp_left).offset(-MalaLayout_Margin_5)
            make.height.equalTo(MalaLayout_FontSize_13)
        }
    }
    
    private func parseTimeSchedules() {
        
        // 解析时间表数据
        let result = parseTimeSlots((self.timeSchedules ?? []))
        
        // 设置UI
        self.timeLineView = ThemeTimeLine(times: result.dates, descs: result.times)
        timeLineView?.hidden = true
        
        self.contentView.addSubview(timeLineView!)
        topLayoutView.snp_updateConstraints { (make) in
            make.bottom.equalTo(timeLineView!.snp_top).offset(-MalaLayout_Margin_10)
        }
        timeLineView!.snp_updateConstraints { (make) in
            make.top.equalTo(topLayoutView.snp_bottom).offset(MalaLayout_Margin_10)
            make.left.equalTo(self.contentView.snp_left).offset(MalaLayout_Margin_12)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_16)
            make.height.equalTo(CGFloat(15*result.heightCount))
        }
    }
}