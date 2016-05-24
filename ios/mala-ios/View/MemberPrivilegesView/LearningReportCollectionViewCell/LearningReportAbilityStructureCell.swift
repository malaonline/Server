//
//  LearningReportAbilityStructureCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/19.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit
import Charts

class LearningReportAbilityStructureCell: MalaBaseReportCardCell {
    
    
    // MARK: - Components
    /// 雷达图
    private lazy var radarChartView: RadarChartView = {
        let radarChartView = RadarChartView()
        
        radarChartView.animate(xAxisDuration: 0.65)
        radarChartView.descriptionText = ""
        radarChartView.webLineWidth = 1
        radarChartView.innerWebLineWidth = 1
        radarChartView.webAlpha = 1
        radarChartView.rotationEnabled = false
        radarChartView.innerWebColor = UIColor.whiteColor()
        radarChartView.webColor = UIColor.whiteColor()
                
        let xAxis = radarChartView.xAxis
        xAxis.labelFont = UIFont.systemFontOfSize(12)
        xAxis.labelTextColor = MalaColor_5E5E5E_0
        xAxis.labelWidth = 20
        
        let yAxis = radarChartView.yAxis
        yAxis.enabled = false
        yAxis.axisMinValue = 0
        
        radarChartView.legend.enabled = false
        return radarChartView
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
        titleLabel.text = "能力结构分析"
        descDetailLabel.text = "学生运算求解能力很强，空间想象和数据分析能力较弱，应加强针对性练习，实际应用能力也需注意引导。"
        
        // 样本数据
        let yVals1 = [
            ChartDataEntry(value: 81, xIndex: 0),
            ChartDataEntry(value: 67, xIndex: 1),
            ChartDataEntry(value: 99, xIndex: 2),
            ChartDataEntry(value: 53, xIndex: 3),
            ChartDataEntry(value: 78, xIndex: 4),
        ]
        let set1 = RadarChartDataSet(yVals: yVals1, label: "")
        set1.lineWidth = 0
        set1.fillAlpha = 0.9
        set1.setColor(MalaColor_F9877C_0)
        set1.fillColor = MalaColor_F9877C_0
        set1.drawValuesEnabled = false
        set1.drawFilledEnabled = true
        set1.highlightEnabled = false
        
        let data = RadarChartData(xVals: ["推理论证", "      数据分析", "空间想象", "运算求解", "实际应用      "], dataSets: [set1])
        data.setValueFont(UIFont.systemFontOfSize(10))
        data.setDrawValues(false)
        radarChartView.data = data
    }
    
    private func setupUserInterface() {
        // SubViews
        layoutView.addSubview(radarChartView)
        
        // Autolayout
        radarChartView.snp_makeConstraints { (make) in
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.18)
            make.left.equalTo(descView.snp_left)
            make.right.equalTo(descView.snp_right)
            make.bottom.equalTo(layoutView.snp_bottom).multipliedBy(0.68)
        }
    }
}