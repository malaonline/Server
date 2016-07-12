//
//  TeacherDetailsPlaceCell.swift
//  mala-ios
//
//  Created by Elors on 1/5/16.
//  Copyright © 2016 Mala Online. All rights reserved.
//

import UIKit

class TeacherDetailsPlaceCell: TeacherDetailBaseCell {

    // MARK: - Property
    var schools: [SchoolModel]? {
        didSet {
            tableView.model = schools ?? []
        }
    }
    var isOpen: Bool = false {
        didSet {
            tableView.isOpen = isOpen
            if isOpen {
                button.selected = true
                let height = (Int(MalaLayout_DetailSchoolsTableViewCellHeight) * schools!.count)
                tableView.snp_updateConstraints { (make) -> Void in
                    make.height.equalTo(height)
                }
            }else {
                button.selected = false
                tableView.snp_updateConstraints { (make) -> Void in
                    make.height.equalTo(MalaLayout_DetailSchoolsTableViewCellHeight)
                }
            }
        }
    }
    
    
    // MARK: - Components
    private lazy var tableView: TeacherDetailsSchoolsTableView = {
        let tableView = TeacherDetailsSchoolsTableView(frame: CGRectZero, style: .Plain)
        return tableView
    }()
    private lazy var button: UIButton = {
        let button = UIButton()
        button.titleLabel?.font = UIFont.systemFontOfSize(15)
        button.setTitleColor(MalaColor_636363_0, forState: .Normal)
        button.setTitle("距您最近社区中心", forState: .Normal)
        button.setTitle("收起教学环境列表", forState: .Selected)
        button.setImage(UIImage(named: "dropArrow"), forState: .Normal)
        button.setImage(UIImage(named: "upArrow"), forState: .Selected)
        button.addTarget(self, action: #selector(TeacherDetailsPlaceCell.buttonDidTap), forControlEvents: .TouchUpInside)
        button.titleEdgeInsets = UIEdgeInsets(top: 0, left: -13, bottom: 0, right: 13)
        button.imageEdgeInsets = UIEdgeInsets(top: 0, left: 128, bottom: 0, right: -128)
        return button
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
        content.addSubview(button)
        
        // Autolayout
        content.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(self.titleLabel.snp_bottom)
            make.bottom.equalTo(self.contentView.snp_bottom)
        }
        tableView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top)
            make.left.equalTo(self.content.snp_left)
            make.bottom.equalTo(self.button.snp_top)
            make.right.equalTo(self.content.snp_right)
        }
        button.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.tableView.snp_bottom)
            make.left.equalTo(self.content.snp_left)
            make.bottom.equalTo(self.content.snp_bottom)
            make.right.equalTo(self.content.snp_right)
            make.height.equalTo(38)
        }
    }
    
    // MARK: - Event Response
    @objc private func buttonDidTap() {
        // 发送通知，刷新 [教师详情页面] 并展开Cell
        NSNotificationCenter.defaultCenter().postNotificationName(MalaNotification_OpenSchoolsCell, object: !isOpen)
    }
}