//
//  TeacherDetailsPlaceCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailsPlaceCell: MalaBaseCell {

    // MARK: - Property
    var schools: [SchoolModel]? {
        didSet {
            tableView.model = schools ?? []
            tableView.snp_updateConstraints { (make) -> Void in
                make.height.equalTo(152)
            }
        }
    }
    var isOpen: Bool = false {
        didSet {
            if isOpen {
                tableView.snp_updateConstraints { (make) -> Void in
                    make.height.equalTo(Int(MalaLayout_DetailSchoolsTableViewCellHeight) * schools!.count)
                }
            }
            tableView.isOpen = isOpen
        }
    }
    
    
    // MARK: - Components
    private lazy var tableView: TeacherDetailsSchoolsTableView = {
        let tableView = TeacherDetailsSchoolsTableView(frame: CGRectZero, style: .Plain)
        return tableView
    }()
    
    
    // MARK: - Constructed
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
        content.addSubview(tableView)
        
        // Autolayout
        // Remove margin
        content.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(self.title.snp_bottom)
            make.bottom.equalTo(self.contentView.snp_bottom)
        }
        
        tableView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top)
            make.left.equalTo(self.content.snp_left)
            make.bottom.equalTo(self.content.snp_bottom)
            make.right.equalTo(self.content.snp_right)
        }
    }
}
