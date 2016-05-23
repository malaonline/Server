//
//  LearningReportHomeworkDataCell.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/19.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit
import Charts

class LearningReportHomeworkDataCell: MalaBaseCardCell {
    
    // MARK: - Components
    /// 标题标签
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "作业数据分析",
            fontSize: 16,
            textColor: MalaColor_5E5E5E_0
        )
        return label
    }()
    /// 分割线
    private lazy var separatorLine: UIView = {
        let view = UIView.separator(MalaColor_EDEDED_0)
        return view
    }()
    /// 学习信息标签
    private lazy var infoLabel: UILabel = {
        let label = UILabel(
            text: "累计答题1706道  超过85%",
            fontSize: 10,
            textColor: MalaColor_5E5E5E_0
        )
        label.backgroundColor = MalaColor_E8F2F8_0
        label.textAlignment = .Center
        label.layer.cornerRadius = 11
        label.layer.masksToBounds = true
        return label
    }()
    /// 皇冠头像图片
    private lazy var infoIcon: UIImageView = {
        let imageView = UIImageView(image: UIImage(named: "reportTitle_icon"))
        return imageView
    }()
    /// 作业信息标签
    private lazy var homeworkLabel: UILabel = {
        let label = UILabel(
            text: "作业102次  准确率78%",
            fontSize: 10,
            textColor: MalaColor_8DBEDE_0
        )
        return label
    }()
    /// 饼形统计视图
    private lazy var pieChartView: PieChartView = {
        let pieChartView = PieChartView()
        
        pieChartView.centerText = "错题分布"
        pieChartView.holeRadiusPercent = 0.4
        pieChartView.animate(xAxisDuration: 0.65)
        
        pieChartView.usePercentValuesEnabled = true
        pieChartView.drawSliceTextEnabled = false
        pieChartView.rotationEnabled = false
        pieChartView.legend.enabled = false
//        pieChartView.legend.form = .Circle
//        pieChartView.legend.formSize = 10
//        pieChartView.legend.font = NSUIFont.systemFontOfSize(11)
//        pieChartView.legend.textColor = MalaColor_939393_0
//        pieChartView.legend.position = .BelowChartLeft
        pieChartView.descriptionText = ""
        return pieChartView
    }()
    /// 图例容器
    private lazy var legendView: PieLegendView = {
        let view = PieLegendView()
        return view
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
        // 样本数据
        
        // Y轴数据
        let set = PieChartDataSet(
            yVals: [
                ChartDataEntry(value: 0.08, xIndex: 0),
                ChartDataEntry(value: 0.09, xIndex: 1),
                ChartDataEntry(value: 0.12, xIndex: 2),
                ChartDataEntry(value: 0.17, xIndex: 3),
                ChartDataEntry(value: 0.21, xIndex: 4),
                ChartDataEntry(value: 0.03, xIndex: 5),
                ChartDataEntry(value: 0.18, xIndex: 6),
                ChartDataEntry(value: 0.07, xIndex: 7),
                ChartDataEntry(value: 0.05, xIndex: 8),
            ],
            label: nil
        )
        set.colors = MalaConfig.chartsColor()
        
        // X轴数据
        let xVals = MalaConfig.homeworkDataChartsTitle()
        // 设置数据、样式
        let data = PieChartData(xVals: xVals, dataSet: set)
        
        let pFormatter = NSNumberFormatter()
        pFormatter.numberStyle = .PercentStyle
        pFormatter.maximumFractionDigits = 1
        pFormatter.multiplier = 1
        pFormatter.percentSymbol = "%"
        data.setValueFormatter(pFormatter)
        data.setValueFont(UIFont.systemFontOfSize(11))
        data.setValueTextColor(UIColor.whiteColor())
        pieChartView.data = data
        
        for (index, string) in MalaConfig.homeworkDataChartsTitle().enumerate() {
            legendView.addLegend(color: MalaConfig.chartsColor()[index], title: string)
        }
    }
    
    private func setupUserInterface() {
        // Style
        contentView.backgroundColor = MalaColor_F2F2F2_0
        
        // SubViews
        layoutView.addSubview(titleLabel)
        layoutView.addSubview(separatorLine)
        layoutView.addSubview(infoLabel)
        layoutView.addSubview(infoIcon)
        layoutView.addSubview(homeworkLabel)
        layoutView.addSubview(pieChartView)
        layoutView.addSubview(legendView)
        
        
        // Autolayout
        titleLabel.snp_makeConstraints { (make) in
            make.height.equalTo(20)
            make.centerX.equalTo(layoutView.snp_centerX)
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.05)
        }
        separatorLine.snp_makeConstraints { (make) in
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.13)
            make.height.equalTo(MalaScreenOnePixel)
            make.centerX.equalTo(layoutView.snp_centerX)
            make.width.equalTo(layoutView.snp_width).multipliedBy(0.84)
        }
        infoLabel.snp_makeConstraints { (make) in
            make.width.equalTo(170)
            make.height.equalTo(22)
            make.centerX.equalTo(layoutView.snp_centerX)
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.17)
        }
        infoIcon.snp_makeConstraints { (make) in
            make.bottom.equalTo(infoLabel.snp_bottom)
            make.left.equalTo(infoLabel.snp_left)
            make.width.equalTo(22.5)
            make.height.equalTo(33)
        }
        homeworkLabel.snp_makeConstraints { (make) in
            make.top.equalTo(layoutView.snp_bottom).multipliedBy(0.23)
            make.height.equalTo(12)
            make.centerX.equalTo(layoutView.snp_centerX)
        }
        pieChartView.snp_makeConstraints { (make) in
            make.top.equalTo(homeworkLabel.snp_bottom)
            make.left.equalTo(layoutView.snp_left).offset(27)
            make.right.equalTo(layoutView.snp_right).offset(-27)
            make.height.equalTo(pieChartView.snp_width)//.offset(100)
        }
        legendView.snp_makeConstraints { (make) in
            make.top.equalTo(pieChartView.snp_bottom)
            make.left.equalTo(layoutView.snp_left).offset(27)
            make.right.equalTo(layoutView.snp_right).offset(-27)
            make.bottom.equalTo(layoutView.snp_bottom).multipliedBy(0.914)
        }
    }
}


