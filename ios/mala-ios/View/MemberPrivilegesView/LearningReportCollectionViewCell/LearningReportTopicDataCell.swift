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
    
    // MARK: - Property
    /// 题目数据
    private var model: [SingleTimeIntervalData] = MalaConfig.topicSampleData() {
        didSet {
            resetData()
        }
    }
    override var asSample: Bool {
        didSet {
            if asSample {
                model = MalaConfig.topicSampleData()
            }else {
                hideDescription()
                model = MalaSubjectReport.month_trend
            }
        }
    }
    
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
        leftAxis.axisMaxValue = 200
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
        setupSampleData()
        resetData()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configure() {
        titleLabel.text = "题目数据分析"
        descDetailLabel.text = "学生4月上旬正确率有上升空间，4月下旬经过大量练习题练习，接触题型增多，通过针对性的专项模块练习，5月下旬正确率明显达到了优秀水平。"
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
    
    // 重置数据
    private func resetData() {
        
        var totalIndex = 0
        var rightIndex = 0
        
        // 总练习数据
        var yValsTotal = model.map { (data) -> ChartDataEntry in
            totalIndex += 1
            return ChartDataEntry(value: Double(data.total_item), xIndex: totalIndex)
        }
        packageData(&yValsTotal)

        let totalSet = LineChartDataSet(yVals: yValsTotal, label: "答题数量")
        totalSet.lineWidth = 0
        totalSet.fillAlpha = 1
        totalSet.setColor(MalaColor_BBDDF6_0)
        totalSet.fillColor = MalaColor_BBDDF6_0
        totalSet.drawCirclesEnabled = false
        totalSet.drawValuesEnabled = false
        totalSet.drawFilledEnabled = true
        
        // 正确练习数据
        var yValsRight = model.map { (data) -> ChartDataEntry in
            rightIndex += 1
            return ChartDataEntry(value: Double(data.total_item-data.error_item), xIndex: rightIndex)
        }
        packageData(&yValsRight)
        
        let rightSet = LineChartDataSet(yVals: yValsRight, label: "正确数量")
        rightSet.lineWidth = 0
        rightSet.fillAlpha = 1
        rightSet.setColor(MalaColor_75CC97_0)
        rightSet.fillColor = MalaColor_75CC97_0
        rightSet.drawCirclesEnabled = false
        rightSet.drawValuesEnabled = false
        rightSet.drawFilledEnabled = true
        
        let data = LineChartData(xVals: getXVals(), dataSets: [totalSet, rightSet])
        data.setValueTextColor(MalaColor_5E5E5E_0)
        data.setValueFont(UIFont.systemFontOfSize(10))
        lineChartView.data = data
    }
    
    // 设置样本数据
    private func setupSampleData() {
        
    }
    
    // 包装数据（在数据首尾分别添加空数据，以保持折线图美观性）
    private func packageData(inout data: [ChartDataEntry]) {
        data.insert(ChartDataEntry(value: 0, xIndex: 0), atIndex: 0)
        data.append(ChartDataEntry(value: 0, xIndex: data.count))
    }
    
    // 获取X轴文字信息
    private func getXVals() -> [String] {
        var xVals = model.map { (data) -> String in
            return String(format: "%d月%@", data.month, data.periodString)
        }
        xVals.insert("", atIndex: 0)
        xVals.append("")
        return xVals
    }
}