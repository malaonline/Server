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
        let frame = CGRect(x: 0, y: 0, width: MalaLayout_CardCellWidth, height: MalaLayout_CardCellWidth*0.66)
        let classSchedule = ThemeClassSchedule(frame: frame, collectionViewLayout: ThemeClassScheduleFlowLayout(frame: frame))
        return classSchedule
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
        // SubViews
        content.addSubview(classSchedule)
        
        // Autolayout
        // Remove margin
        content.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(self.title.snp_bottom)
            make.bottom.equalTo(self.contentView.snp_bottom)
        }
        
        classSchedule.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top).offset(MalaLayout_Margin_14)
            make.left.equalTo(self.content.snp_left)
            make.bottom.equalTo(self.content.snp_bottom).offset(-MalaLayout_Margin_14)
            make.right.equalTo(self.content.snp_right)
            make.height.equalTo(MalaLayout_CardCellWidth*0.66)
        }
    }
}
