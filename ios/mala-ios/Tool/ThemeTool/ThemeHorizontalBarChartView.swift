//
//  ThemeHorizontalBarChartView.swift
//  mala-ios
//
//  Created by 王新宇 on 16/5/26.
//  Copyright © 2016年 Mala Online. All rights reserved.
//

import UIKit

public class ThemeHorizontalBarChartView: UIView {

    // MARK: - Property
    /// 数据
    var vals: [ThemeHorizontalBarData] = [] {
        didSet{
            dispatch_async(dispatch_get_main_queue(), { [weak self] () -> Void in
                self?.setupData()
            })
        }
    }
    
    
    // MARK: - Components
    /// 条形视图数组
    var bars: [ThemeHorizontalBar] = []
    
    
    
    // MARK: - Instance Method
    override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupData() {
        
        for (index, data) in vals.enumerate() {
           
            let bar = ThemeHorizontalBar()
            self.addSubview(bar)
            
            bar.snp_makeConstraints(closure: { (make) in
                make.height.equalTo(16)//self.snp_height).multipliedBy(1/count*2)
                make.top.equalTo(self.snp_top).offset(((index*2)+1)*16)
                make.left.equalTo(self.snp_left)
                make.right.equalTo(self.snp_right)
            })
            
            bar.data = data
            bars.append(bar)
        }
    }
    
    ///  移除所有图表
    func removeAllCharts() {
        for chart in bars {
            chart.removeFromSuperview()
        }
    }
}


public class ThemeHorizontalBar: UIView {
    
    // MARK: - Property
    /// 数据
    var data: ThemeHorizontalBarData = ThemeHorizontalBarData() {
        didSet {
            titleLabel.text = data.title
            progressBar.progressTintColors = [data.color]
            
            var percent: CGFloat = 0
            if data.totalNum != 0 {
                percent = CGFloat(data.rightNum)/CGFloat(data.totalNum)
                percentLabel.text = String(format: "%d%%", Int(percent*100))
                progressBar.setProgress(percent, animated: false)
            }
            progressBar.rightNum = data.rightNum
            progressBar.totalNum = data.totalNum
        }
    }
    
    
    // MARK: - Components
    /// 数据标题标签
    private lazy var titleLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: 11,
            textColor: MalaColor_939393_0
        )
        return label
    }()
    /// 条形图
    private lazy var progressBar: YLProgressBar = {
        let bar = YLProgressBar()
        bar.indicatorTextDisplayMode = .Progress
        bar.behavior = .Indeterminate
        bar.stripesOrientation = .Left
        bar.progressTintColor = MalaColor_E5E5E5_0
        bar.trackTintColor = MalaColor_E5E5E5_0
        bar.stripesColor = MalaColor_E5E5E5_0
        bar.progressTintColors = [MalaColor_75CC97_0]
        bar.indicatorTextLabel.font = UIFont(name: "HelveticaNeue-Light", size: 9)
        bar.hideGloss = true
        return bar
    }()
    /// 百分比标签
    private lazy var percentLabel: UILabel = {
        let label = UILabel(
            text: "",
            fontSize: 10,
            textColor: MalaColor_97A8BB_0
        )
        label.textAlignment = .Right
        return label
    }()
    
    
    // MARK: - Instance Method
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUserInterface()
    }
    
    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // SubViews
        self.addSubview(titleLabel)
        self.addSubview(progressBar)
        self.addSubview(percentLabel)
        
        // AutoLayout
        titleLabel.snp_makeConstraints { (make) in
            make.width.equalTo(45)
            make.left.equalTo(self.snp_left)
            make.right.equalTo(progressBar.snp_left).offset(-8)
            make.height.equalTo(self.snp_height)
            make.centerY.equalTo(self.snp_centerY)
        }
        progressBar.snp_makeConstraints { (make) in
            make.centerY.equalTo(self.snp_centerY)
            make.left.equalTo(titleLabel.snp_right).offset(8)
            make.right.equalTo(percentLabel.snp_left).offset(-8)
            make.height.equalTo(self.snp_height)
        }
        percentLabel.snp_makeConstraints { (make) in
            make.width.equalTo(25)
            make.right.equalTo(self.snp_right)
            make.left.equalTo(progressBar.snp_right).offset(8)
            make.height.equalTo(self.snp_height)
            make.centerY.equalTo(self.snp_centerY)
        }
    }
}


public class ThemeHorizontalBarData: NSObject {
    
    // MARK: - Property
    var title: String = ""
    var color: UIColor = UIColor.whiteColor()
    var rightNum: Int = 0
    var totalNum: Int = 0
    
    
    // MARK: - Instance Method
    override init() {
        super.init()
    }
    
    convenience init(title: String, color: UIColor, rightNum: Int, totalNum: Int) {
        self.init()
        self.title = title
        self.color = color
        self.rightNum = rightNum
        self.totalNum = totalNum
    }
}