//
//  CourseChoosingTimeScheduleCell.swift
//  mala-ios
//
//  Created by 王新宇 on 1/22/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CourseChoosingTimeScheduleCell: MalaBaseCell {

    // MARK: - Components
    private lazy var legendView: PeriodStepper = {
        let legendView = PeriodStepper()
        return legendView
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
        content.removeFromSuperview()
        contentView.addSubview(legendView)
        
        // Autolayout
        // Remove margin
        title.snp_updateConstraints { (make) -> Void in
            make.bottom.equalTo(self.contentView.snp_bottom).offset(-MalaLayout_Margin_16)
        }
        
        legendView.snp_makeConstraints { (make) -> Void in
            make.width.equalTo(97)
            make.height.equalTo(27)
            make.centerY.equalTo(self.title.snp_centerY)
            make.right.equalTo(self.contentView.snp_right).offset(-MalaLayout_Margin_12)
        }
    }
}
