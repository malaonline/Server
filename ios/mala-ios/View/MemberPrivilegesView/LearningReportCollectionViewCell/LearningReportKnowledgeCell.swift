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
    private lazy var barChartView: HorizontalBarChartView = {
        let barChartView = HorizontalBarChartView()
        
        barChartView.backgroundColor = UIColor.lightGrayColor()
        
        barChartView.drawBarShadowEnabled = false
        barChartView.drawValueAboveBarEnabled = true
        
        let XAxis = barChartView.xAxis
        XAxis.labelPosition = .Bottom
        XAxis.drawAxisLineEnabled = false
        XAxis.drawGridLinesEnabled = false
        
        let leftAxis = barChartView.leftAxis
        leftAxis.drawAxisLineEnabled = false
        leftAxis.drawGridLinesEnabled = false
        leftAxis.axisMinValue = 0
        
        
        barChartView.legend.enabled = false
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
//        let yVals1 = [
//            ChartDataEntry(value: 0, xIndex: 0),
//            ChartDataEntry(value: 30, xIndex: 1),
//            ChartDataEntry(value: 90, xIndex: 2),
//            ChartDataEntry(value: 23, xIndex: 3),
//            ChartDataEntry(value: 48, xIndex: 4),
//            ChartDataEntry(value: 0, xIndex: 5)
//        ]
//        let set1 = BarChartDataSet(yVals: <#T##[ChartDataEntry]?#>, label: "")
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