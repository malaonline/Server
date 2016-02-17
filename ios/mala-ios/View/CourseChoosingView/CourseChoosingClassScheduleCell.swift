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
        // SubViews
        content.addSubview(classSchedule)
        content.addSubview(legendView)
        
        // Autolayout
        // Remove margin
        content.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(self.title.snp_bottom)
            make.bottom.equalTo(self.contentView.snp_bottom)
        }
        
        classSchedule.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top).offset(MalaLayout_Margin_14)
            make.left.equalTo(self.content.snp_left)
            make.right.equalTo(self.content.snp_right)
            make.height.equalTo(MalaLayout_CardCellWidth*0.65)
        }
        legendView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.classSchedule.snp_bottom).offset(MalaLayout_Margin_14)
            make.left.equalTo(classSchedule.snp_left)
            make.height.equalTo(MalaLayout_Margin_13)
            make.right.equalTo(classSchedule.snp_right)
            make.bottom.equalTo(self.content.snp_bottom).offset(-MalaLayout_Margin_14)
        }
    }
    
    private func setupLegends() {
        legendView.addLegend(image: "legend_disabled", title: "已被约课")
        legendView.addLegend(image: "legend_active", title: "可授课")
        legendView.addLegend(image: "legend_selected", title: "已选课时")
    }
}


// MARK: - LegendView
public class LegendView: UIView {
    
    // MARK: - Property
    private var currentX: CGFloat = MalaLayout_Margin_6
    
    
    // MARK: - Constructed
    override init(frame: CGRect) {
        super.init(frame: frame)
    }

    required public init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
 
    public func addLegend(image imageName: String, title: String) {
        let button = UIButton()
        button.userInteractionEnabled = false
        
        button.setImage(UIImage(named: imageName), forState: .Normal)
        button.imageEdgeInsets = UIEdgeInsets(top: 0, left: -MalaLayout_Margin_6, bottom: 0, right: MalaLayout_Margin_6)
        
        button.setTitle(title, forState: .Normal)
        button.titleLabel?.font = UIFont.systemFontOfSize(MalaLayout_FontSize_12)
        button.setTitleColor(MalaDetailsCellSubTitleColor, forState: .Normal)
        
        button.sizeToFit()
        button.frame.origin.x = (currentX == MalaLayout_Margin_6 ? currentX : currentX+MalaLayout_Margin_12)
        addSubview(button)
        currentX = CGRectGetMaxX(button.frame)
    }
}