//
//  CourseChoosingTimeScheduleCell.swift
//  mala-ios
//
//  Created by 王新宇 on 1/22/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CourseChoosingTimeScheduleCell: MalaBaseCell {

    // MARK: - Property
    /// 上课时间列表
    var timeSchedules: [[NSTimeInterval]]? {
        didSet {
            println("Time-newValue: \(timeSchedules)")
            println("Time-oldValue: \(oldValue)")
            
            if (timeSchedules ?? []) != (oldValue ?? []) && timeSchedules != nil {
                parseTimeSchedules()
            }
        }
    }
    /// 展开标记
    var isOpen: Bool = false {
        didSet {
            
            guard let timeLineView = timeLineView else {
                return
            }
            println("时间表 展开")
            if isOpen {
                timeLineView.snp_updateConstraints { (make) -> Void in
                    make.height.equalTo(currentHeightCount*16)
                }
            }else {
                timeLineView.snp_updateConstraints { (make) -> Void in
                    make.height.equalTo(0)
                }
            }
            dropArrow.selected = isOpen
        }
    }
    /// 当前高度
    var currentHeightCount: Int = 0
    
    
    // MARK: - Components
    /// 上课时间表控件
    private var timeLineView: ThemeTimeLine?
    
    
    // MARK: - Contructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }

    
    // MARK: - Override
    ///  cell点击事件
    func cellDidTap() {
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_OpenTimeScheduleCell, object: !isOpen)
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        accessory = .DropArrow
        dropArrow.userInteractionEnabled = false
        
        // SubViews
        
        // Autolayout
    }
    
    private func parseTimeSchedules() {
        
        // 解析时间表数据
        let result = parseTimeSlots((self.timeSchedules ?? []))
        
        // 设置UI
        self.timeLineView?.removeFromSuperview()
        self.timeLineView = ThemeTimeLine(times: result.dates, descs: result.times)
        content.addSubview(timeLineView!)
        currentHeightCount = result.heightCount
        timeLineView!.snp_makeConstraints { (make) in
            make.top.equalTo(content.snp_top)
            make.left.equalTo(content.snp_left)
            make.right.equalTo(content.snp_right)
            make.bottom.equalTo(content.snp_bottom)
            make.height.equalTo(currentHeightCount*16)
        }
    }
}