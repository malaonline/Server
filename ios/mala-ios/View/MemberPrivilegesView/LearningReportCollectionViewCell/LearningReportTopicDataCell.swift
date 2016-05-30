//
//  LearningReportTopicDataCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/19.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit
import Charts

class LearningReportTopicDataCell: MalaBaseReportCardCell {
    
    
    // MARK: - Components
    /// 折线统计视图
    private lazy var lineChartView: LineChartView = {
        let lineChartView = LineChartView()
        
        lineChartView.animate(xAxisDuration: 0.65)
        lineChartView.descriptionText = ""
        lineChartView.scaleXEnabled = false
        lineChartView.scaleYEnabled = false
        lineChartView.dragEnabled = false
        lineChartView.drawGridBackgroundEnabled = true
        lineChartView.pinchZoomEnabled = false
        lineChartView.rightAxis.enabled = false
        lineChartView.gridBackgroundColor = UIColor.whiteColor()
        
        let xAxis = lineChartView.xAxis
        xAxis.labelFont = UIFont.systemFontOfSize(10)
        xAxis.labelTextColor = MalaColor_5E5E5E_0
        xAxis.drawGridLinesEnabled = false
        xAxis.spaceBetweenLabels = 1
        xAxis.labelPosition = .Bottom
        xAxis.gridLineDashLengths = [2,2]
        xAxis.gridColor = MalaColor_E6E9EC_0
        xAxis.drawGridLinesEnabled = true
        
        let leftAxis = lineChartView.leftAxis
        leftAxis.labelFont = UIFont.systemFontOfSize(9)
        leftAxis.labelTextColor = MalaColor_939393_0
        leftAxis.gridLineDashLengths = [2,2]
        leftAxis.gridColor = MalaColor_E6E9EC_0
        leftAxis.drawGridLinesEnabled = true
        leftAxis.axisMinValue = 0
        leftAxis.labelCount = 5

        lineChartView.legend.enabled = true
        lineChartView.legend.form = .Circle
        lineChartView.legend.formSize = 8
        lineChartView.legend.font = NSUIFont.systemFontOfSize(10)
        lineChartView.legend.textColor = MalaColor_5E5E5E_0
        lineChartView.legend.position = .AboveChartRight
        return lineChartView
    }()
    
    
    // MARK: - Instance Method
    override init(frame: CGRect) {
        super.init(frame: frame)
        configure()
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configure() {
        titleLabel.text = "题目数据分析"
        descDetailLabel.text = "学生4月上旬正确率有上升空间，4月下旬经过大量练习题练习，接触题型增多，通过针对性的专项模块练习，5月下旬正确率明显达到了优秀水平。"
        
        // 样本数据
        let yVals1 = [
            ChartDataEntry(value: 0, xIndex: 0),
            ChartDataEntry(value: 47, xIndex: 1),
            ChartDataEntry(value: 100, xIndex: 2),
            ChartDataEntry(value: 53, xIndex: 3),
            ChartDataEntry(value: 53, xIndex: 4),
            ChartDataEntry(value: 0, xIndex: 5)
        ]
        let set1 = LineChartDataSet(yVals: yVals1, label: "答题数量")
        set1.lineWidth = 0
        set1.fillAlpha = 1
        set1.setColor(MalaColor_BBDDF6_0)
        set1.fillColor = MalaColor_BBDDF6_0
        set1.drawCirclesEnabled = false
        set1.drawValuesEnabled = false
        set1.drawFilledEnabled = true
        
        let yVals2 = [
            ChartDataEntry(value: 0, xIndex: 0),
            ChartDataEntry(value: 30, xIndex: 1),
            ChartDataEntry(value: 90, xIndex: 2),
            ChartDataEntry(value: 23, xIndex: 3),
            ChartDataEntry(value: 48, xIndex: 4),
            ChartDataEntry(value: 0, xIndex: 5)
            ]
        let set2 = LineChartDataSet(yVals: yVals2, label: "正确数量")
        set2.lineWidth = 0
        set2.fillAlpha = 1
        set2.setColor(MalaColor_75CC97_0)
        set2.fillColor = MalaColor_75CC97_0
        set2.drawCirclesEnabled = false
        set2.drawValuesEnabled = false
        set2.drawFilledEnabled = true
        
        let data = LineChartData(xVals: ["", "4月上", "4月下", "5月上", "5月下", ""], dataSets: [set1, set2])
        data.setValueTextColor(MalaColor_5E5E5E_0)
        data.setValueFont(UIFont.systemFontOfSize(10))
        lineChartView.data = data
    }
    
    private func setupUserInterface() {
        // SubViews
        layoutView.addSubview(lineChartView)
        
        // Autolayout
        lineChartView.snp_makeConstraints { (make) in
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.18)
            make.left.equalTo(descView.snp_left)
            make.right.equalTo(descView.snp_right)
            make.bottom.equalTo(layoutView.snp_bottom).multipliedBy(0.68)
        }
    }
}
