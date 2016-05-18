//
//  LearningReportCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/17.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

class LearningReportCell: UITableViewCell {
    // MARK: - Components
    /// 父布局容器（白色卡片）
    private lazy var content: UIView = {
        let view = UIView()
        return view
    }()
    /// 按钮
    private lazy var button: UIButton = {
        let button = UIButton()
        
        button.backgroundColor = MalaColor_8DC1DE_0
        button.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_16)
        button.setTitle("查看我的学习报告", forState: .Normal)
        button.setTitleColor(UIColor.whiteColor(), forState: .Normal)
        
        button.layer.cornerRadius = 5
        button.layer.masksToBounds = true
        
        button.addTarget(self, action: #selector(LearningReportCell.login), forControlEvents: .TouchUpInside)
        return button
    }()
    /// 学科标签
    private lazy var subjectLabel: UIButton = {
        let button = UIButton()
        button.setBackgroundImage(UIImage(named:"subject_background"), forState: .Normal)
        button.setTitle("数学", forState: .Normal)
        button.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        button.userInteractionEnabled = false
        return button
    }()
    /// 标题标签
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "学习报告",
            fontSize: MalaLayout_FontSize_15,
            textColor: MalaColor_333333_0
        )
        return label
    }()
    /// 中央垂直分割线
    private lazy var separator: UIView = {
        let view = UIView.separator(MalaColor_E5E5E5_0)
        return view
    }()
    /// 答题数标签
    private lazy var answerNumberLabel: UILabel = {
        let label = UILabel(
            text: "0",
            fontSize: 35,
            textColor: MalaColor_333333_0
        )
        label.textAlignment = .Center
        return label
    }()
    /// 正确率标签
    private lazy var correctRateLabel: UILabel = {
        let label = UILabel(
            text: "0％",
            fontSize: 35,
            textColor: MalaColor_333333_0
        )
        label.textAlignment = .Center
        return label
    }()
    /// 答题数图例
    private lazy var answerNumberLegend: UIButton = {
        let button = UIButton()
        button.setImage(UIImage(named: "answerNumber"), forState: .Normal)
        button.setTitle("答题数", forState: .Normal)
        button.titleLabel?.font = UIFont.systemFontOfSize(13)
        button.setTitleColor(MalaColor_636363_0, forState: .Normal)
        button.imageEdgeInsets = UIEdgeInsets(top: 0, left: -6, bottom: 0, right: 6)
        return button
    }()
    /// 正确率图例
    private lazy var correctRateLegend: UIButton = {
        let button = UIButton()
        button.setImage(UIImage(named: "correctRate"), forState: .Normal)
        button.setTitle("正确率", forState: .Normal)
        button.titleLabel?.font = UIFont.systemFontOfSize(13)
        button.setTitleColor(MalaColor_636363_0, forState: .Normal)
        button.imageEdgeInsets = UIEdgeInsets(top: 0, left: -6, bottom: 0, right: 6)
        return button
    }()
    
    
    
    // MARK: - Instance Method
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
        content.backgroundColor = UIColor.whiteColor()
        
        // SubViews
        contentView.addSubview(content)
        content.addSubview(button)
        content.addSubview(titleLabel)
        content.addSubview(separator)
        content.addSubview(subjectLabel)
        content.addSubview(answerNumberLabel)
        content.addSubview(correctRateLabel)
        content.addSubview(answerNumberLegend)
        content.addSubview(correctRateLegend)
        
        // Autolayout
//        let learningReportCellHeight: CGFloat = MalaContentHeight-(8*3)-229
        content.snp_makeConstraints { (make) in
            make.top.equalTo(self.contentView.snp_top).offset(MalaLayout_Margin_8)
            make.left.equalTo(self.contentView.snp_left)
            make.right.equalTo(self.contentView.snp_right)
            make.height.equalTo(212)
            make.bottom.equalTo(self.contentView.snp_bottom)
        }
        titleLabel.snp_makeConstraints { (make) in
            make.top.equalTo(content.snp_top).offset(MalaLayout_Margin_16)
            make.left.equalTo(content.snp_top).offset(MalaLayout_Margin_12)
            make.height.equalTo(MalaLayout_FontSize_15)
        }
        subjectLabel.snp_makeConstraints { (make) in
            make.top.equalTo(content.snp_top).offset(-MalaLayout_Margin_4)
            make.right.equalTo(content.snp_right).offset(-MalaLayout_Margin_12)
            make.width.equalTo(40.5)
            make.height.equalTo(34)
        }
        separator.snp_makeConstraints { (make) in
            make.top.equalTo(titleLabel.snp_bottom).offset(MalaLayout_Margin_20)
            make.centerX.equalTo(content.snp_centerX)
            make.width.equalTo(MalaScreenOnePixel)
            make.bottom.equalTo(button.snp_top).offset(-MalaLayout_Margin_20)
        }
        answerNumberLabel.snp_makeConstraints { (make) in
            make.left.equalTo(button.snp_left)
            make.right.equalTo(separator.snp_left)
            make.bottom.equalTo(separator.snp_bottom)
            make.height.equalTo(35)
        }
        correctRateLabel.snp_makeConstraints { (make) in
            make.left.equalTo(separator.snp_right)
            make.right.equalTo(button.snp_right)
            make.bottom.equalTo(separator.snp_bottom)
            make.height.equalTo(35)
        }
        answerNumberLegend.snp_makeConstraints { (make) in
            make.centerX.equalTo(answerNumberLabel.snp_centerX)
            make.top.equalTo(separator.snp_top).offset(MalaLayout_Margin_8)
        }
        correctRateLegend.snp_makeConstraints { (make) in
            make.centerX.equalTo(correctRateLabel.snp_centerX)
            make.top.equalTo(separator.snp_top).offset(MalaLayout_Margin_8)
        }
        button.snp_makeConstraints { (make) in
            make.height.equalTo(37)
            make.left.equalTo(content.snp_left).offset(MalaLayout_Margin_12)
            make.right.equalTo(content.snp_right).offset(-MalaLayout_Margin_12)
            make.bottom.equalTo(content.snp_bottom).offset(-MalaLayout_Margin_20)
        }
    }
    
    
    
    // MARK: - Event Response
    @objc private func login() {
        
    }
}