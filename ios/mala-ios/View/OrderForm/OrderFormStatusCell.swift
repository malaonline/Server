//
//  OrderFormStatusCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/13.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class OrderFormStatusCell: UITableViewCell {
    
    // MARK: - Property
    /// 订单详情模型
    var model: OrderForm? {
        didSet {
            /// 老师头像
            if let url = NSURL(string: (model?.avatarURL ?? "")) {
                self.avatarView.ma_setImage(url, placeholderImage: UIImage(named: "profileAvatar_placeholder"))
            }
            
            /// 订单状态
            if let status = MalaOrderStatus(rawValue: (model?.status ?? "")) {
                switch status {
                case .Penging:
                    self.statusLabel.text = "订单待支付"
                    break
                    
                case .Paid:
                    self.statusLabel.text = "支付成功"
                    break
                    
                case .Canceled:
                    self.statusLabel.text = "订单已关闭"
                    break
                    
                case .Refund:
                    self.statusLabel.text = "退款成功"
                    break
                    
                case .Confirm:
                    self.statusLabel.text = "确认订单"
                    break
                }
            }
            
            self.teacherLabel.text = model?.teacherName
            self.subjectLabel.text = (model?.gradeName ?? "") + " " + (model?.subjectName ?? "")
            self.schoolLabel.text = model?.schoolName
        }
    }
    

    // MARK: - Components
    /// cell标题
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "订单状态",
            fontSize: 13,
            textColor: MalaColor_6C6C6C_0
        )
        return label
    }()
    /// 订单状态
    private lazy var statusLabel: UILabel = {
        let label = UILabel(
            text: "状态",
            fontSize: 13,
            textColor: MalaColor_E36A5C_0
        )
        return label
    }()
    /// 分割线
    private lazy var separatorLine: UIView = {
        let view = UIView.separator(MalaColor_E5E5E5_0)
        return view
    }()
    /// 老师姓名图标
    private lazy var teacherIcon: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "order_teacher"))
        return imageView
    }()
    /// 老师姓名
    private lazy var teacherLabel: UILabel = {
        let label = UILabel(
            text: "教师姓名",
            fontSize: 13,
            textColor: MalaColor_6C6C6C_0
        )
        return label
    }()
    /// 学科信息图标
    private lazy var subjectIcon: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "order_subject"))
        return imageView
    }()
    /// 学科信息
    private lazy var subjectLabel: UILabel = {
        let label = UILabel(
            text: "年级-学科",
            fontSize: 13,
            textColor: MalaColor_6C6C6C_0
        )
        return label
    }()
    /// 上课地点图标
    private lazy var schoolIcon: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "order_school"))
        return imageView
    }()
    /// 上课地点
    private lazy var schoolLabel: UILabel = {
        let label = UILabel(
            text: "上课地点",
            fontSize: 13,
            textColor: MalaColor_6C6C6C_0
        )
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
        contentView.addSubview(titleLabel)
        contentView.addSubview(statusLabel)
        contentView.addSubview(separatorLine)
        
        contentView.addSubview(teacherIcon)
        contentView.addSubview(teacherLabel)
        contentView.addSubview(subjectIcon)
        contentView.addSubview(subjectLabel)
        contentView.addSubview(schoolIcon)
        contentView.addSubview(schoolLabel)
        
        contentView.addSubview(avatarView)
        
        // Autolayout
        // Remove margin
        titleLabel.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(self.contentView.snp_top).offset(10)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.height.equalTo(13)
        }
        statusLabel.snp_makeConstraints { (make) in
            make.top.equalTo(titleLabel.snp_top)
            make.right.equalTo(self.contentView.snp_right).offset(-12)
        }
        separatorLine.snp_makeConstraints { (make) in
            make.top.equalTo(titleLabel.snp_bottom).offset(10)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.right.equalTo(self.contentView.snp_right).offset(-12)
            make.height.equalTo(MalaScreenOnePixel)
        }
        teacherIcon.snp_makeConstraints { (make) in
            make.top.equalTo(separatorLine.snp_bottom).offset(10)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.height.equalTo(13)
            make.width.equalTo(13)
        }
        teacherLabel.snp_makeConstraints { (make) in
            make.top.equalTo(teacherIcon.snp_top)
            make.left.equalTo(teacherIcon.snp_right).offset(10)
            make.height.equalTo(13)
        }
        subjectIcon.snp_makeConstraints { (make) in
            make.top.equalTo(teacherIcon.snp_bottom).offset(10)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.height.equalTo(13)
            make.width.equalTo(13)
        }
        subjectLabel.snp_makeConstraints { (make) in
            make.top.equalTo(subjectIcon.snp_top)
            make.left.equalTo(subjectIcon.snp_right).offset(10)
            make.height.equalTo(13)
        }
        schoolIcon.snp_makeConstraints { (make) in
            make.top.equalTo(subjectIcon.snp_bottom).offset(10)
            make.left.equalTo(self.contentView.snp_left).offset(12)
            make.height.equalTo(13)
            make.width.equalTo(13)
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-10)
        }
        schoolLabel.snp_makeConstraints { (make) in
            make.top.equalTo(schoolIcon.snp_top)
            make.left.equalTo(schoolIcon.snp_right).offset(10)
            make.height.equalTo(13)
        }
        avatarView.snp_makeConstraints { (make) in
            make.centerY.equalTo(subjectIcon.snp_centerY)
            make.right.equalTo(separatorLine.snp_right)
            make.height.equalTo(55)
            make.width.equalTo(55)
        }
    }
}