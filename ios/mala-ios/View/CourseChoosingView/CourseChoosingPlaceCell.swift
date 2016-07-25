//
//  CourseChoosingPlaceCell.swift
//  mala-ios
//
//  Created by 王新宇 on 1/22/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class CourseChoosingPlaceCell: MalaBaseCell {

    // MARK: - Property
    var schools: [SchoolModel]? {
        didSet {
            tableView.schools = schools!
        }
    }
    var isOpen: Bool = false {
        didSet {
            tableView.isOpen = isOpen
            if isOpen {
                tableView.snp_updateConstraints { (make) -> Void in
                    make.height.equalTo(Int(64) * schools!.count)
                }
            }else {
                tableView.snp_updateConstraints { (make) -> Void in
                    make.height.equalTo(64+38)
                }
            }
        }
    }
    var selectedIndexPath: NSIndexPath? {
        didSet {
            if selectedIndexPath != nil {
                tableView.selectedIndexPath = selectedIndexPath!
            }
        }
    }
    
    
    // MARK: - Components
    private lazy var tableView: CourseChoosingPlaceTableView = {
        let tableView = CourseChoosingPlaceTableView(frame: CGRectZero, style: .Plain)
        return tableView
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
        adjustForCourseChoosing()
        content.addSubview(tableView)
        
        // Autolayout
        content.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(headerView.snp_bottom)
            make.bottom.equalTo(contentView.snp_bottom)
        }
        
        tableView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(content.snp_top)
            make.left.equalTo(content.snp_left)
            make.bottom.equalTo(content.snp_bottom)
            make.right.equalTo(content.snp_right)
        }
    }
    
    func tableViewReloadData() {
        self.tableView.reloadData()
    }
}