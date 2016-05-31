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
    
    // MARK: - Property
    /// 能力结构数据
    var model: [SingleAbilityData] = MalaConfig.abilitySampleData() {
        didSet {
            resetData()
        }
    }
    
    
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
        resetData()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func configure() {
        titleLabel.text = "能力结构分析"
        descDetailLabel.text = "学生运算求解能力很强，空间想象和数据分析能力较弱，应加强针对性练习，实际应用能力也需注意引导。"
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
    
    // 设置样本数据
    private func setupSampleData() {
        
    }
    
    // 重置数据
    private func resetData() {
        
        var index = -1
        // 数据
        let yVals = model.map { (data) -> ChartDataEntry in
            index += 1
            return ChartDataEntry(value: Double(data.value), xIndex: index)
        }
        
        // 设置UI
        let dataSet = RadarChartDataSet(yVals: yVals, label: "")
        dataSet.lineWidth = 0
        dataSet.fillAlpha = 0.9
        dataSet.setColor(MalaColor_F9877C_0)
        dataSet.fillColor = MalaColor_F9877C_0
        dataSet.drawValuesEnabled = false
        dataSet.drawFilledEnabled = true
        dataSet.highlightEnabled = false
        
        let data = RadarChartData(xVals: getXVals(), dataSets: [dataSet])
        data.setValueFont(UIFont.systemFontOfSize(10))
        data.setDrawValues(false)
        radarChartView.data = data
    }
    
    // 获取X轴文字信息
    private func getXVals() -> [String] {
        var xVals = [String]()
        for (index, data) in model.enumerate() {
            var abilityString = data.abilityString
            if index == 1 || index == 2 {
                abilityString = "   "+abilityString
            }else if index == 4 || index == 5 {
                abilityString = abilityString+"   "
            }
            xVals.append(abilityString)
        }
        return xVals
    }
}