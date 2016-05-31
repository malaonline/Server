//
//  LearningReportKnowledgeCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/19.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit
import Charts

class LearningReportKnowledgeCell: MalaBaseReportCardCell {
    
    // MARK: - Property
    /// 知识点数据
    var model: SingleTopicData? {
        didSet {
            
        }
    }
    
    
    // MARK: - Components
    /// 学习信息标签
    private lazy var infoLabel: UILabel = {
        let label = UILabel(
            text: "注：21/50，即 答对题/总题目数",
            fontSize: 10,
            textColor: MalaColor_5E5E5E_0
        )
        label.backgroundColor = MalaColor_E8F2F8_0
        label.textAlignment = .Center
        label.layer.cornerRadius = 11
        label.layer.masksToBounds = true
        return label
    }()
    /// 饼形统计视图
    private lazy var barChartView: ThemeHorizontalBarChartView = {
        let barChartView = ThemeHorizontalBarChartView()
        return barChartView
    }()
    
    
    // MARK: - Instance Method
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUserInterface()
        configure()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configure() {
        titleLabel.text = "知识点分析"
        descDetailLabel.text = "学生各模块水平相差过大，应提前进行针对性练习，实数和相似等模块需提高，多边形板块可以适当减少练习量。"
        
        // 样本数据
        let vals = [
            ThemeHorizontalBarData(title: MalaConfig.homeworkDataChartsTitle()[0], color: MalaConfig.chartsColor()[0], rightNum: 11, totalNum: 30),
            ThemeHorizontalBarData(title: MalaConfig.homeworkDataChartsTitle()[1], color: MalaConfig.chartsColor()[1], rightNum: 21, totalNum: 50),
            ThemeHorizontalBarData(title: MalaConfig.homeworkDataChartsTitle()[2], color: MalaConfig.chartsColor()[2], rightNum: 109, totalNum: 117),
            ThemeHorizontalBarData(title: MalaConfig.homeworkDataChartsTitle()[7], color: MalaConfig.chartsColor()[3], rightNum: 21, totalNum: 50),
            ThemeHorizontalBarData(title: MalaConfig.homeworkDataChartsTitle()[4], color: MalaConfig.chartsColor()[4], rightNum: 114, totalNum: 120),
            ThemeHorizontalBarData(title: MalaConfig.homeworkDataChartsTitle()[5], color: MalaConfig.chartsColor()[5], rightNum: 21, totalNum: 50),
            ThemeHorizontalBarData(title: MalaConfig.homeworkDataChartsTitle()[6], color: MalaConfig.chartsColor()[6], rightNum: 54, totalNum: 62)
        ]
        barChartView.vals = vals
    }
    
    private func setupUserInterface() {
        // Style
        
        
        // SubViews
        layoutView.addSubview(infoLabel)
        layoutView.addSubview(barChartView)
        
        
        // Autolayout
        infoLabel.snp_makeConstraints { (make) in
            make.width.equalTo(170)
            make.height.equalTo(22)
            make.centerX.equalTo(layoutView.snp_centerX)
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.17)
        }
        barChartView.snp_makeConstraints { (make) in
            make.top.equalTo(infoLabel.snp_bottom).offset(10)
            make.left.equalTo(descView.snp_left)
            make.right.equalTo(descView.snp_right)
            make.bottom.equalTo(descView.snp_top).offset(-10)
        }
    }
}