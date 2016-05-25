//
//  LearningReportAbilityImproveCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/19.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit
import Charts

class LearningReportAbilityImproveCell: MalaBaseReportCardCell {
    
    
    // MARK: - Components
    /// 图例布局视图
    private lazy var legendView: CombinedLegendView = {
        let view = CombinedLegendView()
        return view
    }()
    /// 组合统计视图（条形&折线）
    private lazy var combinedChartView: CombinedChartView = {
        
        let chartView = CombinedChartView()
        chartView.animate(xAxisDuration: 0.65)
        chartView.drawOrder = [
            CombinedChartDrawOrder.Bar.rawValue,
            CombinedChartDrawOrder.Line.rawValue
        ]
        
        chartView.descriptionText = ""
        chartView.scaleXEnabled = false
        chartView.scaleYEnabled = false
        chartView.dragEnabled = false
        chartView.drawBarShadowEnabled = false
        chartView.drawValueAboveBarEnabled = true
        
        let xAxis = chartView.xAxis
        xAxis.labelFont = UIFont.systemFontOfSize(9)
        xAxis.labelTextColor = MalaColor_5E5E5E_0
        xAxis.drawGridLinesEnabled = false
        xAxis.labelPosition = .Bottom
        
        let leftAxis = chartView.leftAxis
        leftAxis.labelFont = UIFont.systemFontOfSize(10)
        leftAxis.labelTextColor = MalaColor_5E5E5E_0
        leftAxis.gridLineDashLengths = [2,2]
        leftAxis.gridColor = MalaColor_E6E9EC_0
        leftAxis.drawGridLinesEnabled = true
        leftAxis.axisMinValue = 0
        leftAxis.axisMaxValue = 100
        leftAxis.labelCount = 5
        
        let pFormatter = NSNumberFormatter()
        pFormatter.numberStyle = .PercentStyle
        pFormatter.maximumFractionDigits = 1
        pFormatter.multiplier = 1
        pFormatter.percentSymbol = "%"
        leftAxis.valueFormatter = pFormatter
        
        chartView.rightAxis.enabled = false
        chartView.legend.enabled = false
        return chartView
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
        titleLabel.text = "提分点分析"
        descDetailLabel.text = "学生对于圆的知识点、函数初步知识点和几何变换知识点能力突出，可减少习题数。实数可加强练习。"
        legendView.addLegend(image: "dot_legend", title: "平均分数")
        legendView.addLegend(image: "histogram_legend", title: "我的评分")
        
        // 样本数据
        let data = CombinedChartData(xVals: ["实数", "函数初步", "多边形", "圆", "全等", "相似", "几何变换"])
        
        let yLineVals = [
            ChartDataEntry(value: 10, xIndex: 0),
            ChartDataEntry(value: 22, xIndex: 1),
            ChartDataEntry(value: 23, xIndex: 2),
            ChartDataEntry(value: 14, xIndex: 3),
            ChartDataEntry(value: 20, xIndex: 4),
            ChartDataEntry(value: 37, xIndex: 5),
            ChartDataEntry(value: 50, xIndex: 6)
        ]
        let lineDataSet = LineChartDataSet(yVals: yLineVals, label: "")
        lineDataSet.setColor(MalaColor_82C9F9_0)
        lineDataSet.fillAlpha = 1
        lineDataSet.circleRadius = 6
        lineDataSet.drawCubicEnabled = true
        lineDataSet.drawValuesEnabled = true
        lineDataSet.setDrawHighlightIndicators(false)
        
        let yBarVals = [
            BarChartDataEntry(value: 34, xIndex: 0),
            BarChartDataEntry(value: 40, xIndex: 1),
            BarChartDataEntry(value: 30, xIndex: 2),
            BarChartDataEntry(value: 32, xIndex: 3),
            BarChartDataEntry(value: 39, xIndex: 4),
            BarChartDataEntry(value: 47, xIndex: 5),
            BarChartDataEntry(value: 61, xIndex: 6)
        ]
        let barDataSet = BarChartDataSet(yVals: yBarVals, label: "")
        barDataSet.drawValuesEnabled = true
        barDataSet.colors = MalaConfig.chartsColor()
        barDataSet.highlightEnabled = false
        barDataSet.barSpace = 0.4
        
        let lineData = LineChartData()
        lineData.addDataSet(lineDataSet)
        lineData.setDrawValues(false)
        
        let barData = BarChartData()
        barData.addDataSet(barDataSet)
        barData.setDrawValues(false)
        
        data.lineData = lineData
        data.barData = barData
        
        combinedChartView.data = data
    }
    
    private func setupUserInterface() {
        // Style
        
        
        // SubViews
        layoutView.addSubview(combinedChartView)
        layoutView.addSubview(legendView)
        
        // Autolayout
        legendView.snp_makeConstraints { (make) in
            make.left.equalTo(descView.snp_left)
            make.right.equalTo(descView.snp_right)
            make.height.equalTo(12)
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.17)
        }
        combinedChartView.snp_makeConstraints { (make) in
            make.top.equalTo(legendView.snp_bottom)
            make.left.equalTo(descView.snp_left)
            make.right.equalTo(descView.snp_right)
            make.bottom.equalTo(layoutView.snp_bottom).multipliedBy(0.68)
        }
    }
}

// MARK: - LegendView
public class CombinedLegendView: UIView {
    
    // MARK: - Property
    private var currentButton: UIButton?
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    public func addLegend(image imageName: String, title: String) -> UIButton {
        let button = UIButton()
        button.adjustsImageWhenHighlighted = false
        
        button.setImage(UIImage(named: imageName), forState: .Normal)
        button.imageEdgeInsets = UIEdgeInsets(top: 0, left: -5, bottom: 0, right: 5)
        
        button.setTitle(title, forState: .Normal)
        button.titleLabel?.font = UIFont.systemFontOfSize(10)
        button.setTitleColor(MalaColor_5E5E5E_0, forState: .Normal)
        
        button.sizeToFit()
        self.addSubview(button)
        
        button.snp_makeConstraints { (make) in
            make.centerY.equalTo(self.snp_centerY)
            make.right.equalTo(currentButton?.snp_left ?? self.snp_right).offset(-13)
        }
        currentButton = button
        
        return button
    }
}