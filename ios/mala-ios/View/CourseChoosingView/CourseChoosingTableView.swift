//
//  CourseChoosingTableView.swift
//  mala-ios
//
//  Created by 王新宇 on 1/22/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

private let CourseChoosingCellReuseId = [
    0: "CourseChoosingGradeCellReuseId",            // 选择授课年级
    1: "CourseChoosingPlaceCellReuseId",            // 选择上课地点
    2: "CourseChoosingClassScheduleCellReuseId",    // 选择上课时间（课程表）
    3: "CourseChoosingClassPeriodCellReuseId",      // 选择课时
    4: "CourseChoosingTimeScheduleCellReuseId",     // 上课时间表
    5: "CourseChoosingOtherServiceCellReuseId"      // 其他服务
]

class CourseChoosingTableView: UITableView {

    // MARK: - Property
    
    
    
    // MARK: - Constructed
    override init(frame: CGRect, style: UITableViewStyle) {
        super.init(frame: frame, style: style)
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
}
