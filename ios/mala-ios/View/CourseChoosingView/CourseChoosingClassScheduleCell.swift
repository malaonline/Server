//
//  CourseChoosingClassScheduleCell.swift
//  mala-ios
//
//  Created by 王新宇 on 1/22/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CourseChoosingClassScheduleCell: MalaBaseCell {

    // MARK: - Property
    /// 课程表数据模型
    var classScheduleModel: [[ClassScheduleDayModel]] = [] {
        didSet {
            self.classSchedule.model = classScheduleModel
        }
    }
    
    
    // MARK: - Components
    private lazy var classSchedule: ThemeClassSchedule = {
        let frame = CGRect(x: 0, y: 0, width: MalaLayout_CardCellWidth, height: MalaLayout_CardCellWidth*0.65)
        let classSchedule = ThemeClassSchedule(frame: frame, collectionViewLayout: ThemeClassScheduleFlowLayout(frame: frame))
        classSchedule.bounces = false
        classSchedule.scrollEnabled = false
        return classSchedule
    }()
    private lazy var legendView: LegendView = {
        let legendView = LegendView()
        return legendView
    }()
    
    
    // MARK: - Contructed
    override init(style: UITableViewCellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        setupUserInterface()
        setupLegends()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // Style
        adjustForCourseChoosing()
        
        // SubViews
        content.addSubview(classSchedule)
        content.addSubview(legendView)
        
        // Autolayout
        content.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(headerView.snp_bottom).offset(14)
        }
        classSchedule.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(content.snp_top)
            make.left.equalTo(content.snp_left)
            make.right.equalTo(content.snp_right)
            make.height.equalTo(classSchedule.snp_width).multipliedBy(0.65)
        }
        legendView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(classSchedule.snp_bottom).offset(14)
            make.left.equalTo(content.snp_left)
            make.height.equalTo(15)
            make.right.equalTo(content.snp_right)
            make.bottom.equalTo(content.snp_bottom)
        }
    }
    
    private func setupLegends() {
        legendView.addLegend(image: "legend_active", title: "可选")
        legendView.addLegend(image: "legend_disabled", title: "已售")
        legendView.addLegend(image: "legend_selected", title: "已选")
        let buttonBought = legendView.addLegend(image: "legend_bought", title: "已买")
        let ButtonDesc = legendView.addLegend(image: "desc_icon", title: "")
        buttonBought.addTarget(self, action: #selector(CourseChoosingClassScheduleCell.showBoughtDescription), forControlEvents: .TouchUpInside)
        ButtonDesc.addTarget(self, action: #selector(CourseChoosingClassScheduleCell.showBoughtDescription), forControlEvents: .TouchUpInside)
    }
    
    
    // MARK: - Events Response
    @objc private func showBoughtDescription() {
        CouponRulesPopupWindow(title: "已买课程", desc: MalaConfig.boughtDescriptionString()).show()
    }
}


// MARK: - LegendView
public class LegendView: UIView {
    
    // MARK: - Property
    private var currentX: CGFloat = 6
    
    
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
        button.imageEdgeInsets = UIEdgeInsets(top: 0, left: -6, bottom: 0, right: 6)
        
        button.setTitle(title, forState: .Normal)
        button.titleLabel?.font = UIFont.systemFontOfSize(12)
        button.setTitleColor(MalaColor_939393_0, forState: .Normal)
        
        button.sizeToFit()
        button.frame.origin.x = (currentX == 6 ? currentX : currentX+12)
        addSubview(button)
        currentX = CGRectGetMaxX(button.frame)
        
        return button
    }
}