// MARK: - LegendView
public class PieLegendView: UIView {
    
    // MARK: - Property
    private var currentX: CGFloat = MalaLayout_Margin_6
    private var currentY: CGFloat = 0 {
        didSet {
            if currentY != oldValue {
                self.currentX = MalaLayout_Margin_6
            }
        }
    }
    var viewCount: Int = 0
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    public func addLegend(color color: UIColor, title: String) -> UIButton {
        let button = UIButton()
        button.adjustsImageWhenHighlighted = false
        
        let image = UIImage.withColor(color, bounds: CGRect(x: 0, y: 0, width: 10, height: 10))
        button.setImage(image, forState: .Normal)
        button.imageView?.layer.cornerRadius = 5
        button.imageView?.layer.masksToBounds = true
        button.imageEdgeInsets = UIEdgeInsets(top: 0, left: -MalaLayout_Margin_6, bottom: 0, right: MalaLayout_Margin_6)
        
        button.setTitle(title, forState: .Normal)
        button.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        button.setTitleColor(MalaColor_939393_0, forState: .Normal)
        
        button.sizeToFit()
        currentY = CGFloat(Int(Int(self.viewCount)/3)*20)
        button.frame.origin.y = currentY
        currentX = CGFloat(Int(Int(self.viewCount)%3)*Int(MalaScreenWidth*0.35))
        button.frame.origin.x = currentX
        
        addSubview(button)
        viewCount += 1
        currentX = CGRectGetMaxX(button.frame)
        
        return button
    }
}