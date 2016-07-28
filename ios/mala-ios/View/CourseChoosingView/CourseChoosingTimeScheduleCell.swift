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
            if (timeSchedules ?? []) !== (oldValue ?? []) && timeSchedules != nil && isOpen {
                parseTimeSchedules()
            }
        }
    }
    /// 展开标记
    var isOpen: Bool = true {
        didSet {
            
            guard let timeLineView = timeLineView else {
                return
            }
            
            if isOpen {
                timeLineView.hidden = false
                timeLineView.snp_updateConstraints { (make) -> Void in
                    make.height.equalTo(currentHeight)
                }
            }else {
                timeLineView.hidden = true
                timeLineView.snp_updateConstraints { (make) -> Void in
                    make.height.equalTo(0)
                }
            }
            detailButton.selected = isOpen
        }
    }
    /// 当前高度
    var currentHeight: CGFloat = 0
    
    
    // MARK: - Components
    /// 上课时间表控件
    private var timeLineView: ThemeTimeLine?
    /// 展开按钮
    private lazy var detailButton: UIButton = {
        let button = UIButton()
        button.setImage(UIImage(named: "dropArrow"), forState: .Normal)
        button.setImage(UIImage(named: "upArrow"), forState: .Selected)
        button.addTarget(self, action: #selector(CourseChoosingTimeScheduleCell.detailButtonDidTap), forControlEvents: .TouchUpInside)
        return button
    }()
    
    
    // MARK: - Contructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        setupUserInterface()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        adjustForCourseChoosing()
        
        // SubView
        headerView.addSubview(detailButton)
        
        // Autolayout
        content.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(headerView.snp_bottom).offset(14)
        }
        detailButton.snp_makeConstraints { (make) -> Void in
            make.height.equalTo(13)
            make.right.equalTo(headerView.snp_right).offset(-12)
            make.centerY.equalTo(headerView.snp_centerY)
        }
    }
    
    private func parseTimeSchedules() {
        
        // 解析时间表数据
        let result = parseTimeSlots((self.timeSchedules ?? []))
        
        // 设置UI
        self.timeLineView?.removeFromSuperview()
        self.timeLineView = ThemeTimeLine(times: result.dates, descs: result.times)
        content.addSubview(timeLineView!)
        currentHeight = result.height
        timeLineView!.snp_makeConstraints { (make) in
            make.top.equalTo(content.snp_top)
            make.left.equalTo(content.snp_left)
            make.right.equalTo(content.snp_right)
            make.bottom.equalTo(content.snp_bottom)
            make.height.equalTo(currentHeight)
        }
    }
    
    
    // MARK: - Override
    @objc func detailButtonDidTap() {
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_OpenTimeScheduleCell, object: !isOpen)
    }
}