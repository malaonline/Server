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
    var timeScheduleResult: [String]? {
        didSet {
            self.tableView.snp_updateConstraints { (make) -> Void in
                make.height.equalTo(Int(MalaLayout_FontSize_28)*timeScheduleResult!.count)
            }
            tableView.timeSchedule = self.timeScheduleResult
            tableView.reloadData()
        }
    }
    
    // MARK: - Components
    private lazy var tableView: TimeScheduleCellTableView = {
        let tableView = TimeScheduleCellTableView()
        tableView.bounces = false
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
    
    
    // MARK: - Public Method
    func setupTableView () {
        tableView.timeSchedule = self.timeScheduleResult
        tableView.reloadData()
    }
    
    
    // MARK: - Private Method
    private func setupUserInterface() {
        // SubViews
        content.addSubview(tableView)
        
        // Autolayout
        content.snp_updateConstraints { (make) -> Void in
            make.top.equalTo(title.snp_bottom).offset(MalaLayout_FontSize_14/2)
            make.bottom.equalTo(contentView.snp_bottom).offset(-MalaLayout_FontSize_14/2)
        }
        
        tableView.snp_makeConstraints { (make) -> Void in
            make.top.equalTo(self.content.snp_top)
            make.bottom.equalTo(self.content.snp_bottom)
            make.left.equalTo(self.content.snp_left)
            make.right.equalTo(self.content.snp_right)
            make.height.equalTo(0)
        }
    }
}


private let TimeScheduleCellTableViewCellReuseId = "TimeScheduleCellTableViewCellReuseId"
class TimeScheduleCellTableView: UITableView, UITableViewDelegate, UITableViewDataSource {
    
    // MARK: - Property
    var timeSchedule: [String]?
    
    
    // MARK: - Constructed
    override init(frame: CGRect, style: UITableViewStyle) {
        super.init(frame: frame, style: style)
        
        configura()
    }

    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    // MARK: - Private Method
    private func configura() {
        delegate = self
        dataSource = self
        registerClass(UITableViewCell.self, forCellReuseIdentifier: TimeScheduleCellTableViewCellReuseId)
        
        self.separatorStyle = .None
    }
    
    // MARK: - Delegate
    func tableView(tableView: UITableView, heightForRowAtIndexPath indexPath: NSIndexPath) -> CGFloat {
        return MalaLayout_FontSize_28
    }
    
    func tableView(tableView: UITableView, shouldHighlightRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        return false
    }

    
    // MARK: - DataSource
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return timeSchedule?.count ?? 0
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier(TimeScheduleCellTableViewCellReuseId, forIndexPath: indexPath)
        
        // 设置label
        let label = UILabel()
        label.frame = CGRect(x: 0, y: MalaLayout_FontSize_14/2, width: 0, height: 0)
        label.font = UIFont(name: "Courier New", size: MalaLayout_FontSize_14)
        label.textColor = MalaAppearanceTextColor
        label.text = timeSchedule?[indexPath.row]
        label.sizeToFit()
        cell.contentView.addSubview(label)
        return cell
    }
}